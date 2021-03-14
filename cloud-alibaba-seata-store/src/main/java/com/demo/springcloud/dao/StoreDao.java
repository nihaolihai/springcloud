package com.demo.springcloud.dao;

import com.demo.springcloud.entities.Store;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StoreDao {
    int create(Store store);
    Store getById(@Param("id") Long id);
    int divide(@Param("productId") Long productId,@Param("count") Integer count);
}
