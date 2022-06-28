package com.processout.paymentgateway.persistence.repository;

import com.processout.paymentgateway.persistence.entity.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, UUID> {

    @Query("SELECT p FROM Payment p WHERE id = :paymentId AND merchant_id = :merchantId")
    Optional<Payment> findByPaymentIdAndMerchantId(@Param("paymentId") UUID paymentId, @Param("merchantId") UUID merchantId);
}
