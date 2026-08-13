package com.example.ECommerceBackend.services;

import com.example.ECommerceBackend.dtos.AddToCartRequestDTO;
import com.example.ECommerceBackend.dtos.CartItemResponseDTO;
import com.example.ECommerceBackend.dtos.CartResponseDTO;
import com.example.ECommerceBackend.dtos.UpdateCartItemRequestDTO;
import com.example.ECommerceBackend.entities.Cart;
import com.example.ECommerceBackend.entities.CartItem;
import com.example.ECommerceBackend.entities.Product;
import com.example.ECommerceBackend.entities.Users;
import com.example.ECommerceBackend.repositories.CartItemRepository;
import com.example.ECommerceBackend.repositories.CartRepository;
import com.example.ECommerceBackend.repositories.ProductRepository;
import com.example.ECommerceBackend.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

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

    public CartResponseDTO addToCart(AddToCartRequestDTO req) {
        Users curr=getLoggedInUser();
        Product product=productRepository.findById(req.getProductId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found"));
        if(req.getQuantity()<=0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Minimum quantity should be 1");
        if(req.getQuantity()>product.getStock())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Requested quantity exceeded available stock");
        Cart cart=curr.getCart();
        if (cart==null)
        {
            cart=cartRepository.save(Cart.builder().build());
            curr.setCart(cart);
            usersRepository.save(curr);
        }
        //if same product already exists-> quantity increases
        CartItem existing=cartItemRepository.findByCartIdAndProductId(cart.getId(),product.getId()).orElse(null);
        if(existing!=null)
        {
            existing.setQuantity(existing.getQuantity()+ req.getQuantity());
            cartItemRepository.save(existing);
        }
        else
        {
            CartItem newItem=CartItem.builder().cart(cart).product(product).quantity(req.getQuantity()).build();
            cartItemRepository.save(newItem);
        }
        return getCartByUserId(curr.getId());
    }

    private Users getLoggedInUser() {
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        return usersRepository.findByEmail(email);
    }

    public CartResponseDTO updateCart(UpdateCartItemRequestDTO req, Long cartItemId) {
        Users curr=getLoggedInUser();
        CartItem item=cartItemRepository.findById(cartItemId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(item==null || !item.getCart().getId().equals(curr.getCart().getId()))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"This item do not belong to your cart");
        }
        if(req.getQuantity()<0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Quantity cannot be negative !");
        if(req.getQuantity()==0)
        {
            cartItemRepository.delete(item);
            return getCartByUserId(curr.getId());
        }

        if (req.getQuantity() > item.getProduct().getStock()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested quantity exceeds available stock");
        }
        item.setQuantity(req.getQuantity());
        cartItemRepository.save(item);
        return getCartByUserId(curr.getId());
    }
    public CartResponseDTO addOneItem(Long cartItemId) {
        Users curr=getLoggedInUser();
        CartItem item=cartItemRepository.findById(cartItemId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(item==null || !item.getCart().getId().equals(curr.getCart().getId()))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"This item do not belong to your cart");
        }
        if(item.getQuantity()==item.getProduct().getStock())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested quantity exceeds available stock");
        }
        item.setQuantity(item.getQuantity()+1);
        cartItemRepository.save(item);
        return getCartByUserId(curr.getId());
    }
    public CartResponseDTO deleteItemFromCart(Long cartItemId) {
        Users curr=getLoggedInUser();
        CartItem item=cartItemRepository.findById(cartItemId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(item==null || !item.getCart().getId().equals(curr.getCart().getId()))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"This item do not belong to your cart");
        }
        cartItemRepository.delete(item);
        return getCartByUserId(curr.getId());
    }


    public CartResponseDTO removeByOne(Long cartItemId) {
        Users curr=getLoggedInUser();
        CartItem item=cartItemRepository.findById(cartItemId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(item==null || !item.getCart().getId().equals(curr.getCart().getId()))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"This item do not belong to your cart");
        }
        if(item.getQuantity()==1)
        {
            cartItemRepository.delete(item);
            return getCartByUserId(curr.getId());
        }
        item.setQuantity(item.getQuantity()-1);
        cartItemRepository.save(item);
        return getCartByUserId(curr.getId());
    }
}
