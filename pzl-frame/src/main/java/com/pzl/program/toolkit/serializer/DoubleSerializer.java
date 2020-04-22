package com.pzl.program.toolkit.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Double 数字序列化器
 *
 * @author pzl
 */
public class DoubleSerializer extends JsonSerializer<Double> {

    /**
     * 将 double 数字以 BigDecimal 格式序列化， 防止出现科学计数法
     * 使用方法：
     * "@JsonSerialize(using = DoubleSerializer.class)
     * private Double distance;"
     *
     * @param value       序列化值
     * @param gen         JsonGenerator
     * @param serializers serializers
     * @throws IOException             ex
     * @throws JsonProcessingException ex
     */
    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException, JsonProcessingException {

        BigDecimal bigDecimal = new BigDecimal(value)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        gen.writeNumber(bigDecimal);
    }

}
