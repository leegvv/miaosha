package net.arver.miaosha.service;

import net.arver.miaosha.dao.GoodsDao;
import net.arver.miaosha.domain.MiaoshaGoods;
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

    /**
     * 根据商品id查询秒杀商品信息.
     * @param goodsId 商品id
     * @return 秒杀商品信息
     */
    public GoodsVo getGoodsVoByGoodsId(final long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    /**
     * 减少库存.
     * @param goods 商品
     * @return 更新数量
     */
    public boolean reduceStock(final GoodsVo goods) {
        final MiaoshaGoods g = new MiaoshaGoods();
        g.setGoodsId(goods.getId());
        final int i = goodsDao.reduceStock(g);
        return i > 0;
    }
}
