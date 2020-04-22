package com.pzl.program.frametool.elasticsearch;

import lombok.Data;

import java.util.Date;

@Data
public class UserInfo {

    private String name;

    private Integer age;

    private Float salary;

    private String address;

    private String remark;

    private Date createTime;

    private String birthday;

}
