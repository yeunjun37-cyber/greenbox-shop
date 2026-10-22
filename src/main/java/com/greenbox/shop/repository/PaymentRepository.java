package com.greenbox.shop.repository;

import com.greenbox.shop.domain.order.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
