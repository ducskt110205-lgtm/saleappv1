package com.example.saleappv1.Controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.saleappv1.Model.Category;
import com.example.saleappv1.Service.CategoryService;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Hiển thị danh sách
    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories",
                categoryService.getAllCategories());

        return "categories/categories-list";
    }

    // Hiển thị form thêm
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new Category());

        return "categories/add-category";
    }

    // Lưu danh mục mới
    @PostMapping("/add")
    public String addCategory(
            @ModelAttribute("category") Category category,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            categoryService.addCategory(category);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "categories/add-category";
        }

        redirectAttributes.addFlashAttribute(
                "success", "Thêm danh mục thành công!");

        return "redirect:/categories";
    }

    // Hiển thị form sửa
    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable("id") Integer id,
            Model model) {

        model.addAttribute("category", findCategory(id));

        return "categories/update-category";
    }

    // Lưu thông tin chỉnh sửa
    @PostMapping("/update/{id}")
    public String updateCategory(
            @PathVariable("id") Integer id,
            @ModelAttribute("category") Category category,
            Model model,
            RedirectAttributes redirectAttributes) {

        findCategory(id);
        category.setId(id);

        try {
            categoryService.updateCategory(category);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "categories/update-category";
        }

        redirectAttributes.addFlashAttribute(
                "success", "Cập nhật danh mục thành công!");

        return "redirect:/categories";
    }

    // Xóa danh mục
    @PostMapping("/delete/{id}")
    public String deleteCategory(
            @PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes) {

        try {
            categoryService.deleteCategoryById(id);

            redirectAttributes.addFlashAttribute(
                    "success", "Xóa danh mục thành công!");

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Không thể xóa danh mục đang có sản phẩm.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/categories";
    }

    private Category findCategory(Integer id) {
        return categoryService.getCategoryById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy danh mục."));
    }
}