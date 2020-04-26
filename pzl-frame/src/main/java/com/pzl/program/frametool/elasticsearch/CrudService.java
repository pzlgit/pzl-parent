package com.pzl.program.frametool.elasticsearch;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

/**
 * elasticsearch 增删改查
 */
@Slf4j
@Service
public class CrudService {

    private final RestHighLevelClient restHighLevelClient;

    public CrudService(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * 增加文档信息
     * eg：
     * POST /mydlq-user/doc
     * {
     * "address": "北京市",
     * "age": 29,
     * "birthDate": "1990-01-10",
     * "createTime": 1579530727699,
     * "name": "张三",
     * "remark": "来自北京市的张先生",
     * "salary": 100
     * }
     */
    public void addDocument() {
        try {
            //创建索引请求对象
            IndexRequest indexRequest = new IndexRequest("mydlq-user");
            indexRequest.id("1");
            UserInfo userInfo = new UserInfo();
            userInfo.setName("张三");
            userInfo.setSalary(100.00f);
            userInfo.setAge(18);
            userInfo.setAddress("北京市");
            userInfo.setRemark("来自北京市的张先生");
            userInfo.setCreateTime(new Date());
            userInfo.setBirthday("1996-08-09");
            //将对象转换成byte数组
            byte[] bytes = JSON.toJSONBytes(userInfo);
            //设置文档内容
            indexRequest.source(bytes, XContentType.JSON);
            //执行增加文章
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            log.info("创建状态：{}", response.status());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文档信息
     * GET /mydlq-user/doc/1
     */
    public void getDocumet() {
        try {
            //获取请求对象
            GetRequest getRequest = new GetRequest("mydlq-user", "1");
            //获取文档信息
            GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            //将json转换成对象
            if (getResponse.isExists()) {
                UserInfo userInfo = JSON.parseObject(getResponse.getSourceAsBytes(), UserInfo.class);
                log.info("员工信息：{}", userInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新文档信息
     * 更新之前创建的 id=1 的文档信息。
     * PUT /mydlq-user/doc/1
     * {
     * "address": "北京市海淀区",
     * "age": 29,
     * "birthDate": "1990-01-10",
     * "createTime": 1579530727699,
     * "name": "张三",
     * "remark": "来自北京市的张先生",
     * "salary": 100
     * }
     */
    public void updateDocument() {
        try {
            //创建索引请求对象
            UpdateRequest updateRequest = new UpdateRequest("mydlq-user", "1");
            //设置员工更新信息
            UserInfo userInfo = new UserInfo();
            userInfo.setSalary(500.00f);
            userInfo.setAddress("北京市海淀区");
            //将对象转换成byte数组
            byte[] bytes = JSON.toJSONBytes(userInfo);
            //设置更新文档内容
            updateRequest.doc(bytes, XContentType.JSON);
            //执行更新文档
            UpdateResponse update = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            log.info("更新状态:{}", update.status());
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * 删除文档信息
     * 删除之前创建的 id=1 的文档信息。
     * DELETE /mydlq-user/doc/1
     */
    public void deleteDocument() {
        try {
            // 创建删除请求对象
            DeleteRequest deleteRequest = new DeleteRequest("mydlq-user", "1");
            // 执行删除文档
            DeleteResponse response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("删除状态：{}", response.status());
        } catch (IOException e) {
            log.error("", e);
        }
    }

}
