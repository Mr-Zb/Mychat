package com.fun;

import com.fun.framework.utils.Constants;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApplicationCommandLineRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("正在初始化系统...");

        Constants.init = true;
        log.info("正在初始化系统...done");
    }

}
