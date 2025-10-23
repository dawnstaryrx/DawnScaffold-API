package com.gooodh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class DawnScaffoldApplication {
    public static void main(String[] args) {
        SpringApplication.run(DawnScaffoldApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void afterStartup() {
        System.out.println("✅ Dawn脚手架启动成功！🚀");
        System.out.println("🌐 Swagger接口文档: http://localhost:8080/swagger-ui/index.html");
    }
}
