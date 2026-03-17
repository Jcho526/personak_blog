package com.example.blog.controller;

import com.example.blog.common.response.Result;
import com.example.blog.common.security.LoginUser;
import com.example.blog.dto.CreateArticleDto;
import com.example.blog.dto.UpdateArticleDto;
import com.example.blog.entity.Article;
import com.example.blog.service.ArticleFavoriteService;
import com.example.blog.service.ArticleLikeService;
import com.example.blog.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleLikeService articleLikeService;

    @Autowired
    private ArticleFavoriteService articleFavoriteService;

    @GetMapping("/articles")
    public Result<List<Article>> getArticles() {
        return Result.success(articleService.getArticles());
    }

    @GetMapping("/articles/{id}")
    public Result<Map<String, Object>> getArticleById(
            @PathVariable Long id,
            @AuthenticationPrincipal LoginUser loginUser) {
        Article article = articleService.getArticleById(id);
        if (article == null) {
            return Result.error(404, "文章不存在");
        }
        
        articleService.incrementViewCount(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("article", article);
        
        if (loginUser != null) {
            try {
                result.put("isLiked", articleLikeService.isLiked(id, loginUser.getId()));
            } catch (Exception e) {
                result.put("isLiked", false);
            }
            try {
                result.put("isFavorited", articleFavoriteService.isFavorited(id, loginUser.getId()));
            } catch (Exception e) {
                result.put("isFavorited", false);
            }
        } else {
            result.put("isLiked", false);
            result.put("isFavorited", false);
        }
        
        return Result.success(result);
    }

    @PostMapping("/articles")
    public Result<Article> createArticle(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody CreateArticleDto dto) {
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        Article article = articleService.createArticle(dto, loginUser.getId());
        return Result.success(article);
    }

    @PutMapping("/articles/{id}")
    public Result<Article> updateArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody UpdateArticleDto dto) {
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        Article article = articleService.updateArticle(id, dto, loginUser.getId());
        if (article == null) {
            return Result.error(403, "无权修改此文章");
        }
        return Result.success(article);
    }

    @DeleteMapping("/articles/{id}")
    public Result<String> deleteArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal LoginUser loginUser) {
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        boolean success = articleService.deleteArticle(id, loginUser.getId());
        if (!success) {
            return Result.error(403, "无权删除此文章");
        }
        return Result.success("删除成功");
    }

    @GetMapping("/user/articles")
    public Result<List<Article>> getMyArticles(@AuthenticationPrincipal LoginUser loginUser) {
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        return Result.success(articleService.getArticlesByAuthor(loginUser.getId()));
    }

    @GetMapping("/user/likes")
    public Result<List<Article>> getMyLikedArticles(@AuthenticationPrincipal LoginUser loginUser) {
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        return Result.success(articleService.getLikedArticles(loginUser.getId()));
    }

    @GetMapping("/user/favorites")
    public Result<List<Article>> getMyFavoriteArticles(@AuthenticationPrincipal LoginUser loginUser) {
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        return Result.success(articleService.getFavoritedArticles(loginUser.getId()));
    }

    @PostMapping("/articles/{id}/like")
    public Result<String> likeArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal LoginUser loginUser) {
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        boolean success = articleLikeService.likeArticle(id, loginUser.getId());
        if (success) {
            return Result.success("点赞成功");
        }
        return Result.error(400, "已经点赞过了");
    }

    @DeleteMapping("/articles/{id}/like")
    public Result<String> unlikeArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal LoginUser loginUser) {
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        articleLikeService.unlikeArticle(id, loginUser.getId());
        return Result.success("取消点赞");
    }

    @PostMapping("/articles/{id}/favorite")
    public Result<String> favoriteArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal LoginUser loginUser) {
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        boolean success = articleFavoriteService.favoriteArticle(id, loginUser.getId());
        if (success) {
            return Result.success("收藏成功");
        }
        return Result.error(400, "已经收藏过了");
    }

    @DeleteMapping("/articles/{id}/favorite")
    public Result<String> unfavoriteArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal LoginUser loginUser) {
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        articleFavoriteService.unfavoriteArticle(id, loginUser.getId());
        return Result.success("取消收藏");
    }
}
