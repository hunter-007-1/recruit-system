package com.recruit;

import com.recruit.common.LocalUser;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@MapperScan(basePackages = {"com.recruit.mapper"})
@SpringBootApplication(scanBasePackages={"com.recruit.*"})
public class RecruitApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecruitApplication.class, args);
        LocalUser.clearLocalUser();
    }

    @org.springframework.web.bind.annotation.GetMapping("/api/ping")
    public java.util.Map<String, Object> ping() {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("code", 200);
        result.put("message", "pong");
        result.put("service", "recruit-system");
        result.put("time", new java.util.Date());
        return result;
    }
}
