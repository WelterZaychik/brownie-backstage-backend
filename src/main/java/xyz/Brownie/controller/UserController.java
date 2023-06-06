package xyz.Brownie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.Brownie.domain.dto.UserDto;
import xyz.Brownie.domain.entity.User;
import xyz.Brownie.service.UserService;
import xyz.Brownie.util.ResponseCode;
import xyz.Brownie.util.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")

public class UserController {
    @Autowired
    private UserService userService;

    //登录
    // @PostMapping("/login")
    // public Result login(@RequestBody User user){
    //     return userService.login(user);
    // }

    //登录
    @PostMapping("/login")
    public Result login(@RequestParam("account") String account, @RequestParam("password") String password) {
        User user = new User();
        user.setAccount(account);
        user.setPassword(password);
        return userService.login(user);
    }
    // //登出
     @GetMapping("/logout/{account}")
      public Result logout(@PathVariable("account") String account){
         return userService.logout(account);
     }
    // 新建用户
    @PostMapping("/add")
    public Result add(@RequestBody User user){//创建前十个,直接给管理员
        return userService.addUser(user);
    }

    // 通过id查询user
    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Long id){
        return userService.getByIdPassword(id);
    }

    //通过id查询文章列表
    @GetMapping("/getTopicList/{id}")
    public Result TopicById(@PathVariable("id") Long id){
        return userService.TopicById(id);
    }

    //根据用户id查询用户的总观看数和总点赞数
    @GetMapping("/lvn/{id}")
    public Result LikeViewNum(@PathVariable("id")Long id){
        return userService.LikeViewNum(id);
    }

    //用户信息修改
    @PutMapping("/revise")
    public Result revise(@RequestBody User user){
        return userService.revise(user);
    }
    //删除用户
    @PostMapping
    public Result delete(List<Long> ids){
        Map res = new HashMap<>();
        try {
            userService.removeByIds(ids);
        } catch (Exception e) {
            res.put("msg","删除失败");
            return Result.fail(ResponseCode.Code402,res);
        }
        return Result.suc(ResponseCode.Code200);
    }

    @GetMapping("/list")
    public Result getUserList(){
        Map res = new HashMap<>();
        res.put("userList",userService.list(null));
        return Result.suc(ResponseCode.Code200,res);
    }

    //模糊查询
    @PostMapping("/selectSome")
    public Result selectSome(@RequestBody UserDto userDto){
        return userService.selectSome(userDto);
    }

    //删除用户
    @DeleteMapping("/{id}")
    public Result deleteUserById(@PathVariable String id){
        userService.removeById(id);
        return Result.suc(ResponseCode.Code200);
    }

}
