package cn.edu.xmu.oomall.freight.config;

import cn.edu.xmu.javaee.core.util.JwtHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(StartupRunner.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        JwtHelper jwtHelper = new JwtHelper();
        String adminToken = jwtHelper.createToken(2L, "shop2", 2L, 1, 3600);
        logger.info("test token = {}", adminToken);
    }
}