package org.channel.tester.utils;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Random;

/**
 * Created by hzzhangchanglu on 2017/6/7
 * 测试帮助类
 */
public class TestUtils {
    /**
     * 获取测试对象，随机生成对应字段测试值
     *
     * @param t   测试对象类型
     * @param <T> 测试对象
     * @return T
     */
    public static <T> T getTestObject(Class<T> t) {
        try {
            T instance = t.newInstance();
            ReflectionUtils.doWithFields(t, new ReflectionUtils.FieldCallback() {
                @Override
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    if (!field.getName().equals("serialVersionUID")) {
                        ReflectionUtils.makeAccessible(field);
                        ReflectionUtils.setField(field, instance, getRandomTest(field));
                    }
                }
            });
            return instance;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取随机测试值
     *
     * @param field 对应字段
     * @return 字段测试值
     */
    public static Object getRandomTest(Field field) {
        return getRandomTest(field.getType());
    }

    public static Object getRandomTest(Class type) {
        Random random = new Random();
        if (Float.class.isAssignableFrom(type)) {
            return Math.abs(random.nextFloat());
        } else if (Double.class.isAssignableFrom(type)) {
            return Math.abs(random.nextDouble());
        } else if (Long.class.isAssignableFrom(type)) {
            return Math.abs(random.nextLong());
        } else if (Integer.class.isAssignableFrom(type)) {
            return random.nextInt(5);
        } else if (Boolean.class.isAssignableFrom(type)) {
            return random.nextInt(1) > 0;
        } else if (String.class.isAssignableFrom(type)) {
            String base = "abcdefghijklmnopqrstuvwxyz0123456789";
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 5; i++) {
                int number = random.nextInt(base.length());
                sb.append(base.charAt(number));
            }
            return sb.toString();
        } else {
            return null;
        }
    }
}
