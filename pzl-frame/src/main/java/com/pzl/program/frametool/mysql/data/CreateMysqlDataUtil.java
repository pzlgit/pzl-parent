package com.pzl.program.frametool.mysql.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

/**
 * 数据库和数据表准备
 *
 * @author pzl
 * @date 2020-04-13
 */
public class CreateMysqlDataUtil {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        //一开始必须填一个已经存在的数据库
        String url = "jdbc:mysql://www.pzl.com:3307/mysql?useUnicode=true&characterEncoding=utf-8&useSSL=false";
        Connection conn = DriverManager.getConnection(url, "root", "root");
        Statement stat = conn.createStatement();
        //创建数据库
        stat.execute("CREATE database collection");
        stat.execute("use collection");
        //创建数据库表
        stat.execute("DROP TABLE IF EXISTS `users`;");
        String createUserSql = "CREATE TABLE `users` (\n" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `name` varchar(255) DEFAULT NULL,\n" +
                "  `email` varchar(255) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;";
        stat.execute(createUserSql);
        //插入数据
        List<String> sqlList = Arrays.asList("INSERT INTO `users` VALUES ('1', 'linsen', 'linsen@126.com');",
                "INSERT INTO `users` VALUES ('2', 'sam', 'sam@qq.com');",
                "INSERT INTO `users` VALUES ('3', 'debug', 'debug@sina.com');",
                "INSERT INTO `users` VALUES ('4', '杰克', '杰克@sina.com');",
                "INSERT INTO `users` VALUES ('5', '张三', '张三@sina.com');",
                "INSERT INTO `users` VALUES ('6', '李四', '李四@sina.com');",
                "INSERT INTO `users` VALUES ('7', '王五', '王五@sina.com');",
                " INSERT INTO `users` VALUES ('8', '王五2', '王五2@sina.com');");
        for (String sql : sqlList) {
            stat.execute(sql);
        }
    }

}