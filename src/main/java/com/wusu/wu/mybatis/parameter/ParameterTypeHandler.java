package com.wusu.wu.mybatis.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author github.com/solano33
 * @date 2024/5/19 23:27
 */
public interface ParameterTypeHandler<T> {

    void setParameter(PreparedStatement statement, int i, T value) throws SQLException;
}
