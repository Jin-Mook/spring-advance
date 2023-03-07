package com.kt.myrestapi.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DatabaseRunner implements ApplicationRunner {

    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("datasource 구현 객체 = {}", dataSource.getClass().getName());
        try (Connection connection = dataSource.getConnection()) {
            log.info(connection.getMetaData().getURL());
            log.info(connection.getMetaData().getUserName());
        }
    }
}
