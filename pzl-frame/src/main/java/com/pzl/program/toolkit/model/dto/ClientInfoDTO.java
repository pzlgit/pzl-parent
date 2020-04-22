package com.pzl.program.toolkit.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "ClientInfoDTO", description = "封装通用的客户端请求")
public class ClientInfoDTO implements Serializable {

    private static final long serialVersionUID = -1684344655038378558L;

    @ApiModelProperty(value = "唯一标示", dataType = "string")
    private String kid;
    @ApiModelProperty(value = "用户标示", dataType = "string")
    private String userId;
    @ApiModelProperty(value = "请求发送时间", dataType = "string")
    private String reqTime;
    @ApiModelProperty(value = "请求响应时间", dataType = "string")
    private String respTime;
    @ApiModelProperty(value = "请求端ip", dataType = "string")
    private String clientIp;
    @ApiModelProperty(value = "客户端os系统", dataType = "string")
    private String platform;
    @ApiModelProperty(value = "一页查询的条目数(默认10,最大50)", dataType = "integer")
    private Integer pageSize;
    @ApiModelProperty(value = "查询页号,默认为1", dataType = "integer")
    private Integer pageNo;
    @ApiModelProperty(value = "评分", dataType = "double")
    private Double score;
    @ApiModelProperty(value = "类型ID,字段大写,需要相关配置解决", dataType = "string")
    @JsonProperty
    private List<String> POITypeID;

    @JsonIgnore
    public List<String> getPOITypeID() {
        return POITypeID;
    }

    @JsonIgnore
    public void setPOITypeID(List<String> POITypeID) {
        this.POITypeID = POITypeID;
    }

}
