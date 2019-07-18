package net.arver.miaosha.service;

import net.arver.miaosha.dao.MiaoshaUserDao;
import net.arver.miaosha.domain.MiaoshaUser;
import net.arver.miaosha.exception.GlobalException;
import net.arver.miaosha.redis.MiaoshaUserKey;
import net.arver.miaosha.redis.RedisService;
import net.arver.miaosha.result.CodeMsg;
import net.arver.miaosha.util.MD5Util;
import net.arver.miaosha.util.UUIDUtil;
import net.arver.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户服务.
 */
@Service
public class MiaoshaUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    private MiaoshaUserDao miaoshaUserDao;

    @Autowired
    private RedisService redisService;

    /**
     * 根据id获取用户.
     * @param id id
     * @return 用户
     */
    public MiaoshaUser getById(final long id) {
        return miaoshaUserDao.getById(id);
    }

    /**
     * 登录.
     * @param response response
     * @param loginVo 用户对象
     * @return 登录结果
     */
    public boolean login(final HttpServletResponse response, final LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

        final String mobile = loginVo.getMobile();
        final String formPass = loginVo.getPassword();
        final MiaoshaUser user = getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        final String dbPass = user.getPassword();
        final String salt = user.getSalt();
        final String calcPass = MD5Util.formPassToDBPass(formPass, salt);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        final String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return true;
    }

    /**
     * 根据token获取用户.
     * @param response response
     * @param token token
     * @return 用户
     */
    public MiaoshaUser getByToken(final HttpServletResponse response, final String token) {
        if (StringUtils.isBlank(token)) {
            return  null;
        }
        final MiaoshaUser user = redisService.get(MiaoshaUserKey.TOKEN, token, MiaoshaUser.class);
        if (user != null) {
            addCookie(response, token, user);
        }
        return user;
    }

    /**
     * 添加cookie并更新有效期.
     * @param response response
     * @param token token
     * @param user 用户信息
     */
    private void addCookie(final HttpServletResponse response, final String token,  final MiaoshaUser user) {
        redisService.set(MiaoshaUserKey.TOKEN, token, user);
        final Cookie cookie = new Cookie(MiaoshaUserService.COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.TOKEN.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
