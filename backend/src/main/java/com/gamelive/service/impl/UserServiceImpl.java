package com.gamelive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gamelive.common.BusinessException;
import com.gamelive.dto.LoginRequest;
import com.gamelive.dto.ProfileUpdateRequest;
import com.gamelive.dto.RegisterRequest;
import com.gamelive.entity.Follow;
import com.gamelive.entity.User;
import com.gamelive.mapper.FollowMapper;
import com.gamelive.mapper.UserMapper;
import com.gamelive.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final FollowMapper followMapper;

    @Override
    public void register(RegisterRequest request) {
        // TODO 密码加密存储（BCrypt），此处骨架仅做重名校验
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (count != null && count > 0) {
            throw new BusinessException("账号已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
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
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        if ("BANNED".equals(user.getStatus())) {
            throw new BusinessException("账号已被封禁，请联系管理员");
        }
        // TODO 生成真实 JWT；骨架阶段返回用户 ID 占位串
        return "token-" + user.getId();
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
        // TODO 同步双方粉丝数 / 关注数冗余字段
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
}
