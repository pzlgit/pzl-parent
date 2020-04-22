package com.pzl.program.frametool.mysql.mybatis;

import com.pzl.program.frametool.mysql.data.User;
import org.apache.ibatis.annotations.*;


/**
 * mybatis注解版实现sql查询
 *
 * @author pzl
 * @date 2020-04-17
 */
@Mapper
public interface MybatisService {

    @Select("select * from users where id=#{id}")
    User getUserById(Integer id);

    @Delete("delete from users where id=#{id}")
    int deleteUserById(Integer id);

    //主键回显
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into users(name) values(#{name})")
    int insertUser(User user);

    @Update("update users set name=#{name} where id=#{id}")
    int updateUser(User user);

}
