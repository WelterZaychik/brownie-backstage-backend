package xyz.Brownie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.Brownie.domain.entity.Tag;
import xyz.Brownie.util.ResponseCode;
import xyz.Brownie.util.Result;
import xyz.Brownie.service.TagService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    private Map resYes;

    @GetMapping
    public Result getTagList(){
        resYes = new HashMap();
        resYes.put("tagList",tagService.list());
        return Result.suc(ResponseCode.Code200,resYes);
    }

    @GetMapping("/{keyword}")
    public Result searchTag(@PathVariable String keyword){
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Tag::getContent,keyword);
        List<Tag> list = tagService.list(queryWrapper);
        resYes = new HashMap();
        resYes.put("tagList",list);
        return Result.suc(ResponseCode.Code200,resYes);
    }

    @DeleteMapping("/{id}")
    public Result delTag(@PathVariable("id") Long id){
        tagService.removeById(id);
        return Result.suc(ResponseCode.Code200);
    }

    @PostMapping
    public Result addTag(@RequestBody Tag tag){
        boolean save = tagService.save(tag);
        if (save){
            return Result.suc(ResponseCode.Code200);
        }else{
            return Result.suc(ResponseCode.Code402);
        }
    }


}
