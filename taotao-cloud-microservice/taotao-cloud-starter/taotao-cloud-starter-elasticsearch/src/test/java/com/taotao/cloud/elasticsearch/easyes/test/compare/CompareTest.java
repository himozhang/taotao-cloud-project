package com.taotao.cloud.elasticsearch.easyes.test.compare;

import com.taotao.cloud.elasticsearch.easyes.sample.entity.Document;
import com.taotao.cloud.elasticsearch.easyes.sample.mapper.DocumentMapper;
import com.taotao.cloud.elasticsearch.easyes.test.TestEasyEsApplication;
import com.xpc.easyes.core.conditions.LambdaEsQueryWrapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

/**
 * 对比测试
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestEasyEsApplication.class)
public class CompareTest {
    @Autowired
    private DocumentMapper documentMapper;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void testCompare() {
        // 需求:查询出文档标题为 "中国功夫"且作者为"老汉"的所有文档
        // 传统方式, 直接用RestHighLevelClient进行查询 需要11行代码,还不包含解析代码
        String indexName = "document";
        SearchRequest searchRequest = new SearchRequest(indexName);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        TermQueryBuilder titleTerm = QueryBuilders.termQuery("title", "中国功夫");
        TermsQueryBuilder creatorTerm = QueryBuilders.termsQuery("creator", "老汉");
        boolQueryBuilder.must(titleTerm);
        boolQueryBuilder.must(creatorTerm);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 然后从searchResponse中通过各种方式解析出DocumentList 省略这些代码...
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 使用Easy-ES 仅需3行
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(Document::getTitle, "中国功夫").eq(Document::getCreator, "老汉");
        List<Document> documents = documentMapper.selectList(wrapper);

        // 如果查询条件更为复杂,且包含高亮权重等高阶语法 采用传统方式代码量与直接用Easy-Es差距更为明显
    }
}
