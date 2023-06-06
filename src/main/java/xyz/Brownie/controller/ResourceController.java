package xyz.Brownie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xyz.Brownie.domain.entity.Resource;
import xyz.Brownie.util.ResponseCode;
import xyz.Brownie.util.Result;
import xyz.Brownie.service.ResourceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resource")
public class ResourceController {
    @Autowired
    private ResourceService service;
    //新建资源
    @PostMapping
    public Result AddResource(@RequestBody Resource resource){
        return service.AddResource(resource);
    }
    //删除资源
    @DeleteMapping("/{id}")
    public Result DeleteResource(@PathVariable("id") long id){
        return service.DeleteResource(id);
    }
    //修改资源
    @PutMapping
    public Result UpdateResource(@RequestBody Resource resource){
        return service.UpdateResource(resource);
    }

    //查询资源
    @GetMapping("/search/{keyword}")
    public Result SelectResult(@PathVariable(value = "keyword") String keyword){
        LambdaQueryWrapper<Resource> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(keyword),Resource::getTitle,keyword);
        List<Resource> list = service.list(queryWrapper);
        Map res = new HashMap();
        res.put("resourceList",list);
        return Result.suc(ResponseCode.Code200,res);
    }

    //资源详情
    @GetMapping("/{id}")
    public Result getResourceDetail(@PathVariable("id") Long id){
        Map res = new HashMap();
        res.put("resourceObj",service.getById(id));
        return Result.suc(ResponseCode.Code200,res);
    }

    //获取资源列表
    @GetMapping("/search")
    public Result getResourceList(){
        Map res = new HashMap<>();
        res.put("resourceList",service.list(null));
        return Result.suc(ResponseCode.Code200,res);
    }

}
