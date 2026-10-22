package com.greenbox.shop.repository;

import com.greenbox.shop.domain.product.Product;
import com.greenbox.shop.domain.product.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findTop4ByStatusOrderByIdDesc(ProductStatus status);

    List<Product> findTop4ByStatusOrderByViewCountDesc(ProductStatus status);

    List<Product> findTop4ByStatusAndDifficultyIsNotNullOrderByIdAsc(ProductStatus status);

    List<Product> findByStockLessThanAndStatusNotOrderByStockAsc(int stock, ProductStatus status);

    Page<Product> findByNameContainingOrderByIdDesc(String keyword, Pageable pageable);
}
