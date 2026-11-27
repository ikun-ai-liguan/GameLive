package com.gamelive.service;

import com.gamelive.dto.LoginRequest;
import com.gamelive.dto.ProfileUpdateRequest;
import com.gamelive.dto.RegisterRequest;
import com.gamelive.entity.User;

import java.util.List;

public interface UserService {

    /** 注册（密码 BCrypt 加密存储） */
    void register(RegisterRequest request);

    /** 登录，返回 JWT */
    String login(LoginRequest request);

    /** 查询当前用户信息 */
    User getCurrentUser(Long userId);

    /** 修改个人信息 */
    void updateProfile(Long userId, ProfileUpdateRequest request);

    /** 关注主播 */
    void follow(Long userId, Long anchorId);

    /** 取消关注 */
    void unfollow(Long userId, Long anchorId);

    /** 主播的粉丝列表 */
    List<User> listFans(Long anchorId);

    /** 当前用户关注的主播列表 */
    List<User> listFollows(Long userId);
}
