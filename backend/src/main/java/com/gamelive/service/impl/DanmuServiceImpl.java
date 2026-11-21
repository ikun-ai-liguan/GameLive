package com.gamelive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gamelive.entity.Danmu;
import com.gamelive.mapper.DanmuMapper;
import com.gamelive.service.DanmuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DanmuServiceImpl implements DanmuService {

    private final DanmuMapper danmuMapper;

    @Override
    public void save(Danmu danmu) {
        danmu.setCreateTime(LocalDateTime.now());
        danmuMapper.insert(danmu);
    }

    @Override
    public List<Danmu> listByRoom(Long roomId) {
        return danmuMapper.selectList(new LambdaQueryWrapper<Danmu>()
                .eq(Danmu::getRoomId, roomId)
                .orderByDesc(Danmu::getCreateTime)
                .last("limit 50"));
    }
}
