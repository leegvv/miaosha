package net.arver.miaosha.dao;

import net.arver.miaosha.domain.MiaoshaOrder;
import net.arver.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

/**
 * 订单dao.
 */
@Mapper
public interface OrderDao {

    /**
     * 根据用户id和商品id查询订单信息.
     * @param userId 用户id
     * @param goodsId 商品id
     * @return 秒杀订单
     */
    @Select("select * from miaosha_order where user_id = #{userId} and goods_id = #{goodsId}")
    MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    /**
     * 插入订单信息.
     * @param orderInfo 订单信息
     * @return 订单id
     */
    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel,"
            + " status, create_date) values(#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice},"
            + " #{orderChannel}, #{status}, #{createDate})")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false,
            statement = "select last_insert_id()")
    long insert(OrderInfo orderInfo);

    /**
     * 查询秒杀订单.
     * @param miaoshaOrder 秒杀订单
     * @return 秒杀订单id
     */
    @Insert("insert into miaosha_order(user_id, goods_id, order_id) values(#{userId}, #{goodsId}, #{orderId})")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false,
            statement = "select last_insert_id()")
    long insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

    @Select("select * from order_info where id = #{orderId}")
    OrderInfo getOrderById(long orderId);
}
