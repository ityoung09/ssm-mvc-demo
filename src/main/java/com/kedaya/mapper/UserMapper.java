package com.kedaya.mapper;

import com.kedaya.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 用户数据访问接口，MyBatis会根据此接口及其对应的XML文件生成实现类
public interface UserMapper {
    /**
     * 根据用户ID查询用户信息
     *
     * @param id 用户ID
     * @return 对应的User对象，如果不存在则返回null
     */
    User selectUserById(@Param("id") Integer id);

    /**
     * 查询所有用户信息
     *
     * @return User对象列表
     */
    List<User> selectAllUsers();

    /**
     * 插入新用户
     *
     * @param user 待插入的User对象
     * @return 影响的行数，通常为1表示成功
     */
    int insertUser(User user);

    /**
     * 更新用户信息
     *
     * @param user 包含更新信息的User对象（id必须存在）
     * @return 影响的行数，通常为1表示成功
     */
    int updateUser(User user);

    /**
     * 根据用户ID删除用户
     *
     * @param id 待删除的用户ID
     * @return 影响的行数，通常为1表示成功
     */
    int deleteUserById(@Param("id") Integer id);
}
