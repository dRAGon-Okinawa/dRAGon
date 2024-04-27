package ai.dragon.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("prod")
@Configuration(proxyBeanMethods = false)
public class ProdProfileSecurityConfiguration {
}
