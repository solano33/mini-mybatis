package com.solano.mybatis.type;

import com.solano.mybatis.type.handler.IntegerTypeHandler;
import com.solano.mybatis.type.handler.StringTypeHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author github.com/solano33
 * @date 2024/5/19 23:22
 */
public class TypeFactory {

    private static final Map<Class, TypeHandler> handlerMapping = new HashMap<>();
    static {
        handlerMapping.put(Integer.class, new IntegerTypeHandler());
        handlerMapping.put(String.class, new StringTypeHandler());
    }

    public static TypeHandler getTypeHandler(Class clazz) {
        TypeHandler typeHandler = handlerMapping.get(clazz);
        if (typeHandler == null) {
            throw new RuntimeException("未找到相应的类型处理器：" + clazz.getName());
        }
        return typeHandler;
    }
}
