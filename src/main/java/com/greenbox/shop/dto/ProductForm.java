package com.greenbox.shop.dto;

import com.greenbox.shop.domain.product.Difficulty;
import com.greenbox.shop.domain.product.Light;
import com.greenbox.shop.domain.product.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProductForm {
    private Long id;

    @NotNull(message = "카테고리를 선택하세요.")
    private Long categoryId;

    @NotBlank(message = "상품명을 입력하세요.")
    private String name;

    @NotNull(message = "가격을 입력하세요.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    @NotNull(message = "재고를 입력하세요.")
    @Min(value = 0, message = "재고는 0개 이상이어야 합니다.")
    private Integer stock;

    private String description;
    private Difficulty difficulty;
    private Light light;
    private Integer waterCycle;
    private boolean petSafe;
    private MultipartFile thumbnailFile;
    private String thumbnail;

    public static ProductForm from(Product p) {
        ProductForm f = new ProductForm();
        f.id = p.getId();
        f.categoryId = p.getCategory().getId();
        f.name = p.getName();
        f.price = p.getPrice();
        f.stock = p.getStock();
        f.description = p.getDescription();
        f.difficulty = p.getDifficulty();
        f.light = p.getLight();
        f.waterCycle = p.getWaterCycle();
        f.petSafe = p.isPetSafe();
        f.thumbnail = p.getThumbnail();
        return f;
    }
}
