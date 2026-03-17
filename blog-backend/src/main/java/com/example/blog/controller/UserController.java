package com.example.blog.controller;

import com.example.blog.common.response.Result;
import com.example.blog.common.security.LoginUser;
import com.example.blog.dto.UpdateProfileDto;
import com.example.blog.dto.ChangePasswordDto;
import com.example.blog.entity.User;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${upload.path:uploads}")
    private String uploadPath;

    @GetMapping("/info")
    public Result<User> getUserInfo(@AuthenticationPrincipal LoginUser loginUser) {
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        User user = userService.getById(loginUser.getId());
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PutMapping("/profile")
    public Result<User> updateProfile(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody UpdateProfileDto updateProfileDto) {
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }

        User user = userService.getById(loginUser.getId());
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        if (!user.getUsername().equals(updateProfileDto.getUsername())) {
            User existingUser = userService.findByUsername(updateProfileDto.getUsername());
            if (existingUser != null) {
                return Result.error(400, "用户名已被使用");
            }
        }

        if (!user.getEmail().equals(updateProfileDto.getEmail())) {
            User existingUser = userService.findByEmail(updateProfileDto.getEmail());
            if (existingUser != null) {
                return Result.error(400, "邮箱已被使用");
            }
        }

        user.setUsername(updateProfileDto.getUsername());
        user.setEmail(updateProfileDto.getEmail());
        user.setBio(updateProfileDto.getBio());

        userService.updateById(user);
        user.setPassword(null);

        return Result.success(user);
    }

    @PutMapping("/password")
    public Result<String> changePassword(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }

        boolean success = userService.changePassword(
                loginUser.getId(),
                changePasswordDto.getOldPassword(),
                changePasswordDto.getNewPassword()
        );

        if (success) {
            return Result.success("密码修改成功");
        } else {
            return Result.error(400, "当前密码错误");
        }
    }

    @PostMapping("/avatar")
    public Result<String> uploadAvatar(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam("file") MultipartFile file) {
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }

        if (file == null || file.isEmpty()) {
            return Result.error(400, "请选择文件");
        }

        try {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error(400, "只能上传图片文件");
            }

            String originalFilename = file.getOriginalFilename();
            String extension = ".jpg";
            if (originalFilename != null && originalFilename.lastIndexOf(".") > 0) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;

            File uploadDir = new File(uploadPath + File.separator + "avatars");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            File destFile = new File(uploadDir, filename);
            file.transferTo(destFile.getAbsoluteFile());

            String avatarUrl = "/uploads/avatars/" + filename;

            User user = userService.getById(loginUser.getId());
            if (user != null) {
                user.setAvatar(avatarUrl);
                userService.updateById(user);
            }

            return Result.success(avatarUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(500, "文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "系统错误: " + e.getMessage());
        }
    }
}
