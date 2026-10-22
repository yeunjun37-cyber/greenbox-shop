package com.greenbox.shop.domain;

import com.greenbox.shop.domain.product.*;
import com.greenbox.shop.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductStockTest {

    private Product product(int stock) {
        Category c = Category.create("관엽식물", null, 1);
        return Product.create(c, "몬스테라", 29000, stock, "설명", Difficulty.EASY, Light.MID, 7, false, "/img/plants/monstera.svg");
    }

    @Test
    void 주문하면_재고가_차감되고_0이면_품절() {
        Product p = product(2);
        p.removeStock(2);
        assertThat(p.getStock()).isZero();
        assertThat(p.getStatus()).isEqualTo(ProductStatus.SOLDOUT);
    }

    @Test
    void 재고보다_많이_주문하면_예외() {
        Product p = product(1);
        assertThatThrownBy(() -> p.removeStock(2)).isInstanceOf(BusinessException.class);
    }

    @Test
    void 주문_취소로_재고가_복구되면_다시_판매중() {
        Product p = product(1);
        p.removeStock(1);
        p.addStock(1);
        assertThat(p.getStatus()).isEqualTo(ProductStatus.SALE);
    }
}
