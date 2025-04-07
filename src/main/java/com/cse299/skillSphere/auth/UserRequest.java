package com.cse299.skillSphere.auth;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRequest {
    private String name;
    private String username;
    private String email;
    private String password;
    private String role;
    private MultipartFile profileImage;
}
