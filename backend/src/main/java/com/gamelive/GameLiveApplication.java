package com.gamelive;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * GameLive 游戏直播网站后端启动类。
 */
@SpringBootApplication
@MapperScan("com.gamelive.mapper")
public class GameLiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameLiveApplication.class, args);
    }
}
