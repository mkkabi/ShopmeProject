package com.shopme.admin;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path dirName = Paths.get("user-photos");
        String userPhotosPath = dirName.toFile().getAbsolutePath();
        registry.addResourceHandler("/"+dirName+"/**").addResourceLocations("file:/"+userPhotosPath+"/");
        registry.addResourceHandler("/"+dirName+"/**").addResourceLocations("file:ShopmeWebParent/ShopmeBackEnd/user-photos/");
    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        exposeDirectory("ShopmeWebParent/ShopmeBackEnd/user-photos", registry);
//    }
//
//    private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
//        Path uploadDir = Paths.get(dirName);
//        String uploadPath = uploadDir.toFile().getAbsolutePath();
//
//        if (dirName.startsWith("../")) dirName = dirName.replace("../", "");
//
//        registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/"+ uploadPath + "/");
//    }
}
