// file: src/main/java/com/gcl/app/application/dto/response/UserResponse.java
package com.gcl.app.application.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户响应（密码脱敏）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String userName;
    private String mobile;
    private String email;
    private String nickName;
    private String status;
    private List<Long> userRoleList;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser;
    private Long updateUser;

    @JsonIgnore
    private String password;
}
