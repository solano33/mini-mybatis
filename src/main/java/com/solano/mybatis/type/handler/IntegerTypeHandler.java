package com.solano.mybatis.type.handler;

import com.solano.mybatis.type.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author github.com/solano33
 * @date 2024/5/19 23:39
 */
public class IntegerTypeHandler implements TypeHandler<Integer> {
    
    @Override
    public void setParameter(PreparedStatement statement, int i, Integer value) throws SQLException {
        statement.setInt(i, value);
    }

    @Override
    public Integer getResult(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getInt(columnName);
    }
}
