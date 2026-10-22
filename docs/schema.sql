-- 그린박스(GreenBox) MySQL 8.0 스키마 (테이블 정의서와 동일)
-- 실행: mysql -u root -p < docs/schema.sql
CREATE DATABASE IF NOT EXISTS greenbox DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE greenbox;

-- 회원
CREATE TABLE member (
  member_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '회원 번호',
  email VARCHAR(100) NOT NULL COMMENT '로그인 이메일(아이디)',
  password VARCHAR(255) NOT NULL COMMENT 'BCrypt 암호화 비밀번호',
  name VARCHAR(30) NOT NULL COMMENT '이름',
  phone VARCHAR(20) COMMENT '연락처',
  zipcode VARCHAR(10) COMMENT '우편번호',
  address VARCHAR(255) COMMENT '기본 주소',
  address_detail VARCHAR(255) COMMENT '상세 주소',
  role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '권한 (USER / ADMIN)',
  deleted_yn CHAR(1) NOT NULL DEFAULT 'N' COMMENT '탈퇴 여부',
  created_at DATETIME NOT NULL COMMENT '가입일시',
  updated_at DATETIME COMMENT '수정일시',
  PRIMARY KEY (member_id),
  UNIQUE KEY uk_member_email (email)
) ENGINE=InnoDB COMMENT='회원';

-- 카테고리
CREATE TABLE category (
  category_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '카테고리 번호',
  name VARCHAR(50) NOT NULL COMMENT '카테고리명',
  parent_id BIGINT COMMENT '상위 카테고리 (자기참조)',
  sort_order INT COMMENT '정렬 순서',
  PRIMARY KEY (category_id),
  CONSTRAINT fk_category_parent_id FOREIGN KEY (parent_id) REFERENCES category (category_id)
) ENGINE=InnoDB COMMENT='카테고리';

-- 상품
CREATE TABLE product (
  product_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '상품 번호',
  category_id BIGINT NOT NULL COMMENT '카테고리 번호',
  name VARCHAR(100) NOT NULL COMMENT '상품명',
  price INT NOT NULL COMMENT '판매가',
  stock INT NOT NULL COMMENT '재고 수량',
  description TEXT COMMENT '상세 설명',
  difficulty VARCHAR(10) NOT NULL COMMENT '난이도 (EASY/NORMAL/HARD)',
  light VARCHAR(10) COMMENT '필요 광량 (LOW/MID/HIGH)',
  water_cycle INT COMMENT '권장 물주기 주기(일)',
  pet_safe CHAR(1) NOT NULL DEFAULT 'N' COMMENT '반려동물 안전 여부',
  thumbnail VARCHAR(255) COMMENT '대표 이미지 경로',
  status VARCHAR(10) NOT NULL DEFAULT 'SALE' COMMENT '판매 상태 (SALE/SOLDOUT/HIDDEN)',
  view_count INT NOT NULL DEFAULT 0 COMMENT '조회수',
  created_at DATETIME NOT NULL COMMENT '등록일시',
  PRIMARY KEY (product_id),
  CONSTRAINT fk_product_category_id FOREIGN KEY (category_id) REFERENCES category (category_id)
) ENGINE=InnoDB COMMENT='상품';

-- 상품 이미지
CREATE TABLE product_image (
  image_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '이미지 번호',
  product_id BIGINT NOT NULL COMMENT '상품 번호',
  img_url VARCHAR(255) NOT NULL COMMENT '이미지 경로',
  sort_order INT COMMENT '노출 순서',
  PRIMARY KEY (image_id),
  CONSTRAINT fk_product_image_product_id FOREIGN KEY (product_id) REFERENCES product (product_id)
) ENGINE=InnoDB COMMENT='상품 이미지';

-- 찜
CREATE TABLE wishlist (
  wish_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '찜 번호',
  member_id BIGINT NOT NULL COMMENT '회원 번호',
  product_id BIGINT NOT NULL COMMENT '상품 번호',
  created_at DATETIME NOT NULL COMMENT '등록일시',
  PRIMARY KEY (wish_id),
  UNIQUE KEY uk_wishlist_member_product (member_id, product_id),
  CONSTRAINT fk_wishlist_member_id FOREIGN KEY (member_id) REFERENCES member (member_id),
  CONSTRAINT fk_wishlist_product_id FOREIGN KEY (product_id) REFERENCES product (product_id)
) ENGINE=InnoDB COMMENT='찜';

-- 장바구니
CREATE TABLE cart_item (
  cart_item_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '장바구니 항목 번호',
  member_id BIGINT NOT NULL COMMENT '회원 번호',
  product_id BIGINT NOT NULL COMMENT '상품 번호',
  quantity INT NOT NULL COMMENT '수량',
  created_at DATETIME NOT NULL COMMENT '담은 일시',
  PRIMARY KEY (cart_item_id),
  UNIQUE KEY uk_cart_item_member_product (member_id, product_id),
  CONSTRAINT fk_cart_item_member_id FOREIGN KEY (member_id) REFERENCES member (member_id),
  CONSTRAINT fk_cart_item_product_id FOREIGN KEY (product_id) REFERENCES product (product_id)
) ENGINE=InnoDB COMMENT='장바구니';

-- 주문
CREATE TABLE orders (
  order_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '주문 번호',
  order_no VARCHAR(30) NOT NULL COMMENT '주문번호 (yyyyMMdd-xxxx)',
  member_id BIGINT NOT NULL COMMENT '회원 번호',
  total_price INT NOT NULL COMMENT '총 결제 금액',
  status VARCHAR(20) NOT NULL COMMENT 'ORDERED/PAID/SHIPPING/DELIVERED/CANCELED',
  receiver_name VARCHAR(30) NOT NULL COMMENT '수령인',
  receiver_phone VARCHAR(20) NOT NULL COMMENT '수령인 연락처',
  zipcode VARCHAR(10) NOT NULL COMMENT '우편번호',
  address VARCHAR(255) NOT NULL COMMENT '배송 주소',
  memo VARCHAR(200) COMMENT '배송 요청사항',
  ordered_at DATETIME NOT NULL COMMENT '주문일시',
  PRIMARY KEY (order_id),
  UNIQUE KEY uk_orders_order_no (order_no),
  CONSTRAINT fk_orders_member_id FOREIGN KEY (member_id) REFERENCES member (member_id)
) ENGINE=InnoDB COMMENT='주문';

-- 주문 상품
CREATE TABLE order_item (
  order_item_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '주문 상품 번호',
  order_id BIGINT NOT NULL COMMENT '주문 번호',
  product_id BIGINT NOT NULL COMMENT '상품 번호',
  order_price INT NOT NULL COMMENT '주문 당시 단가',
  quantity INT NOT NULL COMMENT '수량',
  PRIMARY KEY (order_item_id),
  CONSTRAINT fk_order_item_order_id FOREIGN KEY (order_id) REFERENCES orders (order_id),
  CONSTRAINT fk_order_item_product_id FOREIGN KEY (product_id) REFERENCES product (product_id)
) ENGINE=InnoDB COMMENT='주문 상품';

-- 결제
CREATE TABLE payment (
  payment_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '결제 번호',
  order_id BIGINT NOT NULL COMMENT '주문 번호 (1:1)',
  method VARCHAR(10) NOT NULL COMMENT '결제수단 (CARD/BANK)',
  amount INT NOT NULL COMMENT '결제 금액',
  status VARCHAR(10) NOT NULL COMMENT 'PAID / CANCELED',
  paid_at DATETIME COMMENT '결제일시',
  PRIMARY KEY (payment_id),
  UNIQUE KEY uk_payment_order_id (order_id),
  CONSTRAINT fk_payment_order_id FOREIGN KEY (order_id) REFERENCES orders (order_id)
) ENGINE=InnoDB COMMENT='결제';

-- 리뷰
CREATE TABLE review (
  review_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '리뷰 번호',
  member_id BIGINT NOT NULL COMMENT '작성자',
  product_id BIGINT NOT NULL COMMENT '상품 번호',
  order_item_id BIGINT NOT NULL COMMENT '주문 상품 (1건당 1리뷰)',
  rating TINYINT NOT NULL COMMENT '별점 (1~5)',
  content TEXT NOT NULL COMMENT '리뷰 내용',
  img_url VARCHAR(255) COMMENT '첨부 사진',
  created_at DATETIME NOT NULL COMMENT '작성일시',
  PRIMARY KEY (review_id),
  UNIQUE KEY uk_review_order_item_id (order_item_id),
  CONSTRAINT fk_review_member_id FOREIGN KEY (member_id) REFERENCES member (member_id),
  CONSTRAINT fk_review_product_id FOREIGN KEY (product_id) REFERENCES product (product_id),
  CONSTRAINT fk_review_order_item_id FOREIGN KEY (order_item_id) REFERENCES order_item (order_item_id)
) ENGINE=InnoDB COMMENT='리뷰';

-- 상품 문의
CREATE TABLE qna (
  qna_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '문의 번호',
  product_id BIGINT NOT NULL COMMENT '상품 번호',
  member_id BIGINT NOT NULL COMMENT '작성자',
  title VARCHAR(100) NOT NULL COMMENT '제목',
  content TEXT NOT NULL COMMENT '문의 내용',
  secret_yn CHAR(1) NOT NULL DEFAULT 'N' COMMENT '비밀글 여부',
  answer TEXT COMMENT '관리자 답변',
  answered_at DATETIME COMMENT '답변일시',
  created_at DATETIME NOT NULL COMMENT '작성일시',
  PRIMARY KEY (qna_id),
  CONSTRAINT fk_qna_product_id FOREIGN KEY (product_id) REFERENCES product (product_id),
  CONSTRAINT fk_qna_member_id FOREIGN KEY (member_id) REFERENCES member (member_id)
) ENGINE=InnoDB COMMENT='상품 문의';

-- 공지사항
CREATE TABLE notice (
  notice_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '공지 번호',
  member_id BIGINT NOT NULL COMMENT '작성 관리자',
  title VARCHAR(100) NOT NULL COMMENT '제목',
  content TEXT NOT NULL COMMENT '내용',
  view_count INT NOT NULL DEFAULT 0 COMMENT '조회수',
  created_at DATETIME NOT NULL COMMENT '작성일시',
  PRIMARY KEY (notice_id),
  CONSTRAINT fk_notice_member_id FOREIGN KEY (member_id) REFERENCES member (member_id)
) ENGINE=InnoDB COMMENT='공지사항';

-- 내 식물
CREATE TABLE my_plant (
  my_plant_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '내 식물 번호',
  member_id BIGINT NOT NULL COMMENT '회원 번호',
  product_id BIGINT NOT NULL COMMENT '구매한 식물 상품',
  nickname VARCHAR(30) NOT NULL COMMENT '식물 별명',
  water_cycle INT NOT NULL COMMENT '물주기 주기(일, 수정 가능)',
  last_watered_at DATE COMMENT '마지막 물 준 날',
  next_water_at DATE COMMENT '다음 물주기 예정일',
  created_at DATETIME NOT NULL COMMENT '등록일시',
  PRIMARY KEY (my_plant_id),
  CONSTRAINT fk_my_plant_member_id FOREIGN KEY (member_id) REFERENCES member (member_id),
  CONSTRAINT fk_my_plant_product_id FOREIGN KEY (product_id) REFERENCES product (product_id)
) ENGINE=InnoDB COMMENT='내 식물';

