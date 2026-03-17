package com.example.blog.controller;

import com.example.blog.common.response.Result;
import com.example.blog.common.security.LoginUser;
import com.example.blog.common.utils.JwtUtils;
import com.example.blog.dto.JwtResponse;
import com.example.blog.dto.LoginDto;
import com.example.blog.dto.RegisterDto;
import com.example.blog.entity.User;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result<JwtResponse> login(@Valid @RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        LoginUser userDetails = (LoginUser) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails);

        return Result.success(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getAvatar(),
                userDetails.getBio(),
                userDetails.getAuthorities().stream().findFirst().get().getAuthority()
        ));
    }

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterDto registerDto) {
        userService.register(registerDto);
        return Result.success("User registered successfully!");
    }
}
