package com.demo.springcloud.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommoneResult<T>{

    private Integer code;
    private String message;
    private T      data;
    public CommoneResult(Integer code,String message){
        this(code,message,null);
    }

}

