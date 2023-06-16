package com.my.bbs.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessorFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class BeanUtil {

    public static Object copyProperties(Object source, Object target, String... ignoreProperties) {
        if (source == null) {
            return target;
        }
        BeanUtils.copyProperties(source, target, ignoreProperties);
        return target;
    }

    public static <T> List<T> copyList(List sources, Class<T> clazz) {
        return copyList(sources, clazz, null);
    }

    public static <T> List<T> copyList(List sources, Class<T> clazz, Callback<T> callback) {
        List<T> targetList = new ArrayList<>();
        if (sources != null) {
            try {
                for (Object source : sources) {
                    T target = clazz.getDeclaredConstructor().newInstance();//newInstance();
                    copyProperties(source, target);
                    if (callback != null) {
                        callback.set(source, target);
                    }
                    targetList.add(target);
                }
            } catch (InstantiationException e) {
                //当应用程序尝试使用类 Class 中的 newInstance 方法创建类的实例，但无法实例化指定的类对象时抛出。
                e.printStackTrace();//
            } catch (IllegalAccessException e) {
                //当应用程序试图以反射方式创建实例（数组除外）、设置或获取字段或调用方法时抛出 IllegalAccessException，但当前正在执行的方法无权访问指定类、字段的定义， 方法或构造函数。
                e.printStackTrace();//
            } catch (InvocationTargetException e) {
                //InvocationTargetException 是一个已检查的异常，它包装了由调用的方法或构造函数抛出的异常。
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                //找不到特定方法时抛出。
                e.printStackTrace();
            }
        }
        return targetList;
    }

    public static Map<String, Object> toMap(Object bean, String... ignoreProperties) {
        Map<String, Object> map = new LinkedHashMap<>();
        //LinkedHashMap构造一个具有默认初始容量 (16) 和加载因子 (0.75) 的空插入顺序 LinkedHashMap 实例。
        List<String> ignoreList = new ArrayList<>(Arrays.asList(ignoreProperties));
        //asList返回由指定数组支持的固定大小列表。
        ignoreList.add("class");
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        //获取给定目标对象的 BeanWrapper，以 JavaBeans 样式访问属性。
        for (PropertyDescriptor pd : beanWrapper.getPropertyDescriptors()) {
            //PropertyDescriptor 描述了 Java Bean 通过一对访问器方法导出的一个属性。
            if (!ignoreList.contains(pd.getName()) && beanWrapper.isReadableProperty(pd.getName())) {
                Object propertyValue = beanWrapper.getPropertyValue(pd.getName());
                map.put(pd.getName(), propertyValue);
            }
        }
        return map;
    }

    public static <T> T toBean(Map<String, Object> map, Class<T> beanType) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(beanType);
        map.forEach((key, value) -> {
            if (beanWrapper.isWritableProperty(key)) {
                beanWrapper.setPropertyValue(key, value);
            }
        });
        return (T) beanWrapper.getWrappedInstance();
    }

    public static interface Callback<T> {
        void set(Object source, T target);
    }

    //检查Pojo对象是否有null字段
    public static boolean checkPojoNullField(Object o, Class<?> clz) {
        try {
            Field[] fields = clz.getDeclaredFields();
            //Field 提供关于类或接口的单个字段的信息和动态访问。
            //返回一个 Field 对象数组，反映由该 Class 对象表示的类或接口声明的所有字段。
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.get(o) == null) {
                    //get返回指定对象上此 Field 表示的字段的值。
                    return false;
                }
            }
            if (clz.getSuperclass() != Object.class) {
                //返回表示由此类表示的实体（类、接口、原始类型或 void）的直接超类的类。
                return checkPojoNullField(o, clz.getSuperclass());
            }
            return true;
        } catch (IllegalAccessException e) {
            return false;
        }
    }
}
