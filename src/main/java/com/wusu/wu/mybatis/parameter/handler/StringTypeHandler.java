package com.wusu.wu.mybatis.parameter.handler;

import com.wusu.wu.mybatis.parameter.ParameterTypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author github.com/solano33
 * @date 2024/5/19 23:39
 */
public class StringTypeHandler implements ParameterTypeHandler<String> {

    @Override
    public void setParameter(PreparedStatement statement, int i, String value) throws SQLException {
        statement.setString(i, value);
    }
}
