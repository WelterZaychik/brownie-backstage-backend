package xyz.Brownie.domain.dto;

import lombok.Data;

@Data
public class UserDto {
        //关键字
    private String keyWord;
        //联系方式
    private String contact;
        //创建时间
    private String createTime;
        //修改时间
    private String updateTime;

}
