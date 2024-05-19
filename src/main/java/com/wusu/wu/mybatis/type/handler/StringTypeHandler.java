package com.wusu.wu.mybatis.type.handler;

import com.wusu.wu.mybatis.type.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author github.com/solano33
 * @date 2024/5/19 23:39
 */
public class StringTypeHandler implements TypeHandler<String> {

    @Override
    public void setParameter(PreparedStatement statement, int i, String value) throws SQLException {
        statement.setString(i, value);
    }

    @Override
    public String getResult(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getString(columnName);
    }
}
