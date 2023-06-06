package xyz.Brownie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.util.ObjectUtils;
import xyz.Brownie.domain.dto.LoginDto;
import xyz.Brownie.mapper.UserMapper;
import xyz.Brownie.util.RedisCache;
import xyz.Brownie.util.Sm4EncryptUtil;

import java.util.List;

@Service("userDetailsService")
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        //调用UsersMapper方法,根据用户名查询数据库
        QueryWrapper<xyz.Brownie.domain.entity.User> wrapper = new QueryWrapper<>();
        wrapper.eq("account",account);
        xyz.Brownie.domain.entity.User user = userMapper.selectOne(wrapper);
        //解密密码
        user.setPassword(Sm4EncryptUtil.decrypt(user.getPassword()));
        //判断
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("a");
//        Integer roleId = user.getRoleId();
        if (!(ObjectUtils.isEmpty(user)) &&user.getRoleId() == 0){//判断是否管理员
            authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("admin");
            return new User(user.getAccount(),new BCryptPasswordEncoder().encode(user.getPassword()),authorities);
        }
        if (user == null){//数据库没有用户名,认证失败
            throw new UsernameNotFoundException("用户名不存在");
        }

        //从查询数据库返回users对象得到用户名和密码,返回
        return new User("null","null",authorities);
    }
}
