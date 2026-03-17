package com.example.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blog.dto.CreateArticleDto;
import com.example.blog.dto.UpdateArticleDto;
import com.example.blog.entity.Article;

import java.util.List;

public interface ArticleService extends IService<Article> {
    List<Article> getArticles();
    Article getArticleById(Long id);
    List<Article> getArticlesByCategory(Long categoryId);
    List<Article> getArticlesByTag(Long tagId);
    List<Article> getArticlesByAuthor(Long authorId);
    List<Article> getLikedArticles(Long userId);
    List<Article> getFavoritedArticles(Long userId);
    Article createArticle(CreateArticleDto dto, Long authorId);
    Article updateArticle(Long id, UpdateArticleDto dto, Long authorId);
    boolean deleteArticle(Long id, Long authorId);
    void incrementViewCount(Long id);
}
