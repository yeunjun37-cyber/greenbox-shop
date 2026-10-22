package com.greenbox.shop.repository;

import com.greenbox.shop.domain.plant.MyPlant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyPlantRepository extends JpaRepository<MyPlant, Long> {
    List<MyPlant> findByMemberIdOrderByNextWaterAtAsc(Long memberId);
}
