package xyz.Brownie.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.Brownie.util.RedisCache;
import xyz.Brownie.util.ResponseCode;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private RedisCache redisCache;



    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String url = httpServletRequest.getRequestURI();

        if (url.contains("/user/logout")){
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        if (url.equals("/user/login")){
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        String Token = httpServletRequest.getHeader("token");
        String account = httpServletRequest.getHeader("account");
        if (Token == null || account == null){
            httpServletResponse.setContentType("application/json;charset=utf-8");
            Map<String,Object> map =new HashMap<>();
            //设置返回内容
            map.put("code", ResponseCode.Code402);
            map.put("msg","未知错误,请重新登录");
            PrintWriter writer = httpServletResponse.getWriter();
            ObjectMapper objectMapper=new ObjectMapper();
            writer.write(objectMapper.writeValueAsString(map));
            writer.flush();
            writer.close();
            return;
        }
        System.out.println("进入自定义过滤器");
        String rdToken = (String) redisCache.getCacheObject("token-" + account);
            if (Token.equals(rdToken)){
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(account);
                if (StringUtils.hasText(userDetails.getPassword())){
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    //将authentication放入SecurityContextHolder中
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    filterChain.doFilter(httpServletRequest, httpServletResponse);
                }
            }


    }
}


