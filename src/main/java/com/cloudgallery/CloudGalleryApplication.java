package com.cloudgallery;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.cloudgallery.mapper")
public class CloudGalleryApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudGalleryApplication.class, args);
    }

}
