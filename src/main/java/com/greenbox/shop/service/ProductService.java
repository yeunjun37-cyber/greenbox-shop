package com.greenbox.shop.service;

import com.greenbox.shop.domain.product.*;
import com.greenbox.shop.dto.ProductForm;
import com.greenbox.shop.dto.ProductSearch;
import com.greenbox.shop.exception.NotFoundException;
import com.greenbox.shop.repository.CategoryRepository;
import com.greenbox.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.greenbox.shop.repository.ProductSpecs.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    public List<Category> categories() {
        return categoryRepository.findAllByOrderBySortOrderAsc();
    }

    /** F-06 메인 화면 */
    public List<Product> newest() {
        return productRepository.findTop4ByStatusOrderByIdDesc(ProductStatus.SALE);
    }

    public List<Product> best() {
        return productRepository.findTop4ByStatusOrderByViewCountDesc(ProductStatus.SALE);
    }

    public List<Product> beginnerPicks() {
        return productRepository.findTop4ByStatusAndDifficultyIsNotNullOrderByIdAsc(ProductStatus.SALE).stream()
                .filter(p -> p.getDifficulty() == Difficulty.EASY).toList();
    }

    /** F-07 ~ F-09 상품 목록 · 맞춤 필터 · 검색 */
    public Page<Product> search(ProductSearch s) {
        Sort sort = switch (s.getSort()) {
            case "low" -> Sort.by("price").ascending();
            case "high" -> Sort.by("price").descending();
            case "popular" -> Sort.by("viewCount").descending();
            default -> Sort.by("id").descending();
        };
        Specification<Product> spec = Specification.where(visible())
                .and(category(s.getCategoryId()))
                .and(keyword(s.getKeyword()))
                .and(difficultyIn(s.getDifficulty()))
                .and(lightIn(s.getLight()))
                .and(petSafeOnly(s.isPetSafe()));
        return productRepository.findAll(spec, PageRequest.of(Math.max(0, s.getPage()), 9, sort));
    }

    public Product get(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다."));
    }

    /** F-10 상품 상세 (조회수 증가) */
    @Transactional
    public Product view(Long id) {
        Product p = get(id);
        p.increaseViewCount();
        return p;
    }

    // ===== 관리자 F-24 =====

    public Page<Product> adminList(String keyword, int page) {
        return productRepository.findByNameContainingOrderByIdDesc(keyword == null ? "" : keyword, PageRequest.of(page, 10));
    }

    @Transactional
    public Long save(ProductForm f) {
        Category category = categoryRepository.findById(f.getCategoryId())
                .orElseThrow(() -> new NotFoundException("카테고리를 찾을 수 없습니다."));
        String uploaded = fileStorageService.store(f.getThumbnailFile(), "products");
        if (f.getId() == null) {
            Product p = Product.create(category, f.getName(), f.getPrice(), f.getStock(), f.getDescription(),
                    f.getDifficulty(), f.getLight(), f.getWaterCycle(), f.isPetSafe(),
                    uploaded != null ? uploaded : "/img/plants/default.svg");
            return productRepository.save(p).getId();
        }
        Product p = get(f.getId());
        p.update(category, f.getName(), f.getPrice(), f.getStock(), f.getDescription(),
                f.getDifficulty(), f.getLight(), f.getWaterCycle(), f.isPetSafe());
        if (uploaded != null) p.changeThumbnail(uploaded);
        return p.getId();
    }

    /** 삭제는 주문 이력 보존을 위해 숨김 처리 */
    @Transactional
    public void hide(Long id) {
        get(id).changeStatus(ProductStatus.HIDDEN);
    }

    @Transactional
    public void changeStatus(Long id, ProductStatus status) {
        get(id).changeStatus(status);
    }

    public List<Product> lowStock() {
        return productRepository.findByStockLessThanAndStatusNotOrderByStockAsc(10, ProductStatus.HIDDEN);
    }
}
