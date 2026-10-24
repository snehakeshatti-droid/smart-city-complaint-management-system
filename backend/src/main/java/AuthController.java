
package com.smartcity.complaint_system;

import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            JdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @RequestBody RegisterRequest request) {

        if (request.username == null
                || !Pattern.matches("[A-Za-z0-9_]{3,50}", request.username)) {
            return ResponseEntity.badRequest().body(
                    Map.of("message",
                            "Username must be 3-50 characters using letters, numbers or underscores."));
        }

        if (request.password == null
                || request.password.length() < 8
                || request.password.length() > 72) {
            return ResponseEntity.badRequest().body(
                    Map.of("message",
                            "Password must contain between 8 and 72 characters."));
        }

        try {
            String passwordHash = passwordEncoder.encode(request.password);

            jdbcTemplate.update(
                    "INSERT INTO User_Accounts "
                    + "(username, password_hash, role) "
                    + "VALUES (?, ?, 'CITIZEN')",
                    request.username,
                    passwordHash
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of("message", "Citizen registered successfully.")
            );

        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("message", "Username already exists.")
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody LoginRequest request) {

        if (request.username == null || request.password == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Username and password are required.")
            );
        }

        var users = jdbcTemplate.query(
                "SELECT user_id, username, password_hash, role "
                + "FROM User_Accounts WHERE username = ?",
                (rs, rowNum) -> new UserRecord(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role")
                ),
                request.username
        );

        if (users.isEmpty()
                || !passwordEncoder.matches(
                        request.password, users.get(0).passwordHash)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message", "Invalid username or password.")
            );
        }

        UserRecord user = users.get(0);

        return ResponseEntity.ok(Map.of(
                "message", "Login successful.",
                "userId", String.valueOf(user.userId),
                "username", user.username,
                "role", user.role
        ));
    }

    public static class RegisterRequest {
        public String username;
        public String password;
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }

    private record UserRecord(
            int userId,
            String username,
            String passwordHash,
            String role) {
    }
}
