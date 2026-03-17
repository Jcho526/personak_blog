package com.example.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("article_favorite")
public class ArticleFavorite {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "article_id")
    private Long articleId;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
