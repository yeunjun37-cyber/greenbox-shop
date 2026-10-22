package com.greenbox.shop.service;

import com.greenbox.shop.domain.board.Review;
import com.greenbox.shop.domain.order.OrderItem;
import com.greenbox.shop.domain.order.OrderStatus;
import com.greenbox.shop.exception.BusinessException;
import com.greenbox.shop.exception.NotFoundException;
import com.greenbox.shop.repository.MemberRepository;
import com.greenbox.shop.repository.OrderItemRepository;
import com.greenbox.shop.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final MemberRepository memberRepository;
    private final FileStorageService fileStorageService;

    /** F-18 리뷰 작성: 배송완료된 본인 주문 상품, 1건당 1회 */
    @Transactional
    public Long write(Long memberId, Long orderItemId, int rating, String content, MultipartFile image) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("주문 상품을 찾을 수 없습니다."));
        if (!item.getOrder().getMember().getId().equals(memberId)) {
            throw new BusinessException("본인이 구매한 상품만 리뷰를 작성할 수 있습니다.");
        }
        if (item.getOrder().getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessException("배송완료된 상품만 리뷰를 작성할 수 있습니다.");
        }
        if (reviewRepository.existsByOrderItemId(orderItemId)) {
            throw new BusinessException("이미 리뷰를 작성한 상품입니다.");
        }
        if (content == null || content.isBlank()) {
            throw new BusinessException("리뷰 내용을 입력하세요.");
        }
        String img = fileStorageService.store(image, "reviews");
        return reviewRepository.save(Review.create(memberRepository.getReferenceById(memberId), item, rating, content, img)).getId();
    }

    public List<Review> byProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByIdDesc(productId);
    }

    public List<Review> byMember(Long memberId) {
        return reviewRepository.findByMemberIdOrderByIdDesc(memberId);
    }

    public double average(Long productId) {
        Double avg = reviewRepository.averageRating(productId);
        return avg == null ? 0 : Math.round(avg * 10) / 10.0;
    }

    public boolean written(Long orderItemId) {
        return reviewRepository.existsByOrderItemId(orderItemId);
    }
}
