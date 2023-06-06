package xyz.Brownie.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import xyz.Brownie.mapper.UserMapper;
import xyz.Brownie.util.AppJwtUtil;
import xyz.Brownie.util.RedisCache;
import xyz.Brownie.util.ResponseCode;
import xyz.Brownie.util.Result;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
//登录成功处理器
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisCache redisCache;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        //获取当前登录用户主体信息，包括用户名密码，角色，账号状态
        Object principal = authentication.getPrincipal();
        User loginUser = (User)principal;
        QueryWrapper<xyz.Brownie.domain.entity.User> wrapper = new QueryWrapper<>();
        wrapper.eq("account",loginUser.getUsername());
        xyz.Brownie.domain.entity.User user = userMapper.selectOne(wrapper);
        //设置返回编码格式，使用PrintWriter方法输出
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        //设置resp返回状态，成功为200
        response.setStatus(200);
        Map<String,Object> map =new HashMap<>();
        //创建token
        String token = AppJwtUtil.getToken(user.getId());
        //设置返回内容
        map.put("userInfo",user);
        map.put("token",token);
        redisCache.setCacheObject("token-"+user.getAccount(),token);
        Result result = new Result(ResponseCode.Code200, map);
        //使用springboot自带的Jackson转化并输出JSON信息
        ObjectMapper objectMapper=new ObjectMapper();
        out.write(objectMapper.writeValueAsString(result));
        out.flush();
        out.close();
    }
}

