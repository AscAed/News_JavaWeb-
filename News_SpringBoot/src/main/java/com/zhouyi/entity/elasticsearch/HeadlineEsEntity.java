package com.zhouyi.entity.elasticsearch;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 搜索专用实体类（Elasticsearch存储）
 */
@Data
@Document(indexName = "headline_index")
public class HeadlineEsEntity {

    @Id
    private Integer hid; // 对应 MySQL 中的主键 ID

    // analyzer="ik_max_word" 表示存入时细粒度拆分，searchAnalyzer="ik_smart" 表示搜索时粗粒度拆分
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", fielddata = true)
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String article; // 对应新闻正文内容

    @Field(type = FieldType.Keyword)
    private String typeName; // 新闻类型名称

    @Field(type = FieldType.Integer)
    private Integer type; // 新闻类型 ID

    @Field(type = FieldType.Integer)
    private Integer pageViews; // 浏览量
}
