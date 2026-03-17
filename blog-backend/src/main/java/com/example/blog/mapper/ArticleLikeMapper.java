package com.example.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blog.entity.ArticleLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArticleLikeMapper extends BaseMapper<ArticleLike> {

    @Select("SELECT COUNT(*) FROM article_like WHERE article_id = #{articleId}")
    int countByArticleId(@Param("articleId") Long articleId);

    @Select("SELECT COUNT(*) > 0 FROM article_like WHERE article_id = #{articleId} AND user_id = #{userId}")
    boolean existsByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);
}
