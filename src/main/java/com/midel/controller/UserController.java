package com.midel.controller;


import com.midel.dto.user.UserRequestDto;
import com.midel.response.RestResponse;
import com.midel.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
@Tag(name = "User management")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get friends list for an authorized user")
    @GetMapping("/friends")
    public ResponseEntity<?> getFriends() {
        return new RestResponse(
                HttpStatus.OK,
                userService.getFriends()
        ).getResponseEntity();
    }

    @Operation(summary = "Add another user as a friend of an authorized user")
    @PostMapping("/friends")
    public ResponseEntity<Void> addFriend(@RequestBody UserRequestDto userRequestDto) {

        userService.addFriend(userRequestDto.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @Operation(hidden = true)
    @GetMapping("genfriend")
    public ResponseEntity<?> generateFriends() {
        userService.generateFriends();

        return ResponseEntity.ok("");
    }

}
