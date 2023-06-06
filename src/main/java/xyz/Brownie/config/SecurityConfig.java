package xyz.Brownie.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import xyz.Brownie.filter.JwtAuthenticationTokenFilter;
import xyz.Brownie.handler.LoginFailureHandler;
import xyz.Brownie.handler.LoginSuccessHandler;
import javax.sql.DataSource;

@Configuration
//开启Spring Security 注解
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;
    //注入数据源
    @Autowired
    private DataSource dataSource;
    @Autowired
    private LoginSuccessHandler loginSuccessHandler;
    @Autowired
    private LoginFailureHandler loginFailureHandler;

    //配置对象
    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }
    //自定义token过滤器
    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.userDetailsService(userDetailsService).passwordEncoder(password());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //配置自己的jwt验证过滤器
        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
        http.formLogin()//自定义自己编写的登录页面
                .loginProcessingUrl("/user/login")//登录访问的路径
                .usernameParameter("account")
                .passwordParameter("password")
                .successHandler(loginSuccessHandler)//设置成功处理器
                .failureHandler(loginFailureHandler)//设置失败处理器
                .and().authorizeRequests()
                .antMatchers("/user/login").permitAll()//设置哪些路径可以直接访问,不需要认证
                .antMatchers("/**").hasAnyAuthority("admin")
                .anyRequest().authenticated()
                .and().csrf().disable();//关闭csrf防护


    }

    @Bean
    PasswordEncoder password(){
        return new BCryptPasswordEncoder();
    }

}
