package xyz.Brownie.domain.dto;

import lombok.Data;

@Data
public class TopicDto {
    private String keyWord;
    private String startTime;
    private String endTime;
    private Long tagId;
    private String isVideo;
    private Integer current;
    private String isTime;
}
