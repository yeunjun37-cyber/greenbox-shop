package com.greenbox.shop.config;

import com.greenbox.shop.domain.member.Member;
import com.greenbox.shop.domain.member.Role;
import com.greenbox.shop.domain.order.*;
import com.greenbox.shop.domain.product.*;
import com.greenbox.shop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.greenbox.shop.domain.product.Difficulty.*;
import static com.greenbox.shop.domain.product.Light.*;

/**
 * 최초 실행 시 기본 데이터 생성 (DB가 비어 있을 때만)
 * - 관리자: admin@greenbox.com / admin1234
 * - 회원:   chorok@greenbox.com / greenbox1234
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (memberRepository.count() > 0) return;

        Member admin = memberRepository.save(Member.create("admin@greenbox.com", passwordEncoder.encode("admin1234"),
                "관리자", "010-0000-0000", null, null, null, Role.ADMIN));
        Member user = memberRepository.save(Member.create("chorok@greenbox.com", passwordEncoder.encode("greenbox1234"),
                "김초록", "010-1234-5678", "22004", "인천광역시 연수구 송도과학로 123", "101동 1004호", Role.USER));

        Category foliage = categoryRepository.save(Category.create("관엽식물", null, 1));
        Category succulent = categoryRepository.save(Category.create("다육·선인장", null, 2));
        Category supplies = categoryRepository.save(Category.create("화분·용품", null, 3));

        Product monstera = plant(foliage, "몬스테라 델리시오사", 29000, 34, EASY, MID, 7, false, "monstera",
                "구멍 난 큰 잎이 매력적인 대표 인테리어 식물. 성장이 빠르고 적응력이 좋아 초보자에게 추천합니다.");
        Product stucky = plant(succulent, "스투키", 18000, 20, EASY, LOW, 21, false, "tall",
                "물을 자주 주지 않아도 되는 공기정화 식물. 그늘진 실내에서도 잘 자랍니다.");
        plant(foliage, "올리브나무", 42000, 8, NORMAL, HIGH, 5, true, "tree",
                "햇빛을 좋아하는 지중해 나무. 창가 자리에 두면 은빛 잎을 오래 볼 수 있어요.");
        Product pilea = plant(foliage, "필레아 페페", 15000, 25, EASY, MID, 7, true, "round",
                "동전 모양의 동글동글한 잎. 반려동물에게 안전한 식물입니다.");
        plant(foliage, "아레카야자", 38000, 12, NORMAL, MID, 5, true, "palm",
                "시원하게 뻗은 잎이 이국적인 분위기를 만드는 야자. 실내 습도 조절에 도움을 줍니다.");
        plant(foliage, "칼라데아 오르비폴리아", 22000, 15, HARD, LOW, 4, true, "calathea",
                "은빛 줄무늬 잎이 아름다운 식물. 습도 관리가 필요해 경험자에게 추천합니다.");
        plant(foliage, "스킨답서스", 12000, 40, EASY, LOW, 7, false, "vine",
                "늘어지며 자라는 덩굴 식물. 행잉 화분이나 선반 위에 두기 좋습니다.");
        plant(foliage, "테이블야자", 13000, 30, EASY, LOW, 7, true, "palm",
                "작은 크기로 책상 위에 두기 좋은 야자. 반려동물에게 안전합니다.");
        plant(succulent, "에케베리아", 8000, 50, EASY, HIGH, 14, true, "succulent",
                "장미꽃 모양의 다육식물. 햇빛이 잘 드는 곳에서 물은 아주 가끔만 주세요.");
        plant(foliage, "금전수", 26000, 9, EASY, LOW, 21, false, "zz",
                "윤기 나는 잎의 개업·집들이 선물 1순위 식물. 건조에 강합니다.");
        productRepository.save(Product.create(supplies, "토분 15cm", 8000, 120,
                "통기성이 좋은 이탈리아산 토분. 과습을 막아 줍니다.", null, null, null, false, "/img/plants/pot.svg"));
        productRepository.save(Product.create(supplies, "분갈이 흙 2L", 4500, 56,
                "배수가 잘 되는 실내 식물 전용 배합토.", null, null, null, false, "/img/plants/soil.svg"));

        // 리뷰 작성 시연용: 배송완료된 지난 주문 1건
        String no = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-0001";
        Order past = Order.create(no, user, user.getName(), user.getPhone(), user.getZipcode(),
                user.getAddress() + " " + user.getAddressDetail(), "문 앞에 놓아 주세요");
        past.addItem(OrderItem.create(stucky, 1));
        past.addShippingFee(3000);
        orderRepository.save(past);
        past.pay(Payment.create(past, PaymentMethod.CARD));
        past.changeStatus(OrderStatus.DELIVERED);

        log.info("기본 데이터 생성 완료 - 관리자 admin@greenbox.com / admin1234, 회원 chorok@greenbox.com / greenbox1234");
    }

    private Product plant(Category c, String name, int price, int stock, Difficulty d, Light l, int water,
                          boolean petSafe, String img, String desc) {
        return productRepository.save(Product.create(c, name, price, stock, desc, d, l, water, petSafe, "/img/plants/" + img + ".svg"));
    }
}
