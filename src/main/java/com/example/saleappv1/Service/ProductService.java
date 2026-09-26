package com.example.saleappv1.Service;

import com.example.saleappv1.Model.Category;
import com.example.saleappv1.Model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

	private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Product> getAllProducts() {
        try {
            ClassPathResource resource =
                    new ClassPathResource("data/products.json");

            return objectMapper.readValue(
                    resource.getInputStream(),
                    new TypeReference<List<Product>>() {}
            );

        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc products.json", e);
        }
    }

    public List<Category> getAllCategories() {
        try {
            ClassPathResource resource =
                    new ClassPathResource("data/categories.json");

            return objectMapper.readValue(
                    resource.getInputStream(),
                    new TypeReference<List<Category>>() {}
            );

        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc categories.json", e);
        }
    }

    public Product getProductById(Integer id) {

        return getAllProducts()
                .stream()
                .filter(product -> product.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Category getCategoryById(Integer id) {

        return getAllCategories()
                .stream()
                .filter(category -> category.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Product> filterProducts(
            Integer categoryId,
            String keyword,
            Double fromPrice,
            Double toPrice) {

        return getAllProducts()
                .stream()
                .filter(product ->
                        categoryId == null ||
                        product.getCategoryId().equals(categoryId))

                .filter(product ->
                        keyword == null ||
                        keyword.isBlank() ||
                        product.getName()
                                .toLowerCase()
                                .contains(keyword.toLowerCase()))

                .filter(product ->
                        fromPrice == null ||
                        product.getPrice() >= fromPrice)

                .filter(product ->
                        toPrice == null ||
                        product.getPrice() <= toPrice)

                .toList();
    }
}