package com.example.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVo {
    private Long id;
    private Long articleId;
    private Long userId;
    private String username;
    private String avatar;
    private String content;
    private Long parentId;
    private String status;
    private LocalDateTime createdAt;
}

