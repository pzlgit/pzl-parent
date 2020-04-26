package com.pzl.program.frametool.elasticsearch;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 模糊查询（fuzzy）
 */
@Slf4j
@Service
public class FuzzyQueryService {

    private final RestHighLevelClient restHighLevelClient;

    public FuzzyQueryService(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * 模糊查询所有以 “三” 结尾的姓名
     * eg:
     * {
     * "query": {
     * "fuzzy": {
     * "name": "三"
     * }
     * }
     * }
     */
    public void fuzzyQuery() {
        try {
            // 构建查询条件
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.fuzzyQuery("name", "三").fuzziness(Fuzziness.AUTO));
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
