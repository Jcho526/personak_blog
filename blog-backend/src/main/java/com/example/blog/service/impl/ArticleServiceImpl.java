package com.example.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog.dto.CreateArticleDto;
import com.example.blog.dto.UpdateArticleDto;
import com.example.blog.entity.*;
import com.example.blog.mapper.*;
import com.example.blog.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private ArticleLikeMapper articleLikeMapper;

    @Autowired
    private ArticleFavoriteMapper articleFavoriteMapper;

    @Override
    public List<Article> getArticles() {
        List<Article> articles = list(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, "published")
                .orderByDesc(Article::getIsTop)
                .orderByDesc(Article::getCreatedAt));
        populateArticleDetails(articles);
        return articles;
    }

    @Override
    public Article getArticleById(Long id) {
        Article article = getById(id);
        if (article != null) {
            populateArticleDetails(java.util.Collections.singletonList(article));
        }
        return article;
    }

    @Override
    public List<Article> getArticlesByCategory(Long categoryId) {
        List<Article> articles = list(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, "published")
                .eq(Article::getCategoryId, categoryId)
                .orderByDesc(Article::getCreatedAt));
        populateArticleDetails(articles);
        return articles;
    }

    @Override
    public List<Article> getArticlesByTag(Long tagId) {
        List<ArticleTag> articleTags = articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTag>()
                .eq(ArticleTag::getTagId, tagId));
        
        if (articleTags.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> articleIds = articleTags.stream()
                .map(ArticleTag::getArticleId)
                .collect(Collectors.toList());

        List<Article> articles = list(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, "published")
                .in(Article::getId, articleIds)
                .orderByDesc(Article::getCreatedAt));
        
        populateArticleDetails(articles);
        return articles;
    }

    @Override
    public List<Article> getArticlesByAuthor(Long authorId) {
        List<Article> articles = list(new LambdaQueryWrapper<Article>()
                .eq(Article::getAuthorId, authorId)
                .orderByDesc(Article::getCreatedAt));
        populateArticleDetails(articles);
        return articles;
    }

    @Override
    public List<Article> getLikedArticles(Long userId) {
        List<ArticleLike> likes = articleLikeMapper.selectList(
                new LambdaQueryWrapper<ArticleLike>().eq(ArticleLike::getUserId, userId));
        
        if (likes.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> articleIds = likes.stream()
                .map(ArticleLike::getArticleId)
                .collect(Collectors.toList());

        List<Article> articles = list(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, "published")
                .in(Article::getId, articleIds)
                .orderByDesc(Article::getCreatedAt));
        
        populateArticleDetails(articles);
        return articles;
    }

    @Override
    public List<Article> getFavoritedArticles(Long userId) {
        List<ArticleFavorite> favorites = articleFavoriteMapper.selectList(
                new LambdaQueryWrapper<ArticleFavorite>().eq(ArticleFavorite::getUserId, userId));
        
        if (favorites.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> articleIds = favorites.stream()
                .map(ArticleFavorite::getArticleId)
                .collect(Collectors.toList());

        List<Article> articles = list(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, "published")
                .in(Article::getId, articleIds)
                .orderByDesc(Article::getCreatedAt));
        
        populateArticleDetails(articles);
        return articles;
    }

    @Override
    @Transactional
    public Article createArticle(CreateArticleDto dto, Long authorId) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setExcerpt(dto.getExcerpt() != null ? dto.getExcerpt() : generateExcerpt(dto.getContent()));
        article.setAuthorId(authorId);
        article.setCategoryId(dto.getCategoryId());
        article.setStatus(dto.getStatus() != null ? dto.getStatus() : "draft");
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        article.setIsTop(0);

        save(article);

        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            updateArticleTags(article.getId(), dto.getTagIds());
        }

        return article;
    }

    @Override
    @Transactional
    public Article updateArticle(Long id, UpdateArticleDto dto, Long authorId) {
        Article article = getById(id);
        if (article == null) {
            return null;
        }
        
        if (!article.getAuthorId().equals(authorId)) {
            return null;
        }

        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setExcerpt(dto.getExcerpt() != null ? dto.getExcerpt() : generateExcerpt(dto.getContent()));
        article.setCategoryId(dto.getCategoryId());
        if (dto.getStatus() != null) {
            article.setStatus(dto.getStatus());
        }

        updateById(article);

        if (dto.getTagIds() != null) {
            articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>()
                    .eq(ArticleTag::getArticleId, id));
            updateArticleTags(id, dto.getTagIds());
        }

        return article;
    }

    @Override
    @Transactional
    public boolean deleteArticle(Long id, Long authorId) {
        Article article = getById(id);
        if (article == null) {
            return false;
        }
        
        if (!article.getAuthorId().equals(authorId)) {
            return false;
        }

        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>()
                .eq(ArticleTag::getArticleId, id));
        
        return removeById(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        Article article = getById(id);
        if (article != null) {
            article.setViewCount(article.getViewCount() + 1);
            updateById(article);
        }
    }

    private void updateArticleTags(Long articleId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            ArticleTag articleTag = new ArticleTag();
            articleTag.setArticleId(articleId);
            articleTag.setTagId(tagId);
            articleTagMapper.insert(articleTag);
        }
    }

    private String generateExcerpt(String content) {
        if (content == null) return "";
        String text = content.replaceAll("<[^>]*>", "").replaceAll("#|\\*|`|~~", "");
        return text.length() > 200 ? text.substring(0, 200) + "..." : text;
    }

    private void populateArticleDetails(List<Article> articles) {
        for (Article article : articles) {
            if (article.getCategoryId() != null) {
                Category category = categoryMapper.selectById(article.getCategoryId());
                if (category != null) {
                    article.setCategoryName(category.getName());
                }
            }

            List<ArticleTag> articleTags = articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTag>()
                    .eq(ArticleTag::getArticleId, article.getId()));
            
            if (!articleTags.isEmpty()) {
                List<Long> tagIds = articleTags.stream()
                        .map(ArticleTag::getTagId)
                        .collect(Collectors.toList());
                List<Tag> tags = tagMapper.selectBatchIds(tagIds);
                article.setTags(tags);
            }
        }
    }
}
