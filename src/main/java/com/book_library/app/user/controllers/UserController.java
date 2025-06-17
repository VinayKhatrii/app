package com.book_library.app.user.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.book_library.app.core.controller.ApiResponse;
import com.book_library.app.user.dtos.UserLoginDTO;
import com.book_library.app.user.dtos.UserRegisterDTO;
import com.book_library.app.user.services.AuthService;
import com.book_library.app.user.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDTO userLoginDTO) {
        return ResponseEntity.ok(new ApiResponse<>(true, "User Logged In Successfully", authService.loginUser(userLoginDTO)));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        return ResponseEntity.ok(new ApiResponse<>(true, "User Logged In Successfully", authService.registerUser(userRegisterDTO)));
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Users Loaded Successfully", userService.getAllUsers()));
    }

    @GetMapping("/membership-end-date")
    public ResponseEntity<?> getMembershipEndDate() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Membership End Date Loaded Successfully", userService.getMembershipEndDate()));
    }
    
    @GetMapping("/extend-membership/{months}")
    public ResponseEntity<?> extendMembership(@PathVariable int months) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Membership Extended Successfully", userService.extendMembership(months)));
    }
}
