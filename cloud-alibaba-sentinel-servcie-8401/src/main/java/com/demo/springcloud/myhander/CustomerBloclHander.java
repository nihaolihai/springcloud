package com.demo.springcloud.myhander;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.demo.springcloud.entities.CommoneResult;

/**
 * 自定义异常
 */
public class CustomerBloclHander {

    public static CommoneResult handerExcetion(BlockException blockException){
        return new CommoneResult(444,"按客户自定义异常1号");
    }
    public static CommoneResult handerExcetions(BlockException blockException){
        return new CommoneResult(444,"按客户自定义异常2号");
    }
}
