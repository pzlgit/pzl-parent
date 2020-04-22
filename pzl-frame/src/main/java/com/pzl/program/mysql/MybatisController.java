package com.pzl.program.mysql;

import com.pzl.program.frametool.mysql.data.User;
import com.pzl.program.frametool.mysql.datasource.DataManager;
import com.pzl.program.frametool.mysql.mybatis.MapperService;
import com.pzl.program.frametool.mysql.mybatis.MybatisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * mybatis使用服务
 *
 * @author pzl
 * @date 2020-04-17
 */
@RestController
public class MybatisController {

    @Autowired
    private MybatisService mybatisService;

    @Autowired
    private MapperService mapperService;

    @Autowired
    private DataManager dataManager;

    @GetMapping(value = "/users/get/{id}")
    public User getUserById(@PathVariable("id") Integer id) {
        //User user = mybatisService.getUserById(id);
        User user = mapperService.getUserById1(id);
        return user;
    }

    @PostMapping(value = "/users/update")
    public Integer updateUser(@RequestBody User user) {
        int i = mybatisService.updateUser(user);
        return i;
    }

    @PostMapping(value = "/users/insert")
    public Integer insertUser(@RequestBody User user) {
        int i = mybatisService.insertUser(user);
        return i;
    }

    @GetMapping(value = "/users/delete/{id}")
    public Integer deleteUserById(@PathVariable("id") Integer id) {
        int i = mybatisService.deleteUserById(id);
        return i;
    }

    @GetMapping(value = "/get1")
    public List<Map<String, Object>> get1() {
        List<Map<String, Object>> userList1 = dataManager.getUserList1();
        return userList1;
    }

    @GetMapping(value = "/get2")
    public List<Map<String, Object>> get2() {
        List<Map<String, Object>> userList1 = dataManager.getUserList2();
        return userList1;
    }


}