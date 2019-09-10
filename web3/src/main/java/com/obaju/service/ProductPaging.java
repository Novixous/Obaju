package com.obaju.service;

import com.obaju.model.Product;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductPaging extends PagingAndSortingRepository<Product, Integer> {

}
