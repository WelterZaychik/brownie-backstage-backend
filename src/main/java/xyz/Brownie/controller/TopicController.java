package xyz.Brownie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import xyz.Brownie.domain.dto.TopicDto;
import xyz.Brownie.domain.entity.Topic;
import xyz.Brownie.domain.entity.User;
import xyz.Brownie.service.TagService;
import xyz.Brownie.service.TopicService;
import xyz.Brownie.service.UserService;
import xyz.Brownie.util.ResponseCode;
import xyz.Brownie.util.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/topic")
public class TopicController {
    @Autowired
    private TopicService topicService;

    @Autowired
    private UserService userService;


    //创建帖子
    @PostMapping
    public Result publish(@RequestBody Topic topic){return topicService.publish(topic);
    }
    //修改帖子
    @PutMapping
    public Result revise(@RequestBody Topic topic){
        return topicService.revise(topic);
    }
    //删除帖子
    @DeleteMapping("/{ids}")
    public Result delete(@PathVariable("ids") Long ids){
       return topicService.delete(ids);
    }
    //查询帖子
    @PostMapping("/selectsome")
    public Result SelectSome(@RequestBody TopicDto topicDto){
        return topicService.SelectSome(topicDto);
    }

    //帖子的点赞
    @GetMapping("/addlikes/{id}")
    public Result addlikes(@PathVariable("id") Long id){
        return topicService.addlikes(id);
    }
    //帖子观看数
//    @GetMapping("/addviews/{id}")
//    public Result addviews(@PathVariable("id") Long id){
//        return topicService.addviews(id);
//    }
    //分页查询
    @GetMapping("/pagination/{current}")
    public Result pagination(@PathVariable("current") Integer current){
        return topicService.pagination(current);
    }
    //帖子详情
    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable("id") Long id){
        return topicService.detail(id);
    }
    //官方发文的帖子
    @GetMapping("/hostoftopic/{curPage}/{pageSize}")
    public Result hostoftopic(@PathVariable("curPage") int curPage ,
                              @PathVariable("pageSize") int pageSize){
        return topicService.HostOfTopic(curPage,pageSize);
    }
    //双重排序
    @GetMapping("/SelectChoose/{current}")
    public Result SelectChoose(@PathVariable("current") Integer current){
        return topicService.SelectChoose(current);
    }
    //热点资讯
    @GetMapping("/focus")
    private Result Focus(){
        return topicService.Focus();
    }

    //为用户模块提供服务，通过用户id查询帖子
    /*@GetMapping("/getUserTopic/{userId}")
    private List<Topic> getUserTopicList(@PathVariable("userId") Long id){
        LambdaQueryWrapper<Topic> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Topic::getCreateUserId,id);
        List<Topic> list = topicService.list(queryWrapper);
        return list;
    }*/
    @GetMapping("/isTop/{id}/{isTop}")
    public Result upDataIsTop(@PathVariable String id, @PathVariable Integer isTop){
        Map res = new HashMap<>();
        LambdaUpdateWrapper<Topic> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Topic::getId,id)
                .set(Topic::getIsTop,isTop);
        topicService.update(updateWrapper);
        return Result.suc(ResponseCode.Code200);
    }
    @GetMapping("/list")
    public Result getUserList(){
        Map res = new HashMap<>();
        List<Topic> list = topicService.list(null);
        for (int i = 0; i < list.size(); i++) {
            User user = userService.getById(list.get(i).getCreateUserId());
            list.get(i).setNickName(user.getName());
            list.get(i).setAvatar(user.getAvatar());
        }
        res.put("topicList",list);
        return Result.suc(ResponseCode.Code200,res);
    }
}
