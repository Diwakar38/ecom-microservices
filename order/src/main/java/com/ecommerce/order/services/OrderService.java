package com.ecommerce.order.services;

import com.ecommerce.order.models.OrderStatus;
import com.ecommerce.order.dtos.OrderResponse;
import com.ecommerce.order.models.OrderItem;
import com.ecommerce.order.dtos.OrderItemDTO;
import com.ecommerce.order.models.CartItem;
import com.ecommerce.order.models.Order;
import com.ecommerce.order.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

//    @Autowired
//    private UserRepository userRepository;

    public Optional<OrderResponse> createOrder(String userId) {
        List<CartItem> cartItems = cartService.getUserCart(userId);
        if(cartItems.isEmpty()) {
            return Optional.empty();
        }

//        Optional<User> userOptional = userRepository.findById(Long.valueOf(userId));
//        if(userOptional.isEmpty()) {
//            return Optional.empty();
//        }
//        User user = userOptional.get();

        BigDecimal totalPrice = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUserId(Long.valueOf(userId));
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);
        List<OrderItem> orderItems = cartItems.stream()
                        .map(item -> new OrderItem(
                                null,
                                Long.valueOf(item.getProductId()),
                                item.getQuantity(),
                                item.getPrice(),
                                order
                        )).toList();
        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(userId);
        return Optional.of(mapToOrderResponse(savedOrder));
    }

    private OrderResponse mapToOrderResponse(Order savedOrder) {
        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus(),
                savedOrder.getItems().stream().map(orderItem -> new OrderItemDTO(
                        orderItem.getId(),
                        String.valueOf(orderItem.getProductId()),
                        orderItem.getQuantity(),
                        orderItem.getPrice(),
                        orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))
                ))
                        .toList(),
                savedOrder.getCreatedAt()
        );
    }
}
