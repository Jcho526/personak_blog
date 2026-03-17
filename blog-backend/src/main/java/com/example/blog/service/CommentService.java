package com.example.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blog.dto.AddCommentDto;
import com.example.blog.entity.Comment;
import com.example.blog.vo.CommentVo;

public interface CommentService extends IService<Comment> {

    IPage<CommentVo> getCommentsByArticleId(Long articleId, int page, int size);

    CommentVo addComment(Long articleId, Long userId, AddCommentDto dto);
}

