package com.demo.springcloud.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

    private Long id;
    private Long userId;
    private Long productId;
    private Integer count;
    private Integer status;
    private BigDecimal money;
}
