package com.siegfried.blog.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 复制对象或集合属性
 * Created by zy_zhu on 2021/4/21.
 */
public class BeanCopyUtil {

    /**
     * 根据现有对象的属性创建目标对象并赋值
     *
     * @param source
     * @param target
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T copyObject(Object source, Class<T> target){
        T temp = null;
        try {
            temp = target.newInstance();
            if(source != null){
                org.springframework.beans.BeanUtils.copyProperties(source,temp);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * 拷贝集合
     *
     * @param source
     * @param target
     * @param <T>
     * @param <S>
     * @return
     */
    public static <T,S> List<T> copyList(List<S> source, Class<T> target){
        List<T> list = new ArrayList<>();
        if(source != null && source.size() > 0){
            for(Object object : source)
                list.add(BeanCopyUtil.copyObject(object,target));
        }
        return list;
    }
}
