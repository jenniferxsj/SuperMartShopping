package com.example.superdupermart.service;

import com.example.superdupermart.dao.ProductDao;
import com.example.superdupermart.domain.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    private final ProductDao productDao;

    @Autowired
    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<Product> getAllStockProducts() {
        List<Product> productList = productDao.getAllProducts().stream()
                .filter(product -> product.getQuantity() > 0)
                .collect(Collectors.toList());
        return productList;
    }

    public Product getProductById(int id) {
        Product product = productDao.getProductById(id);
        return product;
    }

    public void updateProduct(Product product) {
        Product oldProduct = productDao.getProductById(product.getId());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setQuantity(product.getQuantity());
        oldProduct.setWholesale_price(product.getWholesale_price());
        oldProduct.setRetail_price(product.getRetail_price());
    }

    public void createProduct(Product product) {
        productDao.addProduct(product);
    }
}
