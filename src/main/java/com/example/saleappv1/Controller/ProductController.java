package com.example.saleappv1.Controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.saleappv1.Model.Product;
import com.example.saleappv1.Service.ProductService;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Danh sách và tìm kiếm sản phẩm
    @GetMapping
    public String listProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double fromPrice,
            @RequestParam(required = false) Double toPrice,
            Model model) {

        model.addAttribute("products",
                productService.filterProducts(
                        categoryId, keyword, fromPrice, toPrice));

        loadCategories(model);

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("fromPrice", fromPrice);
        model.addAttribute("toPrice", toPrice);

        return "products/products-list";
    }

    // Hiển thị form thêm
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        loadCategories(model);

        return "products/add-product";
    }

    // Lưu sản phẩm mới
    @PostMapping("/add")
    public String addProduct(
            @ModelAttribute("product") Product product,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("error",
                    "Dữ liệu không hợp lệ. Kiểm tra giá và danh mục.");
            loadCategories(model);
            return "products/add-product";
        }

        try {
            productService.addProduct(product);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            loadCategories(model);
            return "products/add-product";
        }

        redirectAttributes.addFlashAttribute(
                "success", "Thêm sản phẩm thành công!");

        return "redirect:/products";
    }

    // Hiển thị form sửa
    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable("id") Integer id,
            Model model) {

        model.addAttribute("product", findProduct(id));
        loadCategories(model);

        return "products/update-product";
    }

    // Lưu thông tin chỉnh sửa
    @PostMapping("/update/{id}")
    public String updateProduct(
            @PathVariable("id") Integer id,
            @ModelAttribute("product") Product product,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        findProduct(id);
        product.setId(id);

        if (result.hasErrors()) {
            model.addAttribute("error",
                    "Dữ liệu không hợp lệ. Kiểm tra giá và danh mục.");
            loadCategories(model);
            return "products/update-product";
        }

        try {
            productService.updateProduct(product);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            loadCategories(model);
            return "products/update-product";
        }

        redirectAttributes.addFlashAttribute(
                "success", "Cập nhật sản phẩm thành công!");

        return "redirect:/products";
    }

    // Xóa sản phẩm
    @PostMapping("/delete/{id}")
    public String deleteProduct(
            @PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes) {

        try {
            productService.deleteProductById(id);

            redirectAttributes.addFlashAttribute(
                    "success", "Xóa sản phẩm thành công!");

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Không thể xóa sản phẩm đã có trong đơn hàng.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/products";
    }

    // Giữ trang chi tiết sản phẩm đang có
    @GetMapping("/{productId}")
    public String productDetail(
            @PathVariable("productId") Integer productId,
            Model model) {

        Product product = findProduct(productId);

        model.addAttribute("product", product);
        model.addAttribute("category",
                productService.getCategoryById(
                        product.getCategoryId()));

        return "product-detail";
    }

    private void loadCategories(Model model) {
        model.addAttribute("categories",
                productService.getAllCategories());
    }

    private Product findProduct(Integer id) {
        Product product = productService.getProductById(id);

        if (product == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy sản phẩm.");
        }

        return product;
    }
}