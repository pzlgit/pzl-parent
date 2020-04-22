package com.pzl.program.toolkit.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pzl.program.toolkit.serializer.DoubleSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString(callSuper = true)
@ApiModel(value = "ResultVO", description = "ResultVO返回格式")
public class ResultVO implements Serializable {

    private static final long serialVersionUID = 4915223683512780736L;

    @JsonProperty("ID")
    @ApiModelProperty(value = "唯一ID,需要处理大写问题")
    private String ID;

    @JsonProperty("POIType")
    @ApiModelProperty(value = "类型")
    private List<String> POIType;

    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 应用 自定义 double 序列化器，防止科学计数法
     */
    @ApiModelProperty(value = "距离")
    @JsonSerialize(using = DoubleSerializer.class)
    private Double distance;

    @ApiModelProperty(value = "子信息")
    private List<ChildInfo> childInfo;

    @Data
    public static class ChildInfo implements Serializable {

        private static final long serialVersionUID = 9043667441025359114L;

        @ApiModelProperty(value = "名称")
        private String name;

    }

    @JsonIgnore
    public String getID() {
        return ID;
    }

    @JsonIgnore
    public void setID(String ID) {
        this.ID = ID;
    }

}
