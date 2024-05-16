package com.wusu.wu.mybatis.parse.handler;

import com.wusu.wu.mybatis.parse.ParameterMapping;
import com.wusu.wu.mybatis.parse.TokenHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author github.com/solano33
 * @date 2024/5/15 22:57
 */
public class ParameterMappingTokenHandler implements TokenHandler {

    private final List<ParameterMapping> parameterMappings = new ArrayList<>();

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    @Override
    public String handleToken(String content) {
        parameterMappings.add(new ParameterMapping(content));
        return "?";
    }
}