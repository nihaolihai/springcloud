package com.demo.springcloud.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Store implements Serializable {

    private Long id;
    private Long productId;
    private Long total;
    private Long used;
    private Long residue;
}
