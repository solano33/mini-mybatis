package com.wusu.wu.mybatis.proxy;

import com.wusu.wu.mybatis.annotation.Param;
import com.wusu.wu.mybatis.annotation.Select;
import com.wusu.wu.mybatis.entity.User;
import com.wusu.wu.mybatis.parameter.ParameterTypeFactory;
import com.wusu.wu.mybatis.parameter.ParameterTypeHandler;
import com.wusu.wu.mybatis.parse.GenericTokenParser;
import com.wusu.wu.mybatis.parse.ParameterMapping;
import com.wusu.wu.mybatis.parse.handler.ParameterMappingTokenHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author github.com/solano33
 * @date 2024/5/15 22:57
 */
@Slf4j
public class MapperProxyFactory {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getMapper(Class<T> mapper) {
        Object proxyInstance = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{mapper}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                log.info("jdk proxy invoke method: {}", method.getName());
                // 1. 创建数据库连接
                Connection connection = getConnection();

                // 2. 构造PreparedStatement
                Select annotation = method.getAnnotation(Select.class);
                String sql = annotation.value();

                Map<String, Object> paramValueMappings = new HashMap<>();
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    Param paramAnnotation = parameter.getAnnotation(Param.class);
                    paramValueMappings.put(paramAnnotation.value(), args[i]);
                    paramValueMappings.put(parameter.getName(), args[i]);
                }

                // 解析@Select中设置的sql，将select * from user where id = #{id} 转为select * from user where id = ?
                ParameterMappingTokenHandler tokenHandler = new ParameterMappingTokenHandler();
                GenericTokenParser parser = new GenericTokenParser("#{", "}", tokenHandler);
                String parseSql = parser.parse(sql);
                List<ParameterMapping> parameterMappings = tokenHandler.getParameterMappings();
                log.info("解析后的sql为：{}，解析出的参数为：{}", parseSql, parameterMappings);

                PreparedStatement preparedStatement = connection.prepareStatement(parseSql);
                for (int i = 0; i < parameterMappings.size(); i++) {
                    String property = parameterMappings.get(i).getProperty();
                    Object value = paramValueMappings.get(property);
                    if (value == null) {
                        throw new RuntimeException(String.format("未找到方法形参%s对应的实参", property));
                    }
                    ParameterTypeHandler typeHandler = ParameterTypeFactory.getTypeHandler(value.getClass());
                    // 这里注意要使用i+1因为从1开始
                    typeHandler.setParameter(preparedStatement, i + 1, value);
                }

                // 3. 执行查询
                preparedStatement.execute();

                // 4. 封装结果
                List<User> users = new ArrayList<>();
                ResultSet resultSet = preparedStatement.getResultSet();
                while (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setName(resultSet.getString("name"));
                    user.setAge(resultSet.getInt("age"));
                    users.add(user);
                }
                log.info("users: {}", users);

                // 5. 关闭连接
                connection.close();
                return null;
            }
        });
        return (T) proxyInstance;
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/solano?useSSL=false&serverTimezone=UTC",
                "root", "root");
    }
}
