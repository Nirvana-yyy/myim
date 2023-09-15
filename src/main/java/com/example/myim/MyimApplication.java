package com.example.myim;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
@Slf4j
public class MyimApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyimApplication.class, args);
    }

}
