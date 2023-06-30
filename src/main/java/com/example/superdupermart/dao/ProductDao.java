package com.example.superdupermart.dao;

import com.example.superdupermart.domain.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductDao extends AbstractHibernateDao<Product> {
    public ProductDao() {
        setClazz(Product.class);
    }

    public void addProduct(Product product) {
        this.add(product);
    }

    public List<Product> getAllProducts() {
        return this.getAll();
    }

    public Product getProductById(int id) {
        return this.findById(id);
    }

}
