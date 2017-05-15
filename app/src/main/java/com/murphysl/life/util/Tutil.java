package com.murphysl.life.util;

import java.lang.reflect.ParameterizedType;

/**
 * Tutil
 *
 * @author: MurphySL
 * @time: 2017/5/15 18:07
 */


public class Tutil {
    public static <T> T getT(Object o , int i){
        try {
            return ((Class<T>)((ParameterizedType)o.getClass().getGenericSuperclass())
                    .getActualTypeArguments()[i])
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
