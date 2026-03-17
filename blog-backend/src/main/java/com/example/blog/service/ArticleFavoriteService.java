package com.example.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blog.entity.ArticleFavorite;

public interface ArticleFavoriteService extends IService<ArticleFavorite> {
    boolean isFavorited(Long articleId, Long userId);
    int getFavoriteCount(Long articleId);
    boolean favoriteArticle(Long articleId, Long userId);
    boolean unfavoriteArticle(Long articleId, Long userId);
}
