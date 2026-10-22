# 🌿 그린박스 GreenBox — 반려식물 큐레이션 & 관리 쇼핑몰

> [프로젝트Ⅰ] Spring Framework를 활용한 쇼핑몰 제작 · 1팀 **인연준** (1인 프로젝트)

식물을 ‘사는 것’에서 끝내지 않고 ‘잘 키우는 것’까지 함께하는 쇼핑몰입니다.
키우기 난이도 · 필요 광량 · 반려동물 안전 여부로 식물을 고르고, 구매한 식물을 **내 식물**로 등록하면 다음 물주기 날짜를 D-day로 알려 줍니다.

## ERD

![ERD](docs/erd.png)

13개 테이블: `member`, `category`, `product`, `product_image`, `wishlist`, `cart_item`, `orders`, `order_item`, `payment`, `review`, `qna`, `notice`, `my_plant` — 상세는 [docs/schema.sql](docs/schema.sql) 참고.

