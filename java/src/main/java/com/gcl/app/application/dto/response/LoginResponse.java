// file: src/main/java/com/gcl/app/application/dto/response/LoginResponse.java
package com.gcl.app.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private Long id;
    private String userName;
    private String token;
}
