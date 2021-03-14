package com.demo.springcloud.service.impl;

import com.demo.springcloud.dao.StoreDao;
import com.demo.springcloud.entities.Store;
import com.demo.springcloud.service.StoreService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class StoreServiceImpl implements StoreService {

    @Resource
    private StoreDao storeDao;

    @Override
    public int create(Store store) {
        return storeDao.create(store);
    }

    @Override
    public Store getById(Long id) {
        return storeDao.getById(id);
    }

    @Override
    public int divide(Long productId, Integer count) {
        return storeDao.divide(productId,count);
    }
}
