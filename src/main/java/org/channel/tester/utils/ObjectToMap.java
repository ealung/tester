package org.channel.tester.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.util.ReflectionUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author channel<zclsoft@163.com>
 */
@Slf4j
public class ObjectToMap {
    public static Map<String, Object> toMap(Object obj) {
        if (null == obj) {
            return null;
        }
        Map<String, Object> pageData = new LinkedHashMap<>();
        try {
            BeanInfo beanInfo = null;
            beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] proDescrtptors = beanInfo.getPropertyDescriptors();
            if (proDescrtptors != null && proDescrtptors.length > 0) {
                for (PropertyDescriptor propDesc : proDescrtptors) {
                    if(propDesc.getPropertyType().isAssignableFrom(Class.class)){
                        continue;
                    }
                    Method method = propDesc.getReadMethod();
                    if (propDesc.getPropertyType().isAssignableFrom(Date.class)) {
                        Date date = (Date) method.invoke(obj);
                        pageData.put(propDesc.getName(), date == null ? null : date.getTime());
                    } else if (propDesc.getPropertyType().isAssignableFrom(java.sql.Date.class)) {
                        java.sql.Date date = (java.sql.Date) method.invoke(obj);
                        pageData.put(propDesc.getName(), date == null ? null : date.getTime());
                    } else if (propDesc.getPropertyType().isAssignableFrom(List.class)) {
                        List list = null;
                        list = (List) method.invoke(obj);
                        if (null == list || list.isEmpty()) {
                            continue;
                        }
                        List<Map<String, Object>> maps = new ArrayList<>();
                        list.forEach(o1 -> {
                            maps.add(toMap(o1));
                        });
                        pageData.put(propDesc.getName(), maps);
                    } else if (propDesc.getPropertyType().getClassLoader() != null) {
                        Object attrObj = method.invoke(obj);
                        pageData.put(propDesc.getName(), toMap(attrObj));
                    } else {
                        pageData.put(propDesc.getName(), method.invoke(obj));
                    }
                }
            }
        } catch (Exception e) {
            log.error("设置转换类异常", e);
        }
        return pageData;
    }

    public static List<Map<String, Object>> toListMap(List<?> obj) {
        List<Map<String, Object>> maps = new ArrayList<>();
        obj.forEach(o1 -> {
            maps.add(toMap(o1));
        });
        return maps;
    }

    public static Model setToModel(Object obj, Model model) {
        ReflectionUtils.doWithFields(obj.getClass(), field -> {
            try {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), obj.getClass());
                if (propertyDescriptor.getPropertyType().isAssignableFrom(java.sql.Date.class)) {
                    java.sql.Date date = (java.sql.Date) propertyDescriptor.getReadMethod().invoke(obj);
                    model.addAttribute(field.getName(), date.getTime());
                } else if (propertyDescriptor.getPropertyType().isAssignableFrom(Date.class)) {
                    Date date = (Date) propertyDescriptor.getReadMethod().invoke(obj);
                    model.addAttribute(field.getName(), date.getTime());
                }
                if (propertyDescriptor.getPropertyType().isAssignableFrom(List.class)) {
                    List list = (List) propertyDescriptor.getReadMethod().invoke(obj);
                    List<Map<String, Object>> maps = new ArrayList<>();
                    if (null != list) {
                        list.forEach(o1 -> {
                            maps.add(toMap(o1));
                        });
                        model.addAttribute(field.getName(), maps);
                    }
                } else {
                    model.addAttribute(field.getName(), propertyDescriptor.getReadMethod().invoke(obj, null));
                }
            } catch (Exception e) {
                log.error("设置转换类异常", e);
            }
        });
        return model;
    }
}
