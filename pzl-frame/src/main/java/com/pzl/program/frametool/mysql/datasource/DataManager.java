package com.pzl.program.frametool.mysql.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class DataManager {

    @Resource
    @Qualifier("jdbcTemplateOne")
    JdbcTemplate jdbcTemplateOne;

    @Resource
    @Qualifier("jdbcTemplateTwo")
    JdbcTemplate jdbcTemplateTwo;

    /**
     * db1 数据库处理操作
     *
     * @return 数据集合
     */
    public List<Map<String, Object>> getUserList1() {
        String sql = "SELECT * from users";
        List<Map<String, Object>> list = jdbcTemplateOne.queryForList(sql);
        return list;
    }

    /**
     * db2 数据库处理操作
     *
     * @return 数据集合
     */
    public List<Map<String, Object>> getUserList2() {
        String sql = "SELECT * from users";
        List<Map<String, Object>> list = jdbcTemplateTwo.queryForList(sql);
        return list;
    }

    /**
     * db1 执行sql
     *
     * @param sql sql
     */
    public void executeSql1(String sql) {
        jdbcTemplateOne.execute(sql);
    }

    /**
     * db2 执行sql
     *
     * @param sql sql
     */
    public void executeSql2(String sql) {
        jdbcTemplateTwo.execute(sql);
    }

}
