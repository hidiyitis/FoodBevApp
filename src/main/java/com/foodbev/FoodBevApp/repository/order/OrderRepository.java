package com.foodbev.FoodBevApp.repository.order;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.foodbev.FoodBevApp.entity.order.Order;
import com.foodbev.FoodBevApp.entity.user.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);

    @Query(value = "SELECT DATE(o.created_at) as order_date, " +
            "COALESCE(SUM(oi.quantity), 0) as total_products, " +
            "COALESCE(SUM(o.total_amount), 0) as total_revenue " +
            "FROM orders o " +
            "LEFT JOIN (SELECT order_id, SUM(quantity) as quantity FROM order_items GROUP BY order_id) oi " +
            "ON o.id = oi.order_id " +
            "WHERE o.status = 'CONFIRMED' " +
            "GROUP BY DATE(o.created_at) " +
            "ORDER BY DATE(o.created_at) DESC", nativeQuery = true)
    List<Object[]> getDailyRevenue();

    @Query(value = "SELECT COALESCE(SUM(total_amount), 0) " +
            "FROM orders " +
            "WHERE status = 'CONFIRMED'", nativeQuery = true)
    BigDecimal getTotalRevenue();

    @Query(value = "SELECT COALESCE(SUM(total_amount), 0) " +
            "FROM orders " +
            "WHERE status = 'CONFIRMED' " +
            "AND DATE(created_at) = CURRENT_DATE", nativeQuery = true)
    BigDecimal getTodayRevenue();
}
    