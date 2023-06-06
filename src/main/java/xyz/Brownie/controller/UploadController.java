package xyz.Brownie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.Brownie.service.VodService;
import xyz.Brownie.util.EmptyContentException;
import xyz.Brownie.util.ResponseCode;
import xyz.Brownie.util.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class UploadController {
    @Autowired
    private VodService vodService;

    private Map resYes;

    @PostMapping("/uploadVideo")
    public Result uploadVideo(@RequestParam("file") MultipartFile file){
        resYes = new HashMap();
        String videoID = vodService.uploadVideo(file);
        resYes.put("videoId",videoID);
        return Result.suc(ResponseCode.Code200,resYes);
    }

    @GetMapping("/getVideoPath/{id}")
    public Result getVideoPlayPath(@PathVariable("id") String id){
        resYes = new HashMap();
        String videoPath = vodService.getVideoPath(id);
        resYes.put("videoPath",videoPath);
        return Result.suc(ResponseCode.Code200,resYes);
    }

}
