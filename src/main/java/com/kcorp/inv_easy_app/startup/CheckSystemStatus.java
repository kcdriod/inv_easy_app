package com.kcorp.inv_easy_app.startup;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CheckSystemStatus implements ApplicationRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(CheckSystemStatus.class);

    private final JdbcTemplate jdbcTemplate;


    public CheckSystemStatus(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(@NonNull ApplicationArguments args) throws Exception {
        logger.info("Starting DB checks");
        try {
            Integer result = jdbcTemplate.queryForObject("select 1", Integer.class);
            if (result != null && result == 1) {
                logger.info("DB checks successful");
            } else {
                logger.warn("DB reposnded with unexpected result");
            }
        } catch(Exception c){
            logger.warn("DB checks failed");
                throw new IllegalAccessException();
            }
        logger.info("System Status check Completed");

    }
}
