package com.midel.controller;


import com.midel.response.ErrorResponse;
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

    @Operation(summary = "Get all users")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return new RestResponse(
                HttpStatus.OK,
                userService.getAllUser()
            ).getResponseEntity();
    }

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
    public ResponseEntity<?> addFriend(@RequestBody Long friendId) {
        try {
            userService.addFriend(friendId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            ).getResponseEntity();
        }
    }

}
