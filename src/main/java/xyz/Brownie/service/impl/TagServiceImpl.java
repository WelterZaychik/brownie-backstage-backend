package xyz.Brownie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import xyz.Brownie.domain.entity.Tag;
import xyz.Brownie.mapper.TagMapper;
import xyz.Brownie.service.TagService;

/**
* @author Welt
* @description 针对表【tag】的数据库操作Service实现
* @createDate 2023-05-13 14:52:17
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




