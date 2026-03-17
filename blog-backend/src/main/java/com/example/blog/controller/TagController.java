package com.example.blog.controller;

import com.example.blog.common.response.Result;
import com.example.blog.entity.Article;
import com.example.blog.entity.Tag;
import com.example.blog.service.ArticleService;
import com.example.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @Autowired
    private ArticleService articleService;

    @GetMapping
    public Result<List<Tag>> getAllTags() {
        return Result.success(tagService.getAllTags());
    }

    @GetMapping("/{id}/articles")
    public Result<List<Article>> getArticlesByTag(@PathVariable Long id) {
        return Result.success(articleService.getArticlesByTag(id));
    }
}
