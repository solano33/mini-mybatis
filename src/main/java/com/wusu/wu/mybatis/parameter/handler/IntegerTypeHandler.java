package com.wusu.wu.mybatis.parameter.handler;

import com.wusu.wu.mybatis.parameter.ParameterTypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author github.com/solano33
 * @date 2024/5/19 23:39
 */
public class IntegerTypeHandler implements ParameterTypeHandler<Integer> {
    
    @Override
    public void setParameter(PreparedStatement statement, int i, Integer value) throws SQLException {
        statement.setInt(i, value);
    }
}
