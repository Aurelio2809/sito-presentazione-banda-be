package org.example.sitopresentazionebandabenew;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
class SitoPresentazioneBandaBeNewApplicationTests {

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
        assertThat(port).isGreaterThan(0);
    }
}
