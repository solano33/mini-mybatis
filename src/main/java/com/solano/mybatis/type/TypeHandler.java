package com.solano.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author github.com/solano33
 * @date 2024/5/19 23:27
 */
public interface TypeHandler<T> {

    void setParameter(PreparedStatement statement, int i, T value) throws SQLException;

    T getResult(ResultSet resultSet, String columnName) throws SQLException;
}
