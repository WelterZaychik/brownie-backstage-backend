package xyz.Brownie.service;

import org.springframework.web.multipart.MultipartFile;

public interface VodService {

    String uploadVideo(MultipartFile file);


    String getVideoPath(String id);
}
