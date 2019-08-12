package net.arver.miaosha.dao;

import net.arver.miaosha.domain.MiaoshaGoods;
import net.arver.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商品dao.
 */
@Mapper
public interface GoodsDao {

    /**
     * 查询秒杀商品vo.
     * @return 秒杀商品vo
     */
    @Select("select g.*, mg.stock_count, mg.start_date, mg.end_date, mg.miaosha_price "
            + "from miaosha_goods mg left join goods g on mg.goods_id = g.id")
    List<GoodsVo> listGoodsVo();

    /**
     * 根据商品id查询秒杀商品信息.
     * @param goodsId 商品id
     * @return 秒杀商品信息
     */
    @Select("select g.*, mg.stock_count, mg.start_date, mg.end_date, mg.miaosha_price from miaosha_goods mg "
            + "left join goods g on mg.goods_id = g.id where g.id = #{goodsId}")
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    /**
     * 减少库存.
     * @param goods 商品
     * @return 更新数量
     */
    @Update("update miaosha_goods set stock_count = stock_count -1 where goods_id = #{goodsId} and stock_count > 0")
    int reduceStock(MiaoshaGoods goods);

    /**
     * 重置库存.
     * @param goods 秒杀商品
     */
    @Update("update miaosha_goods set stock_count = #{stockCount} where goods_id = #{goodsId}")
    void resetStock(MiaoshaGoods goods);
}
