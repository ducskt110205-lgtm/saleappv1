package com.example.saleappv1.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.saleappv1.Model.Category;
import com.example.saleappv1.repository.CategoryRepository;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Lấy tất cả danh mục
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Tìm danh mục theo ID
    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    // Thêm danh mục mới
    public void addCategory(Category category) {
        validateName(category.getName());

        Category newCategory = new Category();
        newCategory.setName(category.getName().trim());

        categoryRepository.save(newCategory);
    }

    // Cập nhật danh mục đã có
    public void updateCategory(Category category) {
        validateName(category.getName());

        Category existingCategory = categoryRepository
                .findById(category.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy danh mục có ID: "
                        + category.getId()));

        existingCategory.setName(category.getName().trim());
        categoryRepository.save(existingCategory);
    }

    // Xóa danh mục theo ID
    public void deleteCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy danh mục có ID: " + id));

        categoryRepository.delete(category);
        categoryRepository.flush();
    }

    // Kiểm tra tên trước khi lưu
    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Tên danh mục không được để trống.");
        }

        if (name.trim().length() > 255) {
            throw new IllegalArgumentException(
                    "Tên danh mục không được vượt quá 255 ký tự.");
        }
    }
}