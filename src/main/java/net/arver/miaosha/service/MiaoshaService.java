package net.arver.miaosha.service;

import net.arver.miaosha.domain.MiaoshaOrder;
import net.arver.miaosha.domain.MiaoshaUser;
import net.arver.miaosha.domain.OrderInfo;
import net.arver.miaosha.redis.MiaoshaKey;
import net.arver.miaosha.redis.RedisService;
import net.arver.miaosha.util.MD5Util;
import net.arver.miaosha.util.UUIDUtil;
import net.arver.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

/**
 * 秒杀服务.
 */
@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    /**
     * 秒杀.
     *
     * @param user  用户信息
     * @param goods 商品vo
     * @return 订单信息
     */
    public OrderInfo miaosha(final MiaoshaUser user, final GoodsVo goods) {
        //减库存、下订单、写入秒杀订单
        final boolean success = goodsService.reduceStock(goods);
        if (success) {
            return orderService.createOrder(user, goods);
        }
        setGoodsOver(goods.getId());
        return null;
    }

    /**
     * 获取秒杀结果.
     *
     * @param userId  用户id
     * @param goodsId 货物id
     * @return 结果
     */
    public long getMiaoshaResult(final Long userId, final long goodsId) {
        final MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(userId, goodsId);
        if (order != null) {
            return order.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    /**
     * 重置.
     *
     * @param goodsList 商品集合
     */
    public void reset(final List<GoodsVo> goodsList) {
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }

    /**
     * 校验路径.
     *
     * @param user    登录用户
     * @param goodsId 货物id
     * @param path    路径
     * @return 检验结果
     */
    public boolean checkPath(final MiaoshaUser user, final long goodsId, final String path) {
        if (user == null || path == null) {
            return false;
        }
        String pathOld = redisService.get(MiaoshaKey.MIAOSHA_PATH, "" + user.getId() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }

    /**
     * 生成秒杀路径.
     *
     * @param user    登录用户
     * @param goodsId 商品id
     * @return 路径参数
     */
    public String createMiaoshaPath(final MiaoshaUser user, final long goodsId) {
        if (user == null || goodsId <= 0) {
            return null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisService.set(MiaoshaKey.MIAOSHA_PATH, "" + user.getId() + "_" + goodsId, str);
        return str;
    }


    /**
     * 创建二维码.
     *
     * @param user    登录用户
     * @param goodsId 货物id
     * @return 二维码
     */
    public BufferedImage createVerifyCode(final MiaoshaUser user, final long goodsId) {
        if (user == null || goodsId <= 0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.MIAOSHA_VERIFYCODE, user.getId() + "," + goodsId, rnd);
        //输出图片
        return image;
    }

    /**
     * 验证验证码.
     *
     * @param user       登录用户
     * @param goodsId    商品id
     * @param verifyCode 验证码
     * @return 是否正确
     */
    public boolean checkVerifyCode(final MiaoshaUser user, final long goodsId, final int verifyCode) {
        if (user == null || goodsId <= 0) {
            return false;
        }
        Integer codeOld = redisService.get(MiaoshaKey.MIAOSHA_VERIFYCODE, user.getId() + "," + goodsId, Integer.class);
        if (codeOld == null || codeOld - verifyCode != 0) {
            return false;
        }
        redisService.delete(MiaoshaKey.MIAOSHA_VERIFYCODE, user.getId() + "," + goodsId);
        return true;
    }

    /**
     * 查询秒杀是否结束.
     *
     * @param goodsId 货物id
     * @return 是否结束
     */
    private boolean getGoodsOver(final long goodsId) {
        return redisService.exist(MiaoshaKey.GOODS_OVER, "" + goodsId);
    }

    /**
     * 设置秒杀结束.
     *
     * @param goodsId 货物id
     */
    private void setGoodsOver(final long goodsId) {
        redisService.set(MiaoshaKey.GOODS_OVER, "" + goodsId, true);
    }

    /**
     * 计算结果.
     *
     * @param exp 表达式
     * @return 结果
     */
    private static int calc(final String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(exp);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[]{'+', '-', '*'};

    /**
     * 生成二维码.
     *
     * @param rdm 随机数生成器
     * @return 表达式；
     */
    private String generateVerifyCode(final Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = "" + num1 + op1 + num2 + op2 + num3;
        return exp;
    }
}
