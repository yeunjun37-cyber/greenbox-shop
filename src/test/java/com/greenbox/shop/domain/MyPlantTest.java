package com.greenbox.shop.domain;

import com.greenbox.shop.domain.plant.MyPlant;
import com.greenbox.shop.domain.product.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MyPlantTest {

    private final Product pilea = Product.create(Category.create("관엽식물", null, 1), "필레아 페페", 15000, 10, "",
            Difficulty.EASY, Light.MID, 7, true, "/img/plants/round.svg");

    @Test
    void 주기를_비워두면_상품의_권장_물주기를_사용() {
        MyPlant plant = MyPlant.create(null, pilea, "동글이", null, LocalDate.now().minusDays(3));
        assertThat(plant.getWaterCycle()).isEqualTo(7);
        assertThat(plant.getDaysLeft()).isEqualTo(4);
    }

    @Test
    void 물을_주면_다음_물주기_날짜가_재계산된다() {
        MyPlant plant = MyPlant.create(null, pilea, "동글이", 5, LocalDate.now().minusDays(6));
        assertThat(plant.isDue()).isTrue();

        plant.water(LocalDate.now());

        assertThat(plant.getNextWaterAt()).isEqualTo(LocalDate.now().plusDays(5));
        assertThat(plant.isDue()).isFalse();
    }
}
