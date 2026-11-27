package com.gamelive.controller;

import com.gamelive.common.Result;
import com.gamelive.dto.GiftRankItem;
import com.gamelive.service.GiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 直播间礼物榜。
 */
@RestController
@RequiredArgsConstructor
public class GiftRankController {

    private final GiftService giftService;

    @GetMapping("/rooms/{roomId}/gift-rank")
    public Result<List<GiftRankItem>> rank(@PathVariable Long roomId) {
        return Result.success(giftService.giftRank(roomId));
    }
}
