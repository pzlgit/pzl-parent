package com.pzl.program.frametool.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.stereotype.Service;

/**
 * ES 索引操作
 */
@Slf4j
@Service
public class IndexService {

    private final RestHighLevelClient restHighLevelClient;

    public IndexService(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * 创建索引
     * eg:
     * {
     * "mappings": {
     * "doc": {
     * "dynamic": true,
     * "properties": {
     * "name": {
     * "type": "text",
     * "fields": {
     * "keyword": {
     * "type": "keyword"
     * }
     * }
     * },
     * "address": {
     * "type": "text",
     * "fields": {
     * "keyword": {
     * "type": "keyword"
     * }
     * }
     * },
     * "remark": {
     * "type": "text",
     * "fields": {
     * "keyword": {
     * "type": "keyword"
     * }
     * }
     * },
     * "age": {
     * "type": "integer"
     * },
     * "salary": {
     * "type": "float"
     * },
     * "birthDate": {
     * "type": "date",
     * "format": "yyyy-MM-dd"
     * },
     * "createTime": {
     * "type": "date"
     * }
     * }
     * }
     * }
     * }
     */
    public void createIndex() {
        try {
            XContentBuilder mapping = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("dynamic", true)
                    .startObject("properties")
                    .startObject("name")
                    .field("type", "text")
                    .startObject("fields")
                    .startObject("keyword")
                    .field("type", "keyword")
                    .endObject()
                    .endObject()
                    .endObject()

                    .startObject("address")
                    .field("type", "text")
                    .startObject("fields")
                    .startObject("keyword")
                    .field("type", "keyword")
                    .endObject()
                    .endObject()
                    .endObject()

                    .startObject("remark")
                    .field("type", "text")
                    .startObject("fields")
                    .startObject("keyword")
                    .field("type", "keyword")
                    .endObject()
                    .endObject()
                    .endObject()

                    .startObject("age")
                    .field("type", "integer")
                    .endObject()

                    .startObject("salary")
                    .field("type", "float")
                    .endObject()

                    .startObject("birthDate")
                    .field("type", "date")
                    .field("format", "yyyy-MM-dd")
                    .endObject()

                    .startObject("createTime")
                    .field("type", "date")
                    .endObject()
                    .endObject()
                    .endObject();

            //创建索引配置信息
            Settings settings = Settings.builder()
                    .put("index.number_of_shards", 1)
                    .put("index.number_of_replicas", 0)
                    .build();
            //新建创建索引请求对象，然后设置索引类型（7.0将不存在索引类型）和mapping与index配置
            CreateIndexRequest request = new CreateIndexRequest("mydlq-user");
            request.mapping(mapping);
            //执行创建索引
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            //判断是否创建成功
            boolean isCreated = createIndexResponse.isAcknowledged();
            log.info("是否创建成功：{}", isCreated);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除索引  DELETE /mydlq-user
     */
    public void deleteIndex() {
        try {
            DeleteIndexRequest request = new DeleteIndexRequest("mydlq-user");
            AcknowledgedResponse acknowledgedResponse = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
            //判断是否删除成功
            boolean isDeleted = acknowledgedResponse.isAcknowledged();
            log.info("是否删除成功：{}", isDeleted);
        } catch (Exception e) {
            log.error("", e);
        }
    }

}