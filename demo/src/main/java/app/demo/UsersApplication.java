package app.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.util.logging.Logger;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class UsersApplication {

    private static final Logger LOGGER =
            Logger.getLogger(UsersApplication.class.getName());

    public static void main(String[] args) {
        SpringApplication.run(UsersApplication.class, args);
        LOGGER.info("APP is running...");
    }
}
