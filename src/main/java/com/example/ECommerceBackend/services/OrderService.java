package com.example.ECommerceBackend.services;

import com.example.ECommerceBackend.dtos.*;
import com.example.ECommerceBackend.entities.*;
import com.example.ECommerceBackend.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private RazorpayService razorpayService;
    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    public OrderResponseDTO placeOrder() {
        Users curr=getLoggedInUser();
        if (curr.getCart() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }
        Cart cart = cartRepository.findByIdForUpdate(curr.getCart().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty"));

        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }
        List<CartItem> items=cart.getCartItems();

        //Validating stocks
        for(CartItem ci:items)
        {
            if(ci.getQuantity()>ci.getProduct().getStock())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Insuffient stock for product: "+ci.getProduct().getName());
        }
        Order order=Order.builder().status("PLACED").users(curr).build();
        List<OrderItem> orderItems=new ArrayList<>();
        double total=0.0;
        for(CartItem ci:items)
        {
            Product product=ci.getProduct();
            OrderItem orderItem=OrderItem.builder()
                    .quantity(ci.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .order(order)
                    .product(product).build();
            orderItems.add(orderItem);
            total+=product.getPrice()*ci.getQuantity();
            //Decrementing the stock
            product.setStock(product.getStock()-ci.getQuantity());
            productRepository.save(product);

        }
        order.setOrderItems(orderItems);
        order.setTotalAmount(total);
        Order savedOrder=orderRepository.save(order);
        cart.getCartItems().clear();
        cartRepository.save(cart);
        List<OrderItemResponseDTO> itemDTOs = savedOrder.getOrderItems().stream()
                .map(oi -> OrderItemResponseDTO.builder()
                        .productId(oi.getProduct().getId())
                        .productName(oi.getProduct().getName())
                        .quantity(oi.getQuantity())
                        .priceAtPurchase(oi.getPriceAtPurchase())
                        .build())
                .toList();

        return OrderResponseDTO.builder()
                .orderId(savedOrder.getId())
                .status(savedOrder.getStatus())
                .totalAmount(savedOrder.getTotalAmount())
                .items(itemDTOs)
                .build();
    }

    private Users getLoggedInUser() {
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        return usersRepository.findByEmail(email);
    }

    @Transactional
    public InitiateOrderResponseDTO initiateOrder() throws Exception
    {
        Users curr=getLoggedInUser();
        if(curr.getCart()==null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }
        Cart cart=cartRepository.findByIdForUpdate(curr.getCart().getId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Cart is empty"));
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }
        for(CartItem ci:cart.getCartItems())
        {
            if (ci.getQuantity() > ci.getProduct().getStock()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Insufficient stock for product: " + ci.getProduct().getName());
        }
        }
        double total = cart.getCartItems().stream()
                .mapToDouble(ci -> ci.getProduct().getPrice() * ci.getQuantity())
                .sum();
        com.razorpay.Order razorpayOrder=razorpayService.createRazorpayOrder(total);
        Payment payment = Payment.builder()
                .amount(total)
                .status("CREATED")
                .method("razorpay")
                .razorpayOrderId(razorpayOrder.get("id"))
                .build();

        Order order = Order.builder()
                .status("PENDING_PAYMENT")
                .users(curr)
                .totalAmount(total)
                .payment(payment)
                .build();
        Order savedOrder=orderRepository.save(order);

        return InitiateOrderResponseDTO.builder()
                .internalOrderId(savedOrder.getId())
                .razorpayOrderId(razorpayOrder.get("id"))
                .amount(total)
                .currency("INR")
                .razorpayKeyId(razorpayService.getKeyId())
                .build();
    }

    @Transactional
    public OrderResponseDTO confirmPayment(PaymentConfirmDTO req)throws Exception{
        Order order = orderRepository.findById(req.getInternalOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        Users curr = getLoggedInUser();
        if (!order.getUsers().getId().equals(curr.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This order does not belong to you");
        }
        if (!"PENDING_PAYMENT".equals(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This order is not awaiting payment");
        }
        boolean valid=razorpayService.verifySignature(
                order.getPayment().getRazorpayOrderId(),
                req.getRazorpayPaymentId(),
                req.getRazorpaySignature()
        );
        if (!valid) {
            order.getPayment().setStatus("FAILED");
            paymentRepository.save(order.getPayment());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment verification failed");
        }
        order.getPayment().setStatus("PAID");
        order.getPayment().setRazorpayPaymentId(req.getRazorpayPaymentId());
        order.setStatus("PLACED");
        Cart cart = curr.getCart();
        List<CartItem> cartItems = new ArrayList<>(cart.getCartItems());
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem ci : cartItems) {
            Product product = ci.getProduct();

            if (ci.getQuantity() > product.getStock()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(ci.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .build();
            orderItems.add(orderItem);

            product.setStock(product.getStock() - ci.getQuantity());
            productRepository.save(product);
        }
        order.setOrderItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        cart.getCartItems().clear();
        cartRepository.save(cart);

        return mapToDTO(savedOrder);
    }


    private OrderResponseDTO mapToDTO(Order order) {
        List<OrderItemResponseDTO> itemDTOs = order.getOrderItems().stream()
                .map(oi -> OrderItemResponseDTO.builder()
                        .productId(oi.getProduct().getId())
                        .productName(oi.getProduct().getName())
                        .quantity(oi.getQuantity())
                        .priceAtPurchase(oi.getPriceAtPurchase())
                        .build())
                .toList();

        return OrderResponseDTO.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(itemDTOs)
                .build();
    }

    public List<OrderResponseDTO> getAllOrderOfUser() {
        Users curr=getLoggedInUser();
        List<Order> orders=orderRepository.findByUsers(curr);
        if(orders==null || orders.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user do not have any order");
        return orders.stream()
                .map(order -> {
                    List<OrderItemResponseDTO> itemDTOs = order.getOrderItems().stream()
                            .map(oi -> OrderItemResponseDTO.builder()
                                    .productId(oi.getProduct().getId())
                                    .productName(oi.getProduct().getName())
                                    .quantity(oi.getQuantity())
                                    .priceAtPurchase(oi.getPriceAtPurchase())
                                    .build())
                            .toList();

                    return OrderResponseDTO.builder()
                            .orderId(order.getId())
                            .status(order.getStatus())
                            .totalAmount(order.getTotalAmount())
                            .items(itemDTOs)
                            .build();
                })
                .toList();

    }


    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders=orderRepository.findAll();
        if(orders==null || orders.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user do not have any order");
        return orders.stream()
                .map(order -> {
                    List<OrderItemResponseDTO> itemDTOs = order.getOrderItems().stream()
                            .map(oi -> OrderItemResponseDTO.builder()
                                    .productId(oi.getProduct().getId())
                                    .productName(oi.getProduct().getName())
                                    .quantity(oi.getQuantity())
                                    .priceAtPurchase(oi.getPriceAtPurchase())
                                    .build())
                            .toList();

                    return OrderResponseDTO.builder()
                            .orderId(order.getId())
                            .status(order.getStatus())
                            .totalAmount(order.getTotalAmount())
                            .items(itemDTOs)
                            .build();
                })
                .toList();
    }




    public OrderResponseDTO getOrderByOrderId(Long orderId) {
        Users curr=getLoggedInUser();
        Order order=orderRepository.findById(orderId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Order do not exist" +
                "with this id"));

        if (!order.getUsers().getId().equals(curr.getId()) && !curr.getRole().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This order does not belong to you");
        }

        List<OrderItemResponseDTO> itemDTOs = order.getOrderItems().stream()
                .map(oi -> OrderItemResponseDTO.builder()
                        .productId(oi.getProduct().getId())
                        .productName(oi.getProduct().getName())
                        .quantity(oi.getQuantity())
                        .priceAtPurchase(oi.getPriceAtPurchase())
                        .build()).toList();

        return OrderResponseDTO.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(itemDTOs)
                .build();
    }


    public OrderResponseDTO changeStatusofOrder(Long orderId, OrderStatusChangeDTO statusChangeDTO) {
        Users curr=getLoggedInUser();
        Order order=orderRepository.findById(orderId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Order do not exist" +
                "with this id"));

        if (!curr.getRole().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot change status");
        }
        order.setStatus(statusChangeDTO.getStatus());
        Order savedOrder=orderRepository.save(order);
        List<OrderItemResponseDTO> itemDTOs = savedOrder.getOrderItems().stream()
                .map(oi -> OrderItemResponseDTO.builder()
                        .productId(oi.getProduct().getId())
                        .productName(oi.getProduct().getName())
                        .quantity(oi.getQuantity())
                        .priceAtPurchase(oi.getPriceAtPurchase())
                        .build()).toList();
        return OrderResponseDTO.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(itemDTOs)
                .build();
    }

}
