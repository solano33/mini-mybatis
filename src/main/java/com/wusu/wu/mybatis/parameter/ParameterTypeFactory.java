package com.wusu.wu.mybatis.parameter;

import com.wusu.wu.mybatis.parameter.handler.IntegerTypeHandler;
import com.wusu.wu.mybatis.parameter.handler.StringTypeHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author github.com/solano33
 * @date 2024/5/19 23:22
 */
public class ParameterTypeFactory {

    private static final Map<Class, ParameterTypeHandler> handlerMapping = new HashMap<>();
    static {
        handlerMapping.put(Integer.class, new IntegerTypeHandler());
        handlerMapping.put(String.class, new StringTypeHandler());
    }

    public static ParameterTypeHandler getTypeHandler(Class clazz) {
        ParameterTypeHandler parameterTypeHandler = handlerMapping.get(clazz);
        if (parameterTypeHandler == null) {
            throw new RuntimeException("未找到相应的类型处理器：" + clazz.getName());
        }
        return parameterTypeHandler;
    }
}
