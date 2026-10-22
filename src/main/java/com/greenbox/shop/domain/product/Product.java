package com.greenbox.shop.domain.product;

import com.greenbox.shop.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** 키우기 난이도 (화분·용품은 null) */
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Difficulty difficulty;

    /** 필요 광량 */
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Light light;

    /** 권장 물주기 주기(일) */
    private Integer waterCycle;

    /** 반려동물 안전 여부 Y/N */
    @Column(nullable = false, length = 1)
    private String petSafe = "N";

    private String thumbnail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ProductStatus status = ProductStatus.SALE;

    @Column(nullable = false)
    private int viewCount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder asc")
    private List<ProductImage> images = new ArrayList<>();

    public static Product create(Category category, String name, int price, int stock, String description,
                                 Difficulty difficulty, Light light, Integer waterCycle, boolean petSafe, String thumbnail) {
        Product p = new Product();
        p.category = category;
        p.update(category, name, price, stock, description, difficulty, light, waterCycle, petSafe);
        p.thumbnail = thumbnail;
        return p;
    }

    public void update(Category category, String name, int price, int stock, String description,
                       Difficulty difficulty, Light light, Integer waterCycle, boolean petSafe) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.difficulty = difficulty;
        this.light = light;
        this.waterCycle = waterCycle;
        this.petSafe = petSafe ? "Y" : "N";
        if (status != ProductStatus.HIDDEN) {
            this.status = stock > 0 ? ProductStatus.SALE : ProductStatus.SOLDOUT;
        }
    }

    public void changeThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void addImage(String url) {
        images.add(ProductImage.create(this, url, images.size()));
    }

    public void changeStatus(ProductStatus status) {
        this.status = status;
    }

    /** 주문 시 재고 차감 */
    public void removeStock(int quantity) {
        if (stock < quantity) {
            throw new BusinessException("'" + name + "' 상품의 재고가 부족합니다. (남은 재고: " + stock + "개)");
        }
        stock -= quantity;
        if (stock == 0 && status == ProductStatus.SALE) {
            status = ProductStatus.SOLDOUT;
        }
    }

    /** 주문 취소 시 재고 복구 */
    public void addStock(int quantity) {
        stock += quantity;
        if (status == ProductStatus.SOLDOUT && stock > 0) {
            status = ProductStatus.SALE;
        }
    }

    public void increaseViewCount() {
        viewCount++;
    }

    public boolean isPetSafe() {
        return "Y".equals(petSafe);
    }

    public boolean isPlant() {
        return difficulty != null;
    }

    public boolean isOnSale() {
        return status == ProductStatus.SALE;
    }
}
