package com.example.saleappv1.Service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.saleappv1.Model.Category;
import com.example.saleappv1.Model.Product;
import com.example.saleappv1.repository.CategoryRepository;
import com.example.saleappv1.repository.ProductRepository;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // Lấy sản phẩm từ MySQL
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Lấy danh mục từ MySQL
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    public Category getCategoryById(Integer id) {
        if (id == null) {
            return null;
        }
        return categoryRepository.findById(id).orElse(null);
    }

    // Thêm sản phẩm
    public void addProduct(Product product) {
        Category category = validateProduct(product);

        Product newProduct = new Product();
        copyProductData(product, newProduct, category);

        productRepository.save(newProduct);
    }

    // Sửa sản phẩm
    public void updateProduct(Product product) {
        Product existingProduct = productRepository
                .findById(product.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sản phẩm không tồn tại."));

        Category category = validateProduct(product);
        copyProductData(product, existingProduct, category);

        productRepository.save(existingProduct);
    }

    // Xóa sản phẩm
    public void deleteProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sản phẩm không tồn tại."));

        productRepository.delete(product);
        productRepository.flush();
    }

    // Giữ chức năng tìm kiếm và lọc đang có
    public List<Product> filterProducts(
            Integer categoryId,
            String keyword,
            Double fromPrice,
            Double toPrice) {

        return getAllProducts()
                .stream()
                .filter(product ->
                        categoryId == null
                        || categoryId.equals(product.getCategoryId()))

                .filter(product ->
                        keyword == null
                        || keyword.isBlank()
                        || product.getName()
                                .toLowerCase(Locale.ROOT)
                                .contains(keyword.trim()
                                        .toLowerCase(Locale.ROOT)))

                .filter(product ->
                        fromPrice == null
                        || product.getPrice() >= fromPrice)

                .filter(product ->
                        toPrice == null
                        || product.getPrice() <= toPrice)

                .toList();
    }

    // Kiểm tra dữ liệu trước khi lưu
    private Category validateProduct(Product product) {
        if (product.getName() == null
                || product.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Tên sản phẩm không được để trống.");
        }

        if (product.getName().trim().length() > 255) {
            throw new IllegalArgumentException(
                    "Tên sản phẩm không được vượt quá 255 ký tự.");
        }

        if (product.getPrice() == null
                || !Double.isFinite(product.getPrice())
                || product.getPrice() < 0) {
            throw new IllegalArgumentException(
                    "Giá sản phẩm phải là số không âm.");
        }

        if (product.getImage() != null
                && product.getImage().length() > 255) {
            throw new IllegalArgumentException(
                    "Đường dẫn ảnh không được vượt quá 255 ký tự.");
        }

        if (product.getCategoryId() == null) {
            throw new IllegalArgumentException(
                    "Vui lòng chọn danh mục.");
        }

        return categoryRepository.findById(product.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Danh mục được chọn không tồn tại."));
    }

    // Sao chép các trường được phép cập nhật
    private void copyProductData(
            Product source,
            Product target,
            Category category) {

        target.setName(source.getName().trim());
        target.setDescription(source.getDescription());
        target.setPrice(source.getPrice());
        target.setImage(source.getImage());
        target.setCategory(category);
    }
}