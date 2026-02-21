package org.ssu.belous;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendCareerGuidanceApplication
{
    public static void main(String[] args) {
        SpringApplication.run(BackendCareerGuidanceApplication.class, args);
    }
}
