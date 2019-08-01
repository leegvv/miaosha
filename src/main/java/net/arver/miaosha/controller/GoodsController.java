package net.arver.miaosha.controller;

import net.arver.miaosha.domain.MiaoshaUser;
import net.arver.miaosha.redis.GoodsKey;
import net.arver.miaosha.redis.RedisService;
import net.arver.miaosha.result.Result;
import net.arver.miaosha.service.GoodsService;
import net.arver.miaosha.vo.GoodsDetailVo;
import net.arver.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 登录controller
 */
@Controller
@RequestMapping("goods")
public class GoodsController {

    /**
     * 日志.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsController.class);

    /**
     * 商品服务.
     */
    @Autowired
    GoodsService goodsService;

    /**
     * redis服务.
     */
    @Autowired
    RedisService redisService;

    /**
     * thymeleafViewResolver.
     */
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    /**
     * applicationContext.
     */
    @Autowired
    ApplicationContext applicationContext;

    /**
     * 返回登录页面2371,4130.
     * @param request request
     * @param response response
     * @param model 页面模型
     * @param user 用户信息
     * @return 登录页面
     */
    @RequestMapping(value = "to_list", produces = "text/html")
    @ResponseBody
    public String toList(final HttpServletRequest request, final HttpServletResponse response,
                         final Model model, final MiaoshaUser user) {
        model.addAttribute("user", user);
        String html = redisService.get(GoodsKey.GOODS_LIST, "", String.class);
        if (StringUtils.isNotBlank(html)) {
            return html;
        }
        final List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        final WebContext ctx = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if (StringUtils.isNotBlank(html)) {
            redisService.set(GoodsKey.GOODS_LIST, "", html);
        }
        return html;
    }

    /**
     * 返回商品详情页.
     * @param request request
     * @param response response
     * @param model 页面模型
     * @param user 登录用户
     * @param goodsId 商品id
     * @return 详情页
     */
    @RequestMapping(value = "/to_detail2/{goodsId}", produces = "text/html")
    @ResponseBody
    public String detail2(final HttpServletRequest request, final HttpServletResponse response,
                          final Model model, final MiaoshaUser user, @PathVariable("goodsId") final long goodsId) {
        model.addAttribute("user", user);

        String html = redisService.get(GoodsKey.GOODS_DETAIL, "" + goodsId, String.class);
        if (StringUtils.isNotBlank(html)) {
            return html;
        }

        //手动渲染
        final GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        final long startAt = goods.getStartDate().getTime();
        final long endAt = goods.getEndDate().getTime();
        final long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        long remainSeconds = 0;
        if (now < startAt) {
            miaoshaStatus = 0;
            remainSeconds = TimeUnit.MILLISECONDS.toSeconds(startAt - now);
        } else if (now > endAt) {
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        final WebContext ctx = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
        if (StringUtils.isNotBlank(html)) {
            redisService.set(GoodsKey.GOODS_DETAIL, "" + goodsId, html);
        }

        return html;
    }

    /**
     * 返回商品详情页.
     * @param user 登录用户
     * @param goodsId 商品id
     * @return 详情页
     */
    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(final MiaoshaUser user, @PathVariable("goodsId") final long goodsId) {
        final GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        final long startAt = goods.getStartDate().getTime();
        final long endAt = goods.getEndDate().getTime();
        final long now = System.currentTimeMillis();
        int miaoshaStatus = 0;
        long remainSeconds = 0;
        if (now < startAt) {
            miaoshaStatus = 0;
            remainSeconds = TimeUnit.MILLISECONDS.toSeconds(startAt - now);
        } else if (now > endAt) {
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        final GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setMiaoshaStatus(miaoshaStatus);
        vo.setRemainSeconds(remainSeconds);
        return Result.success(vo);
    }

}
