package com.siegfried.blog.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * Created by zy_zhu on 2021/5/7.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OptLog {

    /**
     * @return 操作类型
     */
    String optType() default "";
}
