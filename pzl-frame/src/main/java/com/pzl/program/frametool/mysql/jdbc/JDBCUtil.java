package com.pzl.program.frametool.mysql.jdbc;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * JDBC操作数据库工具类封装
 *
 * @author pzl
 * @date 2020-04-12
 */
@Slf4j
public class JDBCUtil {

    /**
     * 执行sql(不包括查询语句)
     *
     * @param sql sql语句
     */
    public static Boolean executeSql(String sql) {
        Connection connection = null;
        try {
            //获取数据库连接
            connection = getConnection();
            Statement statement = connection.createStatement();
            //执行sql语句
            int i = statement.executeUpdate(sql);
            return i > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(null, null, connection);
        }
        return false;
    }

    /**
     * INSERT, UPDATE, DELETE 操作都可以包含在其中
     *
     * @param sql  sql语句
     * @param args 可变参数
     */
    public static void update(String sql, Object... args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = getConnection();
            //预编译sql语句
            preparedStatement = connection.prepareStatement(sql);
            //设置参数
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            //执行sql语句
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(null, preparedStatement, connection);
        }

    }

    /**
     * 查询一条记录,返回对应的对象
     *
     * @param clazz 泛型
     * @param sql   sql语句
     * @param args  可变参数
     * @param <T>   泛型类型
     * @return 封装对象
     */
    public static <T> T get(Class<T> clazz, String sql, Object... args) {
        List<T> result = getForList(clazz, sql, args);
        //如果集合不为空,则取第一个元素对象
        if (result.size() > 0) {
            return result.get(0);
        }

        //否则返回空
        return null;
    }

    /**
     * 传入 SQL 语句和 Class 对象, 返回 SQL 语句查询到的记录对应的 Class 类的对象的集合
     *
     * @param clazz 对象的类型
     * @param sql   SQL语句
     * @param args  填充SQL语句的占位符的可变参数
     * @return 对象集合
     */
    public static <T> List<T> getForList(Class<T> clazz, String sql, Object... args) {
        List<T> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            //1、获取连接
            connection = getConnection();
            //2、预编译sql
            preparedStatement = connection.prepareStatement(sql);
            //3、设置参数
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            //4、执行sql
            resultSet = preparedStatement.executeQuery();
            /*
             5、处理结果集, 得到 Map 的 List, 其中一个 Map 对象就是一条记录.
                Map 的 key 为 reusltSet 中列的别名, Map 的 value为列的值.
             */
            List<Map<String, Object>> values = handleResultSetToMapList(resultSet);
            /*
              6、把 Map 的 List 转为 clazz 对应的 List，其中 Map 的 key 即为 clazz 对应的对象的 propertyName,
                 而 Map 的 value 即为 clazz 对应的对象的 propertyValue.
             */
            list = transfterMapListToBeanList(clazz, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(resultSet, preparedStatement, connection);
        }

        return list;
    }

    /**
     * 返回某条记录的某一个字段的值 或 一个统计的值(一共有多少条记录等)
     *
     * @param sql  sql语句
     * @param args 可变参数
     * @param <E>  泛型
     * @return 字段
     */
    public static <E> E getForValue(String sql, Object... args) {
        //1、得到结果集: 该结果集应该只有一行, 且只有一列
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return (E) resultSet.getObject(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeConnection(resultSet, preparedStatement, connection);
        }

        return null;
    }

    /**
     * 转换List<Map> 为  List<T>
     *
     * @param clazz  泛型
     * @param values list中map集合
     * @return 对象集合
     * @throws Exception ex
     */
    public static <T> List<T> transfterMapListToBeanList(Class<T> clazz,
                                                         List<Map<String, Object>> values) throws Exception {
        List<T> result = new ArrayList<>();
        T bean = null;
        //当数据集合不为空,则进行数据格式转化
        if (values.size() > 0) {
            //遍历数据集合，单条数据转化
            for (Map<String, Object> m : values) {
                //通过反射创建一个其他类的对象
                bean = clazz.newInstance();
                //遍历map
                for (Map.Entry<String, Object> entry : m.entrySet()) {
                    //获取key
                    String propertyName = entry.getKey();
                    //获取value
                    Object value = entry.getValue();
                    Field f = bean.getClass().getDeclaredField(propertyName);
                    f.setAccessible(true);
                    f.set(bean, value);
                }
                //把 Object 对象放入到 list 中
                result.add(bean);
            }
        }

        return result;
    }

    /**
     * 处理结果集, 得到 Map 的一个 List, 其中一个 Map 对象对应一条记录
     *
     * @param resultSet 结果集
     * @return 数据结果集
     * @throws Exception ex
     */
    public static List<Map<String, Object>> handleResultSetToMapList(ResultSet resultSet) throws Exception {
        /*
         1、准备一个 List<Map<String, Object>>:
            键: 存放列的别名, 值: 存放列的值. 其中一个 Map 对象对应着一条记录
         */
        List<Map<String, Object>> values = new ArrayList<>();
        List<String> columnLabels = getColumnLabels(resultSet);
        Map<String, Object> map = null;
        //2、处理 ResultSet, 使用 while 循环
        while (resultSet.next()) {
            map = new HashMap<>();
            for (String columnLabel : columnLabels) {
                Object value = resultSet.getObject(columnLabel);
                map.put(columnLabel, value);
            }
            //3、把一条记录的一个 Map 对象放入 5 准备的 List 中
            values.add(map);
        }

        return values;
    }

    /**
     * 获取结果集的 ColumnLabel 对应的 List
     *
     * @param rs 结果集
     * @return 数据集合
     * @throws Exception ex
     */
    private static List<String> getColumnLabels(ResultSet rs) throws Exception {
        List<String> labels = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 0; i < rsmd.getColumnCount(); i++) {
            labels.add(rsmd.getColumnLabel(i + 1));
        }

        return labels;
    }

    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     * @throws Exception ex
     */
    public static Connection getConnection() throws Exception {
        //Properties读取配置文件
        Properties properties = new Properties();
        InputStream inStream = JDBCUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");
        properties.load(inStream);

        //1、准备获取连接的4个变量: user, password, url, jdbcDriver
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String url = properties.getProperty("url");
        String jdbcDriver = properties.getProperty("jdbcDriver");
        //2、加载数据库驱动: Class.forName(driverClass)
        Class.forName(jdbcDriver);
        //3、获取数据库连接
        Connection connection = DriverManager.getConnection(url, user, password);
        log.info("数据库连接获取成功！");

        return connection;
    }

    /**
     * 关闭数据库连接
     *
     * @param resultSet  结果集连接
     * @param statement  连接
     * @param connection 数据库连接
     */
    public static void closeConnection(ResultSet resultSet, Statement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}