package com.configbuilder.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class Controller {

//    @Value("${build.id}")
////    private String id;
//
//    @Value("${build.version}")
//    private String version;
//
//    @Value("${build.name}")
//    private String name;

    @Autowired
    private BuildInfo buildInfo;

    @GetMapping("/build-info")
    public String getConfig(){
        return "Build ID: " + buildInfo.getId() + " Version: " + buildInfo.getVersion() + " Name: " + buildInfo.getName();
    }
}
