package ai.dragon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class DragonApplication {
    public static void main(String[] args) {
        SpringApplication.run(DragonApplication.class, args);
    }
}
