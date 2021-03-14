package com.demo.springcloud.service;

import com.demo.springcloud.entities.Store;

public interface StoreService {
    int create(Store store);
    Store getById(Long id);
    int divide(Long productId,Integer count);
}
