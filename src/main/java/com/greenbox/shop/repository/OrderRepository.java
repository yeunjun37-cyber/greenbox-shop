package com.greenbox.shop.repository;

import com.greenbox.shop.domain.order.Order;
import com.greenbox.shop.domain.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByMemberIdOrderByIdDesc(Long memberId);

    Page<Order> findAllByOrderByIdDesc(Pageable pageable);

    Page<Order> findByStatusOrderByIdDesc(OrderStatus status, Pageable pageable);

    long countByOrderedAtBetween(LocalDateTime from, LocalDateTime to);

    @Query("select coalesce(sum(o.totalPrice), 0) from Order o where o.orderedAt between :start and :end and o.status <> com.greenbox.shop.domain.order.OrderStatus.CANCELED")
    long sumSalesBetween(LocalDateTime start, LocalDateTime end);

    List<Order> findTop5ByOrderByIdDesc();
}
