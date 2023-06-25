package com.example.superdupermart.service;

import com.example.superdupermart.dao.ProductDao;
import com.example.superdupermart.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    private ProductDao productDao;

    @Autowired
    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<Product> getAllStockProducts() {
        List<Product> productList = productDao.getAllProducts().stream()
                .filter(product -> product.getQuantity() > 0)
                .collect(Collectors.toList());
        productList.forEach(product -> {
            product.setQuantity(-1);
            product.setWholesale_price(Integer.MAX_VALUE);
        });
        return productList;
    }

    public Product getProductById(int id) {
        return productDao.getProductById(id);
    }
}
