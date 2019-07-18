package net.arver.miaosha.dao;

import net.arver.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

}
