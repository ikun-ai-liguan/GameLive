package com.gamelive.controller;

import com.gamelive.common.Result;
import com.gamelive.dto.GiftSendRequest;
import com.gamelive.entity.Gift;
import com.gamelive.service.GiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 礼物打赏。
 */
@RestController
@RequestMapping("/gifts")
@RequiredArgsConstructor
public class GiftController {

    private final GiftService giftService;

    @GetMapping
    public Result<List<Gift>> list() {
        return Result.success(giftService.listGifts());
    }

    @PostMapping("/send")
    public Result<Void> send(@RequestHeader(value = "X-User-Id", required = false) Long fromUserId,
                             @Valid @RequestBody GiftSendRequest request) {
        giftService.sendGift(request, fromUserId);
        return Result.success();
    }
}
