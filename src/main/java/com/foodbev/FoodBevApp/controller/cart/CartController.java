package com.foodbev.FoodBevApp.controller.cart;

import com.foodbev.FoodBevApp.entity.cart.CartItem;
import com.foodbev.FoodBevApp.entity.user.User;
import com.foodbev.FoodBevApp.service.cart.CartService;
import com.foodbev.FoodBevApp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    // ================= ADD TO CART =================
    @PostMapping("/add")
    @ResponseBody
    public String addToCart(@RequestParam Long productId,
                            @RequestParam int quantity,
                            Principal principal) {

        User user = userService.findByEmail(principal.getName());
        cartService.addToCart(user, productId, quantity);

        return "Added to cart";
    }

    // ================= CART PAGE =================
    @GetMapping
    public String cartPage(Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName());
        if (user == null) {
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartService.getUserCart(user);

        BigDecimal subtotal = cartItems.stream()
            .map(item -> item.getProduct()
                .calculatePrice()
                .multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.10));

        BigDecimal grandTotal = subtotal.add(tax);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("tax", tax);
        model.addAttribute("grandTotal", grandTotal);

        return "user/cart";
    }


    @PostMapping("/update")
    @ResponseBody
    public void updateQty(@RequestParam Long productId,
                        @RequestParam int quantity,
                        Principal principal) {
        User user = userService.findByEmail(principal.getName());
        cartService.updateQuantity(user, productId, quantity);
    }

    @PostMapping("/remove")
    @ResponseBody
    public void remove(@RequestParam Long productId, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        cartService.removeItem(user, productId);
    }

    @PostMapping("/clear")
    @ResponseBody
    public void clear(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        cartService.clearCart(user);
    }

}
