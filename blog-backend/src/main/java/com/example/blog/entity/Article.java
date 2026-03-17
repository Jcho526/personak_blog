package com.example.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("article")
public class Article {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "title")
    private String title;

    @TableField(value = "content")
    private String content;

    @TableField(value = "excerpt")
    private String excerpt;

    @TableField(value = "cover")
    private String cover;

    @TableField(value = "author_id")
    private Long authorId;

    @TableField(value = "category_id")
    private Long categoryId;

    @TableField(value = "status")
    private String status;

    @TableField(value = "view_count")
    private Integer viewCount;

    @TableField(value = "like_count")
    private Integer likeCount;

    @TableField(value = "comment_count")
    private Integer commentCount;

    @TableField(value = "is_top")
    private Integer isTop;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String categoryName;

    @TableField(exist = false)
    private List<Tag> tags;
}
