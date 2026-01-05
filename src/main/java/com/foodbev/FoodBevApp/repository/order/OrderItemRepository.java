package com.foodbev.FoodBevApp.repository.order;

import com.foodbev.FoodBevApp.entity.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query(value = "SELECT oi.product_name, SUM(oi.quantity) as total_quantity " +
                   "FROM public.order_items oi " +
                   "JOIN public.orders o ON oi.order_id = o.id " +
                   "WHERE o.status = 'CONFIRMED' " +
                   "GROUP BY oi.product_name " +
                   "ORDER BY total_quantity DESC " +
                   "LIMIT 5", nativeQuery = true)
    List<Object[]> getTopSellingProducts();
}
