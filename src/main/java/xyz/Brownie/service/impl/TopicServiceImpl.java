package xyz.Brownie.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import org.springframework.web.bind.annotation.PathVariable;
import xyz.Brownie.domain.dto.TopicDto;
import xyz.Brownie.domain.entity.Topic;
import xyz.Brownie.domain.entity.TopicTag;
import xyz.Brownie.domain.entity.User;
import xyz.Brownie.mapper.TopicMapper;
import xyz.Brownie.mapper.TopicTagMapper;
import xyz.Brownie.service.TopicService;
import xyz.Brownie.service.TopicTagService;
import xyz.Brownie.service.UserService;
import xyz.Brownie.util.EmptyContentException;
import xyz.Brownie.util.ResponseCode;
import xyz.Brownie.util.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author 76650
* @description 针对表【topic】的数据库操作Service实现
* @createDate 2023-04-25 15:50:42
*/
@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic>
    implements TopicService{
    @Autowired
    private TopicMapper topicMapper;
    @Autowired
    private TopicTagService tagService;
    @Autowired
    private UserService userService;
    @Autowired
    private TopicTagMapper topicTagMapper;

    private Map resNo;
    private Map resYes;

    public List<Topic> searchTopic(TopicDto topicDto) {
        return topicMapper.searchTopic(topicDto);
    }
    //新增帖子
    @Override
    public Result publish(Topic topic){
        resNo = new HashMap();
        resYes = new HashMap();
        String msg = "";
        try {
            if (!StringUtils.hasText(topic.getTitle())){
                msg = "标题为空";
                resNo.put("msg",msg);
                throw new EmptyContentException(msg);
            }else if(!StringUtils.hasText(topic.getContent())){
                msg = "内容为空";
                resNo.put("msg",msg);
                throw new EmptyContentException(msg);
            }else if(!StringUtils.hasText(topic.getSynopsis())){
                msg = "简介为空";
                resNo.put("msg",msg);
                throw new EmptyContentException(msg);
            } else if(topic.getTagId() == null){
                msg = "未选择标签";
                resNo.put("msg",msg);
                throw new EmptyContentException(msg);
            }else if (save(topic)){
                tagService.save(new TopicTag(topic.getId(),topic.getTagId(),topic.getIsDelete()));
                Long createUserId = topic.getCreateUserId();
                User user = userService.getById(createUserId);
                topic.setNickName(user.getName());
                topic.setAvatar(user.getAvatar());
                // resYes.put("topic",topic);
            }
        } catch (EmptyContentException e) {
            System.out.println(e.getMessage());
            return Result.fail(ResponseCode.Code402,resNo);
        }

        // return Result.suc(ResponseCode.Code200,resYes);
        return Result.suc(ResponseCode.Code200);
    }

    //删除帖子
    @Override
    public Result delete(Long ids) {
        resNo = new HashMap();
        resYes = new HashMap();
        try {
            if (removeById(ids)) {
                //通过帖子id删除对应的帖子标签表
                LambdaQueryWrapper<TopicTag> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(TopicTag::getTopicId,ids);
                tagService.remove(wrapper);
                resYes.put("msg","删除成功!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resNo.put("msg","已经删除啦,请刷新页面");
            return Result.fail(ResponseCode.Code402,resNo);
        }
        return Result.suc(ResponseCode.Code200,resYes);
    }

    //修改帖子
    @Override
    public Result revise(Topic topic){
        resNo = new HashMap();
        resYes = new HashMap();
        String msg = "";
        try {
            if (!StringUtils.hasText(topic.getTitle())){
                msg = "标题为空";
                resNo.put("msg",msg);
                throw new EmptyContentException(msg);
            }else if(!StringUtils.hasText(topic.getContent())){
                msg = "内容为空";
                resNo.put("msg",msg);
                throw new EmptyContentException(msg);
            }else if(!StringUtils.hasText(topic.getSynopsis())){
                msg = "简介为空";
                resNo.put("msg",msg);
                throw new EmptyContentException(msg);
            } else if(topic.getTagId() == null){
                msg = "未选择标签";
                resNo.put("msg",msg);
                throw new EmptyContentException(msg);
            }else if(updateById(topic)){
                UpdateWrapper<TopicTag> wrapper = new UpdateWrapper<>();
                wrapper.eq("topic_id",topic.getId()).set("tag_id",topic.getTagId());
                tagService.update(wrapper);
                resYes.put("topic",topic);
            }
        } catch (EmptyContentException e) {
            System.out.println(e.getMessage());
            return Result.fail(ResponseCode.Code402,resNo);

        }
        return Result.suc(ResponseCode.Code200,resYes);
    }

    //模糊查询
    @Override
    public Result SelectSome(TopicDto topicDto) {
        resNo = new HashMap();
        resYes = new HashMap();
        LambdaQueryWrapper<Topic> topicWrapper = new LambdaQueryWrapper<>();
        List<Long> topicIds = new ArrayList<>();
        String msg = "某一处出现错误请检查!";
        try {
            if (!(topicDto.getTagId() == null)) {
                QueryWrapper<TopicTag> wrapper = new QueryWrapper<>();
                wrapper.eq("tag_id", topicDto.getTagId());
                List<TopicTag> topicTags = topicTagMapper.selectList(wrapper);
                for (int i = 0; i < topicTags.size(); i++) {
                    topicIds.add(topicTags.get(i).getTopicId());
                }
                topicWrapper.in(Topic::getId, topicIds);
            }
            topicWrapper.gt(StringUtils.hasText(topicDto.getStartTime()),Topic::getCreateTime, topicDto.getStartTime())
                    .le(StringUtils.hasText(topicDto.getEndTime()),Topic::getCreateTime,topicDto.getEndTime())
                    .eq(StringUtils.hasText(topicDto.getIsVideo()), Topic::getIsVideo, topicDto.getIsVideo());

            if (StringUtils.hasText(topicDto.getKeyWord())) {
                topicWrapper.and(qw -> qw.like(Topic::getTitle, topicDto.getKeyWord())
                        .or()
                        .like(Topic::getSynopsis, topicDto.getKeyWord()));
            }

            List<Topic> topicList = topicMapper.selectList(topicWrapper);
            for (int i = 0; i < topicList.size(); i++) {
                User user = userService.getById(topicList.get(i).getCreateUserId());
                topicList.get(i).setNickName(user.getName());
                topicList.get(i).setAvatar(user.getAvatar());
            }
            resYes.put("fuzzyList",topicList);
        }catch (Exception e) {
            e.printStackTrace();
            resNo.put("msg",msg);
            return Result.fail(ResponseCode.Code402,resNo);
        }
        return Result.suc(ResponseCode.Code200,resYes);
    }

    //帖子的点赞
    @Override
    public Result addlikes(Long id){
        resNo = new HashMap();
        resYes = new HashMap();
        if(topicMapper.updatelikes(id) == 0){
            resNo.put("data","未能点赞");
            Result.fail(ResponseCode.Code402,resNo);
        }
        resYes.put("data","成功");
        return Result.suc(ResponseCode.Code200,resYes);
    }
    //帖子的观看数
    @Override
    public Result addviews(Long id) {
        resNo = new HashMap();
        resYes = new HashMap();
        if (topicMapper.updateviews(id) != 0) {
            Topic topic = topicMapper.selectById(id);
            Long numberOfViews = null;
            try {
                numberOfViews = topic.getNumberOfViews();
            } catch (Exception e) {
                String msg = "估计是is_delete为0,反正就是出错啦!";
                System.out.println(msg);
                resNo.put("msg",msg);
                return Result.fail(ResponseCode.Code402,resNo);
            }
            resYes.put("data",numberOfViews);
            return Result.suc(ResponseCode.Code200,resYes);
        }
        return Result.fail(ResponseCode.Code402,resNo);
    }
    //分页
    @Override
    public Result pagination(Integer current) {
        resNo = new HashMap();
        resYes = new HashMap();
        Page<Topic> page = new Page<>(current,4);
        try {
            List<Topic> list = topicMapper.pagination(page);
            if (list.size() != 0){
                for (int i = 0; i < list.size(); i++) {
                    Topic topic = list.get(i);
                    Long id = topic.getCreateUserId();
                    User user = userService.getById(id);
                    topic.setNickName(user.getName());
                    topic.setAvatar(user.getAvatar());
                }
                resYes.put("data",list);
                resYes.put("total",topicMapper.selectCount(null));
                return Result.suc(ResponseCode.Code200,resYes);
            }
        } catch (Exception e) {
            resNo.put("msg","出现错误!");
            return Result.fail(ResponseCode.Code402,resNo);
        }
        resNo.put("msg","查不出来");
        return Result.fail(ResponseCode.Code402,resNo);

    }
    //帖子详情
    @Override
    public Result detail(Long id) {
        Topic topic = null;
        try {
            resNo = new HashMap();
            resYes = new HashMap();
            topic = topicMapper.selectById(id);
            Long createUserId = topic.getCreateUserId();
            User user = userService.getById(createUserId);
            topic.setNickName(user.getName());
            topic.setAvatar(user.getAvatar());
            addviews(id);
            resYes.put("topic",topic);
        } catch (Exception e) {
            resNo.put("msg","出现错误!");
            return Result.fail(ResponseCode.Code402,resNo);
        }
        return Result.suc(ResponseCode.Code200,resYes);
    }
    //官方发的帖子
    @Override
    public Result HostOfTopic(int curPage,int pageSize) {
        resNo = new HashMap();
        resYes = new HashMap();
        QueryWrapper<Topic> wrapper = new QueryWrapper<>();
        wrapper.eq("create_user_id","0")
                .orderByDesc("create_time");
        Page<Topic> page = new Page<>(curPage,pageSize);
        try {
            Page<Topic> topicPage = topicMapper.selectPage(page, wrapper);
            List<Topic> list = topicPage.getRecords();
            long total = topicPage.getTotal();
            resYes.put("hostList",list);
            resYes.put("hostTotal",(int)total);
            if (list.size() == 0 || list == null){
                resNo.put("msg","没有查到或者发生错误!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(ResponseCode.Code402,resNo);
        }
        return Result.suc(ResponseCode.Code200,resYes);

    }
    //双重排序
    @Override
    public Result SelectChoose(Integer current) {
        resNo = new HashMap();
        resYes = new HashMap();
        QueryWrapper<Topic> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("is_top").orderByDesc("create_time");
        Page<Topic> page = new Page<>(current,4);
        try {
            Page<Topic> topic = topicMapper.selectPage(page, wrapper);
            List<Topic> records = topic.getRecords();
            resYes.put("total",(int)topic.getTotal());
            for (int i = 0; i < records.size(); i++) {
                User user = userService.getById(records.get(i).getCreateUserId());
                records.get(i).setNickName(user.getName());
                records.get(i).setAvatar(user.getAvatar());
            }
            resYes.put("list",records);

        } catch (Exception e) {
            resNo.put("msg","出现错误!");
            return Result.fail(ResponseCode.Code402,resNo);
        }
        return Result.suc(ResponseCode.Code200,resYes);
    }
    //热点资讯
    @Override
    public Result Focus() {
        resNo = new HashMap();
        resYes = new HashMap();
        QueryWrapper<Topic> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("number_of_views").last("limit 0 ,6");
        try {
            List<Topic> topics = topicMapper.selectList(wrapper);
            resYes.put("hotList",topics);

        } catch (Exception e) {
            resNo.put("msg","出现错误!");
            return Result.fail(ResponseCode.Code402,resNo);
        }
        return Result.suc(ResponseCode.Code200,resYes);
    }

    @Override
    public List<Topic> getUserTopicList(Long id) {
            LambdaQueryWrapper<Topic> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(Topic::getCreateUserId,id);
            List<Topic> list = list(queryWrapper);
            return list;

    }


}




