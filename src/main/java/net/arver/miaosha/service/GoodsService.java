package net.arver.miaosha.service;

import net.arver.miaosha.dao.GoodsDao;
import net.arver.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品服务.
 */
@Service
public class GoodsService {

    /**
     * 商品dao.
     */
    @Autowired
    private GoodsDao goodsDao;

    /**
     * 查询秒杀商品.
     * @return 秒杀商品
     */
    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }
}