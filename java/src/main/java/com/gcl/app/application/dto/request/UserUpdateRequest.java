// file: src/main/java/com/gcl/app/application/dto/request/UserUpdateRequest.java
package com.gcl.app.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 用户更新请求
 */
@Data
public class UserUpdateRequest {

    @NotNull(message = "用户ID不能为空")
    private Long id;

    @Pattern(regexp = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$", message = "用户名不能包含特殊字符")
    private String userName;

    @Pattern(regexp = "^\\d{11}$", message = "手机号必须为11位数字")
    private String mobile;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;

    @Size(min = 2, max = 10, message = "昵称长度必须在2-10位之间")
    private String nickName;

    private List<Long> userRoleList;
}
