package com.pzl.program.toolkit.convert;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Bean 实体类转化工具类
 *
 * @author pzl
 * @date 2020-04-05
 */
@Slf4j
public class BeanConvertUtil {

    /**
     * 深拷贝 对象
     *
     * @param sourceObj      原始对象
     * @param sourceObjClass 原始对象类型
     * @param <T>            泛型
     * @return data
     */
    public static <T> T deepCopy(T sourceObj, Class<T> sourceObjClass) {
        if (Objects.isNull(sourceObj)) {
            return null;
        }
        try {
            return JSON.parseObject(JSON.toJSONString(sourceObj), sourceObjClass);
        } catch (Exception e) {
            log.error("深拷贝对象失败,原始对象为:" + JSON.toJSONString(sourceObj), e);
            throw new RuntimeException("深拷贝对象失败");
        }
    }

    /**
     * Bean转换
     *
     * @param sourceObj   源Bean对象
     * @param targetClass 转换后对象类
     * @return data
     */
    public static <S, T> T convert(S sourceObj, Class<T> targetClass) {
        if (sourceObj == null) {
            return null;
        }
        T instance = BeanUtils.instantiate(targetClass);
        // 允许出现这样的bean关系：
        // S和T 存在名字相同 且 数据类型不同 的字段
        // 效果：不会抛异常，在赋值操作时因为类型不能互转故自动忽略，即：该字段值为null
        BeanUtils.copyProperties(sourceObj, instance);
        return instance;
    }

    /**
     * 复制属性
     *
     * @param sourceObj 源Bean对象
     * @param destObj   目标Bean对象
     * @param <S>       泛型
     */
    public static <S> void copyProperties(S sourceObj, S destObj) {
        Assert.notNull(sourceObj, "原始对象不允许为空");
        Assert.notNull(destObj, "复制对象不允许为空");
        BeanUtils.copyProperties(sourceObj, destObj);
    }

    /**
     * Bean列表转换
     *
     * @param sourceList      源Bean对象列表
     * @param targetItemClass 转换后列表元素类型
     * @return data
     */
    public static <S, T> List<T> convert(List<S> sourceList, Class<T> targetItemClass) {
        if (sourceList == null) {
            return null;
        }
        if (sourceList.size() < 1) {
            return Lists.newArrayList();
        }
        return sourceList.stream().map(s -> convert(s, targetItemClass)).collect(Collectors.toList());
    }

}

