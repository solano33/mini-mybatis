package com.wusu.wu.mybatis.proxy;

import com.wusu.wu.mybatis.annotation.Param;
import com.wusu.wu.mybatis.annotation.Select;
import com.wusu.wu.mybatis.type.TypeFactory;
import com.wusu.wu.mybatis.type.TypeHandler;
import com.wusu.wu.mybatis.parse.GenericTokenParser;
import com.wusu.wu.mybatis.parse.ParameterMapping;
import com.wusu.wu.mybatis.parse.handler.ParameterMappingTokenHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

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
                    TypeHandler typeHandler = TypeFactory.getTypeHandler(value.getClass());
                    // 这里注意要使用i+1因为从1开始
                    typeHandler.setParameter(preparedStatement, i + 1, value);
                }

                // 3. 执行查询
                preparedStatement.execute();

                // 4. 封装结果
                Class resultType = null;
                Type genericReturnType = method.getGenericReturnType();
                if (genericReturnType instanceof Class) {
                    // 不是泛型
                    resultType = (Class) genericReturnType;
                } else if (genericReturnType instanceof ParameterizedType) {
                    // 是泛型
                    Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
                    resultType = (Class) actualTypeArguments[0];
                }

                List<Object> resultList = new ArrayList<>();
                ResultSet resultSet = preparedStatement.getResultSet();
                ResultSetMetaData metaData = resultSet.getMetaData();
                List<String> columnNames = new ArrayList<>();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    columnNames.add(metaData.getColumnName(i + 1));
                }

                Map<String, Method> setterMethodMapping = new HashMap<>();
                Arrays.stream(resultType.getDeclaredMethods()).filter(e -> e.getName().startsWith("set"))
                        .forEach(e -> {
                            String propertyName = e.getName().substring(3);
                            propertyName = propertyName.substring(0, 1).toLowerCase(Locale.ROOT) +
                                    propertyName.substring(1);
                            setterMethodMapping.put(propertyName, e);
                        });

                while (resultSet.next()) {
                    Object instance = resultType.newInstance();
                    for (int i = 0; i < metaData.getColumnCount(); i++) {
                        String columnName = columnNames.get(i);
                        log.info("columnName：{}", columnName);
                        // 通过set方法反射注入
                        Method setterMethod = setterMethodMapping.get(columnName);
                        Class<?> parameterType = setterMethod.getParameterTypes()[0];
                        TypeHandler typeHandler = TypeFactory.getTypeHandler(parameterType);
                        setterMethod.invoke(instance, typeHandler.getResult(resultSet, columnName));
                    }
                    resultList.add(instance);
                }
                log.info("users: {}", resultList);

                Object result = null;
                if (method.getReturnType().equals(List.class)) {
                    result = resultList;
                } else {
                    result = resultList.size() == 0 ? null : resultList.get(0);
                }

                // 5. 关闭连接
                connection.close();
                return result;
            }
        });
        return (T) proxyInstance;
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/solano?useSSL=false&serverTimezone=UTC",
                "root", "root");
    }
}
