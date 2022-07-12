package spring;

import org.apache.tomcat.jni.Local;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

@SpringBootApplication
@EnableScheduling
public class Application {
    public static int numberOfShields = 12;
    public static int wishedNumberOfJuniors = 5;
    public static int wishedNumberOfDemandingTrainer = 7;
    public static LocalTime lenghtOfMasterClass = LocalTime.of(2, 0);
    public static LocalTime[] beginnings = new LocalTime[] {LocalTime.of(18, 0), LocalTime.of(18, 0),
            LocalTime.of(18, 0), LocalTime.of(18, 0), LocalTime.of(18, 0),
            LocalTime.of(23, 59), LocalTime.of(23, 59)};
    public static LocalTime[] ends = new LocalTime[] {LocalTime.of(22, 0), LocalTime.of(22, 0),
            LocalTime.of(22, 0), LocalTime.of(22, 0), LocalTime.of(22, 0),
            LocalTime.of(0, 0), LocalTime.of(0, 0)};
    public static Boolean[] areLessons = new Boolean[] {true, true, true, true, true, false, false};

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
