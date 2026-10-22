package com.greenbox.shop.service;

import com.greenbox.shop.domain.plant.MyPlant;
import com.greenbox.shop.domain.product.Product;
import com.greenbox.shop.exception.BusinessException;
import com.greenbox.shop.exception.NotFoundException;
import com.greenbox.shop.repository.MemberRepository;
import com.greenbox.shop.repository.MyPlantRepository;
import com.greenbox.shop.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPlantService {

    private final MyPlantRepository myPlantRepository;
    private final OrderItemRepository orderItemRepository;
    private final MemberRepository memberRepository;

    public List<MyPlant> list(Long memberId) {
        return myPlantRepository.findByMemberIdOrderByNextWaterAtAsc(memberId);
    }

    /** 등록 가능한 식물 = 구매 이력이 있는 식물 상품 */
    public List<Product> purchasedPlants(Long memberId) {
        return orderItemRepository.findPurchasedPlants(memberId);
    }

    /** F-21 내 식물 등록 */
    @Transactional
    public Long register(Long memberId, Long productId, String nickname, Integer waterCycle, LocalDate lastWateredAt) {
        Product product = purchasedPlants(memberId).stream()
                .filter(p -> p.getId().equals(productId)).findFirst()
                .orElseThrow(() -> new BusinessException("구매한 식물만 내 식물로 등록할 수 있습니다."));
        if (nickname == null || nickname.isBlank()) {
            throw new BusinessException("식물 별명을 입력하세요.");
        }
        if (lastWateredAt != null && lastWateredAt.isAfter(LocalDate.now())) {
            throw new BusinessException("마지막 물 준 날은 오늘 이전이어야 합니다.");
        }
        MyPlant plant = MyPlant.create(memberRepository.getReferenceById(memberId), product, nickname.trim(), waterCycle, lastWateredAt);
        return myPlantRepository.save(plant).getId();
    }

    /** F-22 물주기 기록 → 다음 물주기 재계산 */
    @Transactional
    public MyPlant water(Long memberId, Long myPlantId) {
        MyPlant plant = getOwned(memberId, myPlantId);
        plant.water(LocalDate.now());
        return plant;
    }

    @Transactional
    public void delete(Long memberId, Long myPlantId) {
        myPlantRepository.delete(getOwned(memberId, myPlantId));
    }

    private MyPlant getOwned(Long memberId, Long myPlantId) {
        MyPlant plant = myPlantRepository.findById(myPlantId)
                .orElseThrow(() -> new NotFoundException("식물을 찾을 수 없습니다."));
        if (!plant.getMember().getId().equals(memberId)) {
            throw new BusinessException("본인의 식물만 관리할 수 있습니다.");
        }
        return plant;
    }
}
