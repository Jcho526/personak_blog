package com.example.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "article_id")
    private Long articleId;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "content")
    private String content;

    @TableField(value = "parent_id")
    private Long parentId;

    @TableField(value = "status")
    private String status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}