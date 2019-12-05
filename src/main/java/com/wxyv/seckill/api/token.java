package com.wxyv.seckill.api;

import com.wxyv.seckill.domain.Seckill;
import com.wxyv.seckill.util.TokenTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping("/api/token")
public class token {

    /**
     * 注入响应式的ReactiveRedisTemplate
     */
    @Autowired
    private ReactiveRedisTemplate reactiveRedisTemplate;
    /**
     * 通过渠道和商品，获取秒杀令牌
     * @param seckill
     * @return 秒杀令牌
     */
    @PostMapping
    public Mono create(@RequestBody Seckill seckill){
        //TODO 验证是否在秒杀时间段，是否可以还有库存，等规则
        //创建秒杀的 token
        String token = TokenTools.createToken();
        //将token、当前状态、渠道、商品 等相关信息存放到redis 中，并设置过期时间
        reactiveRedisTemplate.opsForValue().set(token,seckill, 30000L);
        return Mono.just(token);
    }

    /**
     * 检查令牌的有效性
     *  可以增加 渠道 、 商品等验证条件
     * @param token
     * @return
     */
    @GetMapping("/{token}/check")
    public Mono check (@PathVariable("token") String token){
        Mono<Seckill> seckillMono =  reactiveRedisTemplate.opsForValue().get(token);
        return Mono.just(Optional.of(seckillMono).isPresent());
    }

    /**
     * 获取令牌信息： 渠道、产品、过期时间
     * @param token
     * @return
     */
    @GetMapping("/{token}")
    public Mono query (@PathVariable("token") String token){
        Mono<Seckill> seckillMono =  reactiveRedisTemplate.opsForValue().get(token);
        return Mono.just(seckillMono);
    }

    /**
     * 下单成功，更新token状态，并归档
     * @param token
     * @return
     */
    @PutMapping("/{token}")
    public Mono modifiy (@PathVariable("token") String token){
        //TODO 更改 状态

        //TODO 判断库存 补库存

        return null;
    }
}
