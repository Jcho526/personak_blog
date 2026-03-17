package com.example.blog.controller;

import com.example.blog.common.response.Result;
import com.example.blog.entity.Article;
import com.example.blog.entity.Category;
import com.example.blog.service.ArticleService;
import com.example.blog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleService articleService;

    @GetMapping
    public Result<List<Category>> getAllCategories() {
        return Result.success(categoryService.getAllCategories());
    }

    @GetMapping("/{id}/articles")
    public Result<List<Article>> getArticlesByCategory(@PathVariable Long id) {
        return Result.success(articleService.getArticlesByCategory(id));
    }
}
