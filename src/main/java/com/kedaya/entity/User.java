package com.kedaya.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author：CHENWEI
 * @Package：com.kedaya.entity
 * @Project：ssm-mvc-demo
 * @name：User
 * @Date：2025-06-21 23:22
 * @Filename：User
 */
@Data
public class User implements Serializable {
    private Integer id;       // 用户ID
    private String username;  // 用户名
    private String password;  // 密码
    private String email;     // 邮箱
}
