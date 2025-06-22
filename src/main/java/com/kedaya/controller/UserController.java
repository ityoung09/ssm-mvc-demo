package com.kedaya.controller;

import com.kedaya.entity.User;
import com.kedaya.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author：CHENWEI
 * @Package：com.kedaya.controller
 * @Project：ssm-mvc-demo
 * @name：DemoController
 * @Date：2025-06-21 22:49
 * @Filename：DemoController
 */
// @RestController 是 @Controller 和 @ResponseBody 的组合注解
// 表示该类的所有方法返回值都会直接作为HTTP响应体返回，通常是JSON或XML数据
@RestController
// @RequestMapping("/user") 定义了该控制器处理的所有请求的根路径
@RequestMapping("/user")
public class UserController {

    // @Autowired注解自动注入UserService实例
    @Autowired
    private UserService userService;

    // 处理GET请求，路径为/user/list
    // 返回所有用户列表的JSON数据
    @GetMapping("/list")
    public List<User> listUsers() {
        // 调用Service层方法获取所有用户列表
        return userService.getAllUsers();
    }

    // 处理GET请求，路径为/user/{id}，{id}是路径变量
    // @PathVariable("id") 将路径中的id值绑定到方法的id参数上
    // 返回单个用户信息的JSON数据
    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer id) {
        // 调用Service层方法根据ID查询用户
        return userService.getUserById(id);
    }

    // 处理POST请求，路径为/user/add
    // @RequestBody注解表示将HTTP请求体中的JSON或XML数据绑定到User对象上
    // 返回操作结果的JSON消息
    @PostMapping("/add")
    public String addUser(@RequestBody User user) {
        // 调用Service层方法添加用户
        if (userService.addUser(user)) {
            // 返回JSON格式的成功消息
            return "{\"msg\": \"User added successfully!\"}";
        }
        // 返回JSON格式的失败消息
        return "{\"msg\": \"Failed to add user!\"}";
    }

    // 处理PUT请求，路径为/user/update
    // 通常用于更新资源
    // 返回操作结果的JSON消息
    @PutMapping("/update")
    public String updateUser(@RequestBody User user) {
        // 调用Service层方法更新用户
        if (userService.updateUser(user)) {
            return "{\"msg\": \"User updated successfully!\"}";
        }
        return "{\"msg\": \"Failed to update user!\"}";
    }

    // 处理DELETE请求，路径为/user/delete/{id}
    // 通常用于删除资源
    // 返回操作结果的JSON消息
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id) {
        // 调用Service层方法删除用户
        if (userService.deleteUser(id)) {
            return "{\"msg\": \"User deleted successfully!\"}";
        }
        return "{\"msg\": \"Failed to delete user!\"}";
    }
}
