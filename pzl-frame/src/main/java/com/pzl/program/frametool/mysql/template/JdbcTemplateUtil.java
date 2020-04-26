package com.pzl.program.frametool.mysql.template;

import com.pzl.program.frametool.mysql.data.User;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.UserException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * jdbcTemplate 操作数据库工具类
 *
 * @author pzl
 * @date 2020-04-13
 */
@Slf4j
@Repository
public class JdbcTemplateUtil {

    @Resource
    @Qualifier("jdbcTemplateOne")
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取用户列表
     * readOnly = true只读
     *
     * @return 数据集合
     * @throws Exception ex
     */
    @Transactional(readOnly = true)
    public List<User> getUserList() throws Exception {
        List<User> userList = jdbcTemplate.query("select id,name,email from users", new UserRowMapper());
        log.info("用户列表数据{}", userList);
        return userList;
    }

    /**
     * 根据用户id获取用户
     *
     * @param id id
     * @return 数据对象
     * @throws Exception ex
     */
    @Transactional(readOnly = true)
    public User getUserById(Integer id) throws Exception {
        // queryForObject:找不到会报异常  query:找不到则Null
        //User user=jdbcTemplate.queryForObject("select id,name,email from users where id=?",new Object[]{id},new UserRowMapper());
        List<User> userList = jdbcTemplate.query("select id,name,email from users where id=?", new Object[]{id}, new UserRowMapper());
        User user = null;
        if (!userList.isEmpty()) {
            user = userList.get(0);
        }
        log.info("用户数据{}", user);
        return user;
    }

    /**
     * 根据用户名查找用户-用于判断用户是否存在
     *
     * @param user 数据对象
     * @return 数据对象
     * @throws Exception ex
     */
    public User getUserByUserName(final User user) throws Exception {
        String sql = "select id,name,email from users where name=?";
        List<User> queryList = jdbcTemplate.query(sql, new UserRowMapper(), new Object[]{user.getName()});
        if (queryList != null && queryList.size() > 0) {
            return queryList.get(0);
        } else {
            return null;
        }
    }

    /**
     * 获取记录数
     *
     * @return count
     * @throws Exception ex
     */
    public Integer getCount() throws Exception {
        String sql = "select count(id) from users";
        //jdbctemplate 提供了相应的函数，无需直接写语句
        int maxRows = jdbcTemplate.getMaxRows();
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class);
        log.info("操作结果记录数{}", total);
        return total;
    }

    /**
     * 插入用户数据-会存在sql注入问题
     *
     * @param user 用户数据
     * @return int
     * @throws Exception ex
     */
    public int saveUser(final User user) throws Exception {
        int resRow = jdbcTemplate.update("INSERT INTO users(id,name,email) VALUES(NULL,?,?)", new Object[]{
                user.getName(), user.getEmail()
        });
        log.info("操作结果记录数:{}", resRow);
        return resRow;
    }

    /**
     * 插入用户数据-防止sql注入(推荐)
     *
     * @param user 数据对象
     * @return int
     * @throws Exception ex
     */
    public int saveUserWithSafe(final User user) throws Exception {
        int resRow = jdbcTemplate.update("INSERT INTO users(id,name,email) VALUES(NULL,?,?)", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail());
            }
        });
        log.info("操作结果记录数:{}", resRow);
        return resRow;
    }

    /**
     * 插入用户数据-防止sql注入-可以返回该条记录的主键（注意需要指定主键）
     *
     * @param user 数据对象
     * @return 主键
     * @throws Exception ex
     */
    @Transactional(rollbackFor = UserException.class)
    public int saveUserWithKey(final User user) throws Exception {
        String sql = "INSERT INTO users(id,name,email) VALUES(NULL,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int resRow = jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id"});  //指定 id 为主键
                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail());
                return ps;
            }
        }, keyHolder);
        log.info("操作结果记录数:{},主键{}", resRow, keyHolder.getKey());
        return Integer.parseInt(keyHolder.getKey().toString());
    }

    /**
     * 更新用户信息-防止sql注入
     *
     * @param user 数据对象
     * @return int
     */
    public int updateUserById(final User user) throws Exception {
        String sql = "update users set name=?,email=? where id=?";
        int resRow = jdbcTemplate.update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getEmail());
                preparedStatement.setInt(3, user.getId());
            }
        });
        log.info("操作结果记录数:{}", resRow);
        return resRow;
    }

    /**
     * 删除用户-防止sql注入
     *
     * @param user 数据对象
     * @return int
     * @throws Exception ex
     */
    public int deleteUserById(final User user) throws Exception {
        int resRow = jdbcTemplate.update("DELETE FROM users WHERE id=?", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, user.getId());
            }
        });
        log.info("操作结果记录数:{}", resRow);
        return resRow;
    }

}

/**
 * 行映射
 */
class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setName(resultSet.getString("name"));
        user.setEmail(resultSet.getString("email"));
        return user;
    }

}