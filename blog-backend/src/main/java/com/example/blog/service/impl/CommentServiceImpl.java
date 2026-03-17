package com.example.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog.dto.AddCommentDto;
import com.example.blog.entity.Article;
import com.example.blog.entity.Comment;
import com.example.blog.entity.User;
import com.example.blog.mapper.CommentMapper;
import com.example.blog.mapper.UserMapper;
import com.example.blog.service.ArticleService;
import com.example.blog.service.CommentService;
import com.example.blog.vo.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ArticleService articleService;

    @Override
    public IPage<CommentVo> getCommentsByArticleId(Long articleId, int page, int size) {
        Page<Comment> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getArticleId, articleId)
                .eq(Comment::getStatus, "approved")
                .orderByAsc(Comment::getCreatedAt)
                .orderByAsc(Comment::getId);

        IPage<Comment> commentPage = page(mpPage, wrapper);
        List<Comment> records = commentPage.getRecords();

        Map<Long, User> userMap = loadUsers(records);

        List<CommentVo> voRecords = records.stream()
                .map(c -> toVo(c, userMap.get(c.getUserId())))
                .collect(Collectors.toList());

        Page<CommentVo> voPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        voPage.setRecords(voRecords);
        return voPage;
    }

    @Override
    @Transactional
    public CommentVo addComment(Long articleId, Long userId, AddCommentDto dto) {
        Article article = articleService.getById(articleId);
        if (article == null) {
            throw new RuntimeException("Article not found");
        }

        if (dto.getParentId() != null) {
            Comment parent = getById(dto.getParentId());
            if (parent == null || !Objects.equals(parent.getArticleId(), articleId)) {
                throw new RuntimeException("Parent comment not found");
            }
        }

        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(userId);
        comment.setContent(dto.getContent());
        comment.setParentId(dto.getParentId());
        comment.setStatus("approved");
        save(comment);

        Integer current = article.getCommentCount() == null ? 0 : article.getCommentCount();
        article.setCommentCount(current + 1);
        articleService.updateById(article);

        User user = userMapper.selectById(userId);
        return toVo(comment, user);
    }

    private Map<Long, User> loadUsers(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> userIds = comments.stream()
                .map(Comment::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectBatchIds(userIds).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));
    }

    private CommentVo toVo(Comment comment, User user) {
        CommentVo vo = new CommentVo();
        vo.setId(comment.getId());
        vo.setArticleId(comment.getArticleId());
        vo.setUserId(comment.getUserId());
        vo.setContent(comment.getContent());
        vo.setParentId(comment.getParentId());
        vo.setStatus(comment.getStatus());
        vo.setCreatedAt(comment.getCreatedAt());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setAvatar(user.getAvatar());
        }
        return vo;
    }
}

