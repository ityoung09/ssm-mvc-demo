package com.kedaya.service;

import com.kedaya.entity.User;

import java.util.List;

// UserService接口，定义用户业务逻辑操作
public interface UserService {
    /**
     * 根据用户ID获取用户信息
     *
     * @param id 用户ID
     * @return User对象
     */
    User getUserById(Integer id);

    /**
     * 获取所有用户列表
     *
     * @return User对象列表
     */
    List<User> getAllUsers();

    /**
     * 添加新用户
     *
     * @param user 待添加的用户对象
     * @return true表示添加成功，false表示失败
     */
    boolean addUser(User user);

    /**
     * 更新用户信息
     *
     * @param user 待更新的用户对象
     * @return true表示更新成功，false表示失败
     */
    boolean updateUser(User user);

    /**
     * 删除用户
     *
     * @param id 待删除的用户ID
     * @return true表示删除成功，false表示失败
     */
    boolean deleteUser(Integer id);
}
