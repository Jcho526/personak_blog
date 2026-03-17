package com.example.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blog.entity.ArticleLike;

public interface ArticleLikeService extends IService<ArticleLike> {
    boolean isLiked(Long articleId, Long userId);
    int getLikeCount(Long articleId);
    boolean likeArticle(Long articleId, Long userId);
    boolean unlikeArticle(Long articleId, Long userId);
}
