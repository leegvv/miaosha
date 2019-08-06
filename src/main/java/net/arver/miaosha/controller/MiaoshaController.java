package net.arver.miaosha.controller;

import net.arver.miaosha.domain.MiaoshaOrder;
import net.arver.miaosha.domain.MiaoshaUser;
import net.arver.miaosha.rabbitmq.MQSender;
import net.arver.miaosha.rabbitmq.MiaoshaMessage;
import net.arver.miaosha.redis.GoodsKey;
import net.arver.miaosha.redis.RedisService;
import net.arver.miaosha.result.CodeMsg;
import net.arver.miaosha.result.Result;
import net.arver.miaosha.service.GoodsService;
import net.arver.miaosha.service.MiaoshaService;
import net.arver.miaosha.service.OrderService;
import net.arver.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 秒杀controller.
 */
@RequestMapping("miaosha")
@Controller
public class MiaoshaController implements InitializingBean {

    /**
     * 商品服务.
     */
    @Autowired
    private GoodsService goodsService;

    /**
     * 订单服务.
     */
    @Autowired
    OrderService orderService;

    /**
     * 秒杀服务.
     */
    @Autowired
    MiaoshaService miaoshaService;

    /**
     * redis服务.
     */
    @Autowired
    RedisService redisService;
    /**
     * 消息生产者.
     */
    @Autowired
    MQSender mqSender;

    private Map<Long, Boolean> localOverMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        final List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if (goodsVoList == null) {
            return;
        }
        for (GoodsVo goodsVo : goodsVoList) {
            redisService.set(GoodsKey.MIAOSHA_GOODSSTOCK, "" + goodsVo.getId(), goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(), false);
        }
    }

    /**
     * 秒杀 2087 2948.
     * @param user 用户
     * @param goodsId 商品id
     * @return 订单信息页面
     */
    @RequestMapping(value = "do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doMiaosha(final MiaoshaUser user, @RequestParam("goodsId") final long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        final Boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //预减库存
        final Long stock = redisService.decrement(GoodsKey.MIAOSHA_GOODSSTOCK, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        final MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        final MiaoshaMessage msg = new MiaoshaMessage();
        msg.setUser(user);
        msg.setGoodsId(goodsId);
        mqSender.sendMiaoshaMessage(msg);
        return Result.success(0);


        /*final GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        final Integer stockCount = goodsVo.getStockCount();
        if (stockCount <= 0) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        final MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        // 减库存 下订单 写入秒杀订单
        final OrderInfo orderInfo = miaoshaService.miaosha(user, goodsVo);
        return Result.success(orderInfo);*/
    }

    /**
     * 获取秒杀结果.
     * @param user 登录用户
     * @param goodsId 货物id
     * @return orderId:成功， -1：秒杀失败， 0：排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(final MiaoshaUser user, @RequestParam("goodsId") final long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        final long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }
}
