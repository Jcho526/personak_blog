package com.example.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blog.entity.Tag;

import java.util.List;

public interface TagService extends IService<Tag> {
    List<Tag> getAllTags();
}
