package com.example.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog.entity.ArticleFavorite;
import com.example.blog.mapper.ArticleFavoriteMapper;
import com.example.blog.service.ArticleFavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleFavoriteServiceImpl extends ServiceImpl<ArticleFavoriteMapper, ArticleFavorite> implements ArticleFavoriteService {

    @Override
    public boolean isFavorited(Long articleId, Long userId) {
        return baseMapper.existsByArticleIdAndUserId(articleId, userId);
    }

    @Override
    public int getFavoriteCount(Long articleId) {
        return baseMapper.countByArticleId(articleId);
    }

    @Override
    @Transactional
    public boolean favoriteArticle(Long articleId, Long userId) {
        if (isFavorited(articleId, userId)) {
            return false;
        }
        ArticleFavorite favorite = new ArticleFavorite();
        favorite.setArticleId(articleId);
        favorite.setUserId(userId);
        return save(favorite);
    }

    @Override
    @Transactional
    public boolean unfavoriteArticle(Long articleId, Long userId) {
        LambdaQueryWrapper<ArticleFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleFavorite::getArticleId, articleId)
               .eq(ArticleFavorite::getUserId, userId);
        return remove(wrapper);
    }
}
