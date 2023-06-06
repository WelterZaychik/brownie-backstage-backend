package xyz.Brownie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.Brownie.domain.entity.Tag;

/**
* @author Welt
* @description 针对表【tag】的数据库操作Mapper
* @createDate 2023-05-13 14:52:17
* @Entity xyz.Brownie.bean.entity.Tag
*/
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

}




