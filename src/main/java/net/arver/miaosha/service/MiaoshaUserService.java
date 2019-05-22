package net.arver.miaosha.service;

import net.arver.miaosha.dao.MiaoshaUserDao;
import net.arver.miaosha.domain.MiaoshaUser;
import net.arver.miaosha.result.CodeMsg;
import net.arver.miaosha.util.MD5Util;
import net.arver.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务.
 */
@Service
public class MiaoshaUserService {

    @Autowired
    private MiaoshaUserDao miaoshaUserDao;

    /**
     * 根据id获取用户.
     * @param id id
     * @return 用户
     */
    public MiaoshaUser getById(long id) {
        return miaoshaUserDao.getById(id);
    }

    public CodeMsg login(final LoginVo loginVo) {
        if (loginVo == null) {
            return CodeMsg.SERVER_ERROR;
        }

        final String mobile = loginVo.getMobile();
        final String formPass = loginVo.getPassword();
        final MiaoshaUser user = getById(Long.parseLong(mobile));
        if (user == null) {
            return CodeMsg.MOBILE_NOT_EXIST;
        }
        //验证密码
        final String dbPass = user.getPassword();
        final String salt = user.getSalt();
        final String calcPass = MD5Util.formPassToDBPass(formPass, salt);
        if (!calcPass.equals(dbPass)) {
            return CodeMsg.PASSWORD_ERROR;
        }
        return CodeMsg.SUCCESS;
    }
}
