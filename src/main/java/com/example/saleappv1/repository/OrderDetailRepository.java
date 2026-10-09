package com.example.saleappv1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.saleappv1.Model.OrderDetail;

@Repository
public interface OrderDetailRepository
        extends JpaRepository<OrderDetail, Integer> {
}