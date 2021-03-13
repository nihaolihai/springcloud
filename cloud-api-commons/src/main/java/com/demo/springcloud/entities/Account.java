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
public class Account implements Serializable {

    private Long id;
    private Long userId;
    private Long total;
    private Long used;
    private Long residue;
}
