package com.example.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.blog.common.response.Result;
import com.example.blog.common.security.LoginUser;
import com.example.blog.dto.AddCommentDto;
import com.example.blog.service.CommentService;
import com.example.blog.vo.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/articles/{articleId}/comments")
    public Result<IPage<CommentVo>> getCommentsByArticleId(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return Result.success(commentService.getCommentsByArticleId(articleId, page, size));
    }

    @PostMapping("/articles/{articleId}/comments")
    public Result<CommentVo> addComment(
            @PathVariable Long articleId,
            @Valid @RequestBody AddCommentDto dto
    ) {
        Long userId = currentUserId();
        return Result.success(commentService.addComment(articleId, userId, dto));
    }

    private Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("Unauthorized");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser) {
            return ((LoginUser) principal).getId();
        }
        throw new RuntimeException("Unauthorized");
    }
}

