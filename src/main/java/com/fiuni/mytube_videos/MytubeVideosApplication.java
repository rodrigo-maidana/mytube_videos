package com.fiuni.mytube_videos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EntityScan(basePackages = "com.fiuni.mytube.domain")
@EnableCaching
public class MytubeVideosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MytubeVideosApplication.class, args);
    }

}
