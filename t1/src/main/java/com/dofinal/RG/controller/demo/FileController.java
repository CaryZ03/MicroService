package com.dofinal.RG.controller.demo;

import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * &#064;Classname FileController
 * &#064;Description  TODO
 * &#064;Date 2024/5/4 19:42
 * &#064;Created MuJue
 */
@RestController
public class FileController {
    @PostMapping("/upload")
    @Trace
    public String up(String nickname, MultipartFile photo, HttpServletRequest request) throws IOException {
        System.out.println(nickname);
        System.out.println(photo.getOriginalFilename());
        System.out.println(photo.getContentType());

        String path = request.getServletContext().getRealPath("/upload");
        System.out.println(path);
        saveFile(photo, path);
        return "upload file success!";
    }
    @Trace
    public void saveFile(MultipartFile photo, String path) throws IOException {
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(path + photo.getOriginalFilename());
        photo.transferTo(file);
    }
}
