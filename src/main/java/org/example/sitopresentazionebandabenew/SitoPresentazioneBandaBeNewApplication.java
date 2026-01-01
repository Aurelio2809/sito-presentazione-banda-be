package org.example.sitopresentazionebandabenew;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SitoPresentazioneBandaBeNewApplication {

    public static void main(String[] args) {
        SpringApplication.run(SitoPresentazioneBandaBeNewApplication.class, args);
    }

}