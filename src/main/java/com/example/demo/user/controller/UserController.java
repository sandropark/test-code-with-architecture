package com.example.demo.user.controller;

import com.example.demo.user.controller.port.UserService;
import com.example.demo.user.controller.request.UserUpdateRequest;
import com.example.demo.user.controller.response.MyProfileResponse;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@Builder
@Tag(name = "유저(users)")
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;

    @ResponseStatus
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable long id) {
        return ResponseEntity
                .ok()
                .body(UserResponse.fromModel(userService.getById(id)));
    }

    @GetMapping("/{id}/verify")
    public ResponseEntity<Void> verifyEmail(
            @PathVariable long id,
            @RequestParam String certificationCode) {
        userService.verifyEmail(id, certificationCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:3000"))
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<MyProfileResponse> getMyInfo(
            @Parameter(name = "EMAIL", in = ParameterIn.HEADER)
            @RequestHeader("EMAIL") String email // 일반적으로 스프링 시큐리티를 사용한다면 UserPrincipal 에서 가져옵니다.
    ) {
        User user = userService.getByEmail(email);
        User logined = userService.login(user);
        return ResponseEntity
                .ok()
                .body(MyProfileResponse.fromModel(logined));
    }

    @PutMapping("/me")
    @Parameter(in = ParameterIn.HEADER, name = "EMAIL")
    public ResponseEntity<MyProfileResponse> updateMyInfo(
            @Parameter(name = "EMAIL", in = ParameterIn.HEADER)
            @RequestHeader("EMAIL") String email, // 일반적으로 스프링 시큐리티를 사용한다면 UserPrincipal 에서 가져옵니다.
            @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        User user = userService.getByEmail(email);
        User updatedUser = userService.update(user, userUpdateRequest.toModel());
        return ResponseEntity
                .ok()
                .body(MyProfileResponse.fromModel(updatedUser));
    }

}