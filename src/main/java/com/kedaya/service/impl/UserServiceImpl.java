package com.kedaya.service.impl;

import com.kedaya.entity.User;
import com.kedaya.mapper.UserMapper;
import com.kedaya.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author：CHENWEI
 * @Package：com.kedaya.service.impl
 * @Project：ssm-mvc-demo
 * @name：UserServiceImpl
 * @Date：2025-06-21 23:31
 * @Filename：UserServiceImpl
 */
// @Service注解标记当前类为Spring的Service组件，Spring会自动创建其实例并管理
@Service
public class UserServiceImpl implements UserService {

    // @Autowired注解自动注入UserMapper实例，MyBatis-Spring会自动创建Mapper接口的实现类并注入
    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserById(Integer id) {
        // 调用Mapper层方法查询用户
        return userMapper.selectUserById(id);
    }

    @Override
    public List<User> getAllUsers() {
        // 调用Mapper层方法查询所有用户
        return userMapper.selectAllUsers();
    }

    @Override
    public boolean addUser(User user) {
        // 调用Mapper层方法插入用户，并判断是否成功（影响行数大于0）
        return userMapper.insertUser(user) > 0;
    }

    @Override
    public boolean updateUser(User user) {
        // 调用Mapper层方法更新用户，并判断是否成功
        return userMapper.updateUser(user) > 0;
    }

    @Override
    public boolean deleteUser(Integer id) {
        // 调用Mapper层方法删除用户，并判断是否成功
        return userMapper.deleteUserById(id) > 0;
    }
}
