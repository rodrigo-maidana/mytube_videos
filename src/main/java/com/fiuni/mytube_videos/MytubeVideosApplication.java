package com.fiuni.mytube_videos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.fiuni.mytube.domain")
public class MytubeVideosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MytubeVideosApplication.class, args);
    }

}
