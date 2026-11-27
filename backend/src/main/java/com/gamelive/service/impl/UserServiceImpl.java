package com.gamelive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gamelive.common.BusinessException;
import com.gamelive.config.JwtUtil;
import com.gamelive.dto.LoginRequest;
import com.gamelive.dto.ProfileUpdateRequest;
import com.gamelive.dto.RegisterRequest;
import com.gamelive.entity.Follow;
import com.gamelive.entity.User;
import com.gamelive.mapper.FollowMapper;
import com.gamelive.mapper.UserMapper;
import com.gamelive.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final FollowMapper followMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public void register(RegisterRequest request) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (count != null && count > 0) {
            throw new BusinessException("账号已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() == null ? request.getUsername() : request.getNickname());
        user.setRole("USER");
        user.setStatus("NORMAL");
        user.setFansCount(0);
        user.setFollowCount(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
    }

    @Override
    public String login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        if ("BANNED".equals(user.getStatus())) {
            throw new BusinessException("账号已被封禁，请联系管理员");
        }
        return jwtUtil.generateToken(user.getId());
    }

    @Override
    public User getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public void updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getSignature() != null) {
            user.setSignature(request.getSignature());
        }
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void follow(Long userId, Long anchorId) {
        Follow exist = followMapper.selectOne(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getUserId, userId)
                .eq(Follow::getAnchorId, anchorId));
        if (exist != null) {
            return;
        }
        Follow follow = new Follow();
        follow.setUserId(userId);
        follow.setAnchorId(anchorId);
        follow.setCreateTime(LocalDateTime.now());
        followMapper.insert(follow);
    }

    @Override
    public void unfollow(Long userId, Long anchorId) {
        followMapper.delete(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getUserId, userId)
                .eq(Follow::getAnchorId, anchorId));
    }

    @Override
    public List<User> listFans(Long anchorId) {
        List<Follow> follows = followMapper.selectList(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getAnchorId, anchorId));
        List<Long> userIds = follows.stream().map(Follow::getUserId).collect(Collectors.toList());
        if (userIds.isEmpty()) {
            return List.of();
        }
        List<User> fans = userMapper.selectBatchIds(userIds);
        fans.forEach(u -> u.setPassword(null));
        return fans;
    }

    @Override
    public List<User> listFollows(Long userId) {
        List<Follow> follows = followMapper.selectList(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getUserId, userId)
                .orderByDesc(Follow::getCreateTime));
        List<Long> anchorIds = follows.stream().map(Follow::getAnchorId).collect(Collectors.toList());
        if (anchorIds.isEmpty()) {
            return List.of();
        }
        List<User> anchors = userMapper.selectBatchIds(anchorIds);
        anchors.forEach(u -> u.setPassword(null));
        return anchors;
    }
}
