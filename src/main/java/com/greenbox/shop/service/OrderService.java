package com.greenbox.shop.service;

import com.greenbox.shop.domain.cart.CartItem;
import com.greenbox.shop.domain.member.Member;
import com.greenbox.shop.domain.order.*;
import com.greenbox.shop.dto.CartSummary;
import com.greenbox.shop.dto.OrderForm;
import com.greenbox.shop.exception.BusinessException;
import com.greenbox.shop.exception.NotFoundException;
import com.greenbox.shop.repository.CartItemRepository;
import com.greenbox.shop.repository.MemberRepository;
import com.greenbox.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final CartService cartService;

    /**
     * F-15 결제 처리 (모의 결제)
     * 주문 생성 → 재고 차감 → 결제 저장 → 장바구니 삭제를 하나의 트랜잭션으로 처리.
     * 재고 부족 등 예외 발생 시 전체 롤백.
     */
    @Transactional
    public Long order(Long memberId, OrderForm form) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        CartSummary cart = cartService.summary(memberId, form.getCartItemIds());
        if (cart.items().isEmpty()) {
            throw new BusinessException("주문할 상품이 없습니다.");
        }
        String address = form.getAddress() + (form.getAddressDetail() == null || form.getAddressDetail().isBlank() ? "" : " " + form.getAddressDetail());
        Order order = Order.create(nextOrderNo(), member, form.getReceiverName(), form.getReceiverPhone(),
                form.getZipcode(), address, form.getMemo());
        for (CartItem c : cart.items()) {
            order.addItem(OrderItem.create(c.getProduct(), c.getQuantity()));
        }
        order.addShippingFee(cart.shippingFee());
        orderRepository.save(order);
        order.pay(Payment.create(order, form.getPaymentMethod()));
        cartItemRepository.deleteAll(cart.items());
        return order.getId();
    }

    /** 주문번호: yyyyMMdd-0001 */
    private String nextOrderNo() {
        LocalDate today = LocalDate.now();
        long count = orderRepository.countByOrderedAtBetween(today.atStartOfDay(), today.plusDays(1).atStartOfDay());
        return today.format(DateTimeFormatter.BASIC_ISO_DATE) + "-" + String.format("%04d", count + 1);
    }

    public Order getOwned(Long memberId, Long orderId) {
        Order order = get(orderId);
        if (!order.getMember().getId().equals(memberId)) {
            throw new BusinessException("본인의 주문만 조회할 수 있습니다.");
        }
        return order;
    }

    public Order get(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));
    }

    /** F-16 주문 내역 */
    public List<Order> myOrders(Long memberId) {
        return orderRepository.findByMemberIdOrderByIdDesc(memberId);
    }

    /** F-17 주문 취소 (재고 복구) */
    @Transactional
    public void cancel(Long memberId, Long orderId) {
        getOwned(memberId, orderId).cancel();
    }

    // ===== 관리자 F-25 =====

    public Page<Order> adminList(OrderStatus status, int page) {
        PageRequest pr = PageRequest.of(page, 10);
        return status == null ? orderRepository.findAllByOrderByIdDesc(pr) : orderRepository.findByStatusOrderByIdDesc(status, pr);
    }

    @Transactional
    public void changeStatus(Long orderId, OrderStatus status) {
        get(orderId).changeStatus(status);
    }

    public long todayCount() {
        LocalDate t = LocalDate.now();
        return orderRepository.countByOrderedAtBetween(t.atStartOfDay(), t.plusDays(1).atStartOfDay());
    }

    public long todaySales() {
        LocalDate t = LocalDate.now();
        LocalDateTime from = t.atStartOfDay();
        return orderRepository.sumSalesBetween(from, from.plusDays(1));
    }

    public List<Order> recent() {
        return orderRepository.findTop5ByOrderByIdDesc();
    }
}
