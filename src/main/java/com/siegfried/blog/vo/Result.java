package com.siegfried.blog.vo;

import com.siegfried.blog.constant.StatusConst;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;

/**
 * view object，用于返回给前端，对数据和一些message等进行封装形成一个完整等json
 *
 * Created by zy_zhu on 2021/4/21.
 */
@Data
public class Result<T> implements Serializable {
    private boolean flag;
    private Integer code;
    private String message;
    private T data;

    public Result(boolean flag,Integer code,String message,Object data){
        this.flag = flag;
        this.code = code;
        this.message = message;
        this.data = (T) data;
    }

    public Result(boolean flag,Integer code, String message){
        this.flag = flag;
        this.code = code;
        this.message = message;
    }

    public Result(){
        this.flag = true;
        this.code = StatusConst.OK;
        this.message = "操作成功";
    }
}
