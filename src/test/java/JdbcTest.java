import com.wusu.wu.mybatis.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 原生JDBC测试
 * @author github.com/solano33
 * @date 2024/5/15 23:07
 */
@Slf4j
public class JdbcTest {

    @Before
    public void before() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    @Test
    public void test() throws SQLException {
        // 1. 创建数据库连接
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/solano?useSSL=false&serverTimezone=UTC",
                "root", "root");

        // 2. 构造PreparedStatement
        PreparedStatement preparedStatement = connection.prepareStatement("select * from user where id = ?");
        preparedStatement.setInt(1, 1);

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
    }
}
