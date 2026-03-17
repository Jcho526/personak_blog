package com.example.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog.entity.ArticleLike;
import com.example.blog.mapper.ArticleLikeMapper;
import com.example.blog.service.ArticleLikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleLikeServiceImpl extends ServiceImpl<ArticleLikeMapper, ArticleLike> implements ArticleLikeService {

    @Override
    public boolean isLiked(Long articleId, Long userId) {
        return baseMapper.existsByArticleIdAndUserId(articleId, userId);
    }

    @Override
    public int getLikeCount(Long articleId) {
        return baseMapper.countByArticleId(articleId);
    }

    @Override
    @Transactional
    public boolean likeArticle(Long articleId, Long userId) {
        if (isLiked(articleId, userId)) {
            return false;
        }
        ArticleLike like = new ArticleLike();
        like.setArticleId(articleId);
        like.setUserId(userId);
        return save(like);
    }

    @Override
    @Transactional
    public boolean unlikeArticle(Long articleId, Long userId) {
        LambdaQueryWrapper<ArticleLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleLike::getArticleId, articleId)
               .eq(ArticleLike::getUserId, userId);
        return remove(wrapper);
    }
}
