package com.foodbev.FoodBevApp.controller.admin;

import com.foodbev.FoodBevApp.dto.product.request.CoffeeRequest;
import com.foodbev.FoodBevApp.dto.product.request.FoodRequest;
import com.foodbev.FoodBevApp.dto.product.request.SnackRequest;
import com.foodbev.FoodBevApp.dto.product.response.ProductListResponse;
import com.foodbev.FoodBevApp.entity.product.Coffee;
import com.foodbev.FoodBevApp.entity.product.Food;
import com.foodbev.FoodBevApp.entity.product.Product;
import com.foodbev.FoodBevApp.entity.product.Snack;
import com.foodbev.FoodBevApp.entity.product.enums.*;
import com.foodbev.FoodBevApp.service.product.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@Slf4j
public class AdminProductController {

    private final ProductService productService;

    // ==================== GET ====================
    @GetMapping
    public String getProductsPage(
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        log.info("Accessing products list page - Category: {}, Status: {}, Page: {}, Size: {}",
                category, status, page, size);


        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);


        Page<ProductListResponse> productPage = productService.getAllProducts(category, status, pageable);


        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("categories", ProductCategory.values());
        model.addAttribute("statuses", ProductStatus.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedStatus", status);

        return "admin/product/list";
    }

    // ==================== ADD ====================
    @GetMapping("/add")
    public String showAddProductPage(Model model) {
        log.info("Accessing add product page");

        model.addAttribute("foodTypes", FoodType.values());
        model.addAttribute("snackTypes", SnackType.values());
        model.addAttribute("coffeeTypes", CoffeeType.values());
        model.addAttribute("sizes", ProductSize.values());
        model.addAttribute("statuses", ProductStatus.values());

        return "admin/product/add";
    }

    @PostMapping("/add/food")
    public String addFood(
            @Valid @ModelAttribute FoodRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "image", required = false) MultipartFile image,
            RedirectAttributes redirectAttributes) {

        log.info("Adding new food product: {}", request.getName());

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation error: " + bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/products/add";
        }

        try {
            productService.createFood(request, image);
            redirectAttributes.addFlashAttribute("successMessage", "Food product added successfully!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            log.error("Error adding food product: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding product: " + e.getMessage());
            return "redirect:/admin/products/add";
        }
    }

    @PostMapping("/add/snack")
    public String addSnack(
            @Valid @ModelAttribute SnackRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "image", required = false) MultipartFile image,
            RedirectAttributes redirectAttributes) {

        log.info("Adding new snack product: {}", request.getName());

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation error: " + bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/products/add";
        }

        try {
            productService.createSnack(request, image);
            redirectAttributes.addFlashAttribute("successMessage", "Snack product added successfully!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            log.error("Error adding snack product: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding product: " + e.getMessage());
            return "redirect:/admin/products/add";
        }
    }

    @PostMapping("/add/coffee")
    public String addCoffee(
            @Valid @ModelAttribute CoffeeRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "image", required = false) MultipartFile image,
            RedirectAttributes redirectAttributes) {

        log.info("Adding new coffee product: {}", request.getName());

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation error: " + bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/products/add";
        }

        try {
            productService.createCoffee(request, image);
            redirectAttributes.addFlashAttribute("successMessage", "Coffee product added successfully!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            log.error("Error adding coffee product: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding product: " + e.getMessage());
            return "redirect:/admin/products/add";
        }
    }

    // ==================== EDIT ====================
    @GetMapping("/edit/{id}")
    public String showEditProductPage(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        log.info("Accessing edit product page for ID: {}", id);

        try {
            Product product = productService.findById(id);

            model.addAttribute("product", product);
            model.addAttribute("productId", id);

            if (product instanceof Food food) {
                model.addAttribute("productType", "FOOD");
                model.addAttribute("food", food);
            } else if (product instanceof Snack snack) {
                model.addAttribute("productType", "SNACK");
                model.addAttribute("snack", snack);
            } else if (product instanceof Coffee coffee) {
                model.addAttribute("productType", "COFFEE");
                model.addAttribute("coffee", coffee);
            }

            addFormAttributes(model);
            return "admin/product/edit";

        } catch (EntityNotFoundException e) {
            log.error("Product not found with ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found!");
            return "redirect:/admin/products";
        }
    }

    @PostMapping("/edit/{id}/food")
    public String editFood(
            @PathVariable Long id,
            @Valid @ModelAttribute FoodRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "image", required = false) MultipartFile image,
            RedirectAttributes redirectAttributes) {

        log.info("Updating food product with ID: {}", id);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Validation error: " + bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/products/edit/" + id;
        }

        try {
            productService.updateFood(id, request, image);
            redirectAttributes.addFlashAttribute("successMessage", "Food product updated successfully!");
            return "redirect:/admin/products";
        } catch (EntityNotFoundException e) {
            log.error("Food not found with ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            log.error("Error updating food product: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating product: " + e.getMessage());
            return "redirect:/admin/products/edit/" + id;
        }
    }

    @PostMapping("/edit/{id}/snack")
    public String editSnack(
            @PathVariable Long id,
            @Valid @ModelAttribute SnackRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "image", required = false) MultipartFile image,
            RedirectAttributes redirectAttributes) {

        log.info("Updating snack product with ID: {}", id);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Validation error: " + bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/products/edit/" + id;
        }

        try {
            productService.updateSnack(id, request, image);
            redirectAttributes.addFlashAttribute("successMessage", "Snack product updated successfully!");
            return "redirect:/admin/products";
        } catch (EntityNotFoundException e) {
            log.error("Snack not found with ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            log.error("Error updating snack product: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating product: " + e.getMessage());
            return "redirect:/admin/products/edit/" + id;
        }
    }

    @PostMapping("/edit/{id}/coffee")
    public String editCoffee(
            @PathVariable Long id,
            @Valid @ModelAttribute CoffeeRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "image", required = false) MultipartFile image,
            RedirectAttributes redirectAttributes) {

        log.info("Updating coffee product with ID: {}", id);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Validation error: " + bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/products/edit/" + id;
        }

        try {
            productService.updateCoffee(id, request, image);
            redirectAttributes.addFlashAttribute("successMessage", "Coffee product updated successfully!");
            return "redirect:/admin/products";
        } catch (EntityNotFoundException e) {
            log.error("Coffee not found with ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            log.error("Error updating coffee product: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating product: " + e.getMessage());
            return "redirect:/admin/products/edit/" + id;
        }
    }

    // ==================== DELETE ====================
    @PostMapping("/delete/{id}")
    public String deleteProduct(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        log.info("Deleting product with ID: {}", id);

        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        } catch (EntityNotFoundException e) {
            log.error("Product not found with ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found!");
        } catch (Exception e) {
            log.error("Error deleting product: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting product: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }


    // ==================== HELPER ====================
    private void addFormAttributes(Model model) {
        model.addAttribute("foodTypes", FoodType.values());
        model.addAttribute("snackTypes", SnackType.values());
        model.addAttribute("coffeeTypes", CoffeeType.values());
        model.addAttribute("sizes", ProductSize.values());
        model.addAttribute("statuses", ProductStatus.values());
    }
}
