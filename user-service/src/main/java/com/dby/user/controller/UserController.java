package com.dby.user.controller;

import com.dby.common.dto.ApiResponse;
import com.dby.user.model.RegisterRequest;
import com.dby.user.model.UserResponse;
import com.dby.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户 REST API 控制器
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("注册成功", user));
    }

    /**
     * 根据 ID 查询用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    /**
     * 根据用户名查询用户
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserByUsername(username)));
    }

    /**
     * 查询所有用户
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers()));
    }

    /**
     * 更新用户昵称
     */
    @PatchMapping("/{id}/nickname")
    public ResponseEntity<ApiResponse<UserResponse>> updateNickname(
            @PathVariable Long id,
            @RequestParam String nickname) {
        return ResponseEntity.ok(ApiResponse.success("昵称更新成功", userService.updateNickname(id, nickname)));
    }

    /**
     * 禁用用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return ResponseEntity.ok(ApiResponse.success("用户已禁用", null));
    }
}
