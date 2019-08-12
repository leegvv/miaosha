package net.arver.miaosha.controller;

import net.arver.miaosha.access.AccessLimit;
import net.arver.miaosha.domain.MiaoshaOrder;
import net.arver.miaosha.domain.MiaoshaUser;
import net.arver.miaosha.rabbitmq.MQSender;
import net.arver.miaosha.rabbitmq.MiaoshaMessage;
import net.arver.miaosha.redis.GoodsKey;
import net.arver.miaosha.redis.MiaoshaKey;
import net.arver.miaosha.redis.OrderKey;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
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
     *
     * @param user    用户
     * @param goodsId 商品id
     * @return 订单信息页面
     */
    @RequestMapping(value = "/{path}/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doMiaosha(final MiaoshaUser user, @RequestParam("goodsId") final long goodsId,
                                     @PathVariable("path") final String path) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //验证path
        boolean check = miaoshaService.checkPath(user, goodsId, path);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //预减库存
        long stock = redisService.decrement(GoodsKey.MIAOSHA_GOODSSTOCK, "" + goodsId); //10
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //入队
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        mqSender.sendMiaoshaMessage(mm);
        return Result.success(0); //排队中

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
     *
     * @param user    登录用户
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

    /**
     * 重置.
     *
     * @return 成功与否
     */
    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset() {
        final List<GoodsVo> goodsList = goodsService.listGoodsVo();
        for (GoodsVo goods : goodsList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.MIAOSHA_GOODSSTOCK, "" + goods.getId(), 10);
            localOverMap.put(goods.getId(), false);
        }
        redisService.delete(OrderKey.MIAOSHAORDER_BY_UID_GID);
        redisService.delete(MiaoshaKey.GOODS_OVER);
        miaoshaService.reset(goodsList);
        return Result.success(true);
    }

    /**
     * 生成秒杀路径参数.
     *
     * @param request    req
     * @param user       登录用户
     * @param goodsId    商品id
     * @param verifyCode 验证码
     * @return 路径参数
     */
    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(final HttpServletRequest request, final MiaoshaUser user,
                                         @RequestParam("goodsId") final long goodsId,
                                         @RequestParam(value = "verifyCode", defaultValue = "0") final int verifyCode) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
        if (!check) {
            return Result.error(CodeMsg.VERIFYCODE_ERROR);
        }
        String path = miaoshaService.createMiaoshaPath(user, goodsId);
        return Result.success(path);
    }

    /**
     * 获取验证码.
     *
     * @param response rep
     * @param user     登录用户
     * @param goodsId  货物di
     * @return 验证码图片
     */
    @RequestMapping(value = "verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> verifyCode(final HttpServletResponse response, final MiaoshaUser user,
                                     @RequestParam("goodsId") final long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            final BufferedImage image = miaoshaService.createVerifyCode(user, goodsId);
            final ServletOutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }
}
