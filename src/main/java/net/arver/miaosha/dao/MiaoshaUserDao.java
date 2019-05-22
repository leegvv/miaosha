package net.arver.miaosha.dao;

import net.arver.miaosha.domain.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * userDao.
 */
@Mapper
public interface MiaoshaUserDao {

    /**
     * 根据id查询.
     * @param id id
     * @return 用户
     */
    @Select("select * from miaosha_user where id = #{id}")
    MiaoshaUser getById(@Param("id") long id);
}
