package xyz.Brownie.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import xyz.Brownie.util.ResponseCode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        /* 默认：执行重定向或转发到defaultfailureurl(如果设置)，Otherw返回401错误代码 */
        //super.onAuthenticationFailure(request,response,exception)

//        response.setStatus(HttpServletResponse.SC_PAYMENT_REQUIRED);
        response.setContentType("application/json;charset=utf-8");
        Map<String,Object> map =new HashMap<>();
        //设置返回内容
        map.put("code", ResponseCode.Code402);
        map.put("msg","登录失败账号或密码错误");
        PrintWriter writer = response.getWriter();
        ObjectMapper objectMapper=new ObjectMapper();
        writer.write(objectMapper.writeValueAsString(map));
//        writer.write(exception.getMessage());
        writer.flush();
        writer.close();
    }
}

