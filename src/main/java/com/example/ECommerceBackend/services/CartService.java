package com.example.ECommerceBackend.services;

import com.example.ECommerceBackend.dtos.CartItemResponseDTO;
import com.example.ECommerceBackend.dtos.CartResponseDTO;
import com.example.ECommerceBackend.entities.Cart;
import com.example.ECommerceBackend.entities.CartItem;
import com.example.ECommerceBackend.entities.Users;
import com.example.ECommerceBackend.repositories.CartRepository;
import com.example.ECommerceBackend.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CartRepository cartRepository;
    public CartResponseDTO getCartByUserId(Long userId) {
        Users user=usersRepository.findById(userId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Cart cart=user.getCart();
        if(cart ==null)
        {
            cart=cartRepository.save(Cart.builder().build());
            user.setCart(cart);
            usersRepository.save(user);
        }
        List<CartItemResponseDTO> items= (cart.getCartItems() != null ? cart.getCartItems() : List.<CartItem>of()).stream()
                .map(item->CartItemResponseDTO.builder()
                        .cartItemId(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .price(item.getProduct().getPrice())
                        .quantity(item.getQuantity())
                        .build()
                ).toList();
        double total=items.stream().mapToDouble(i->i.getPrice()*i.getQuantity())
                .sum();
        return CartResponseDTO.builder().cartId(cart.getId()).items(items).totalAmount(total).build();
    }
}
