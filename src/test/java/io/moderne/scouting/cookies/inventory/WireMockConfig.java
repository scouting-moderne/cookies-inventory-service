package io.moderne.scouting.cookies.inventory;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles("wiremock")
public class WireMockConfig {
    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockUserService() {
        return new WireMockServer(12345);
    }
}
