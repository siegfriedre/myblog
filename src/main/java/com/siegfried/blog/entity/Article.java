package com.siegfried.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 实体类，和数据库表做一个映射
 *
 * Created by zy_zhu on 2021/4/21.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_article")
public class Article {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * author
     */
    private Integer userId;

    /**
     * category
     */
    private Integer categoryId;

    /**
     * articleCover
     */
    private String articleCover;

    /**
     * title
     */
    private String articleTitle;

    /**
     * content
     */
    private String articleContent;

    /**
     * createTime
     */
    private Date createTime;

    /**
     * updateTime
     */
    private Date updateTime;

    /**
     * top
     */
    private Integer isTop;

    /**
     * draft
     */
    private Integer isDraft;

    /**
     * statusCode
     */
    private Integer isDelete;
}
