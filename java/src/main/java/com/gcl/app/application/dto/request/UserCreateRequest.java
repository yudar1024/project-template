// file: src/main/java/com/gcl/app/application/dto/request/UserCreateRequest.java
package com.gcl.app.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 用户创建请求
 */
@Data
public class UserCreateRequest {

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$", message = "用户名不能包含特殊字符")
    private String userName;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^\\d{11}$", message = "手机号必须为11位数字")
    private String mobile;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;

    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 10, message = "昵称长度必须在2-10位之间")
    private String nickName;

    @NotEmpty(message = "用户角色列表不能为空")
    private List<Long> userRoleList;
}
