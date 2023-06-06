package xyz.Brownie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.Brownie.domain.dto.LVNumDTO;
import xyz.Brownie.domain.entity.User;

import java.util.List;

/**
* @author 76650
* @description 针对表【user】的数据库操作Mapper
* @createDate 2023-04-25 15:09:04
* @Entity xyz.Brownie.aboutuser.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

    LVNumDTO LikeViewNum(@Param("id") Long id);
    User selectUser(@Param("id") Long id);
}




