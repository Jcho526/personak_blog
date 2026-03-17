package com.example.blog.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AddCommentDto {

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 2000, message = "评论内容不能超过2000字符")
    private String content;

    /**
     * 回复某条评论时传入父评论ID；不回复则为 null
     */
    private Long parentId;
}

