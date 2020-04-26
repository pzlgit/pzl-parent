package com.pzl.program.frametool.elasticsearch;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 布尔查询（bool）
 */
@Slf4j
@Service
public class BoolQueryService {

    private final RestHighLevelClient restHighLevelClient;

    public BoolQueryService(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * 查询出生在 1990-1995 年期间，且地址在 北京市昌平区、北京市大兴区、北京市房山区 的员工信息：
     * eg：
     * {
     * "query": {
     * "bool": {
     * "filter": {
     * "range": {
     * "birthDate": {
     * "format": "yyyy",
     * "gte": 1990,
     * "lte": 1995
     * }
     * }
     * },
     * "must": [
     * {
     * "terms": {
     * "address.keyword": [
     * "北京市昌平区",
     * "北京市大兴区",
     * "北京市房山区"
     * ]
     * }
     * }
     * ]
     * }
     * }
     * }
     */
    public void boolQuery() {
        try {
            // 创建 Bool 查询构建器
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            // 构建查询条件
            boolQueryBuilder.must(QueryBuilders.termsQuery("address.keyword", "北京市昌平区", "北京市大兴区", "北京市房山区"))
                    .filter().add(QueryBuilders.rangeQuery("birthDate").format("yyyy").gte("1990").lte("1995"));
            // 构建查询源构建器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(boolQueryBuilder);
            // 创建查询请求对象，将查询对象配置到其中
            SearchRequest searchRequest = new SearchRequest("mydlq-user");
            searchRequest.source(searchSourceBuilder);
            // 执行查询，然后处理响应结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 根据状态和数据条数验证是否返回了数据
            if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getHits().length > 0) {
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits) {
                    // 将 JSON 转换成对象
                    UserInfo userInfo = JSON.parseObject(hit.getSourceAsString(), UserInfo.class);
                    // 输出查询信息
                    log.info(userInfo.toString());
                }
            }
        } catch (IOException e) {
            log.error("", e);
        }
    }

}