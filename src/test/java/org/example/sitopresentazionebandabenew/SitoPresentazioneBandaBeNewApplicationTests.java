package org.example.sitopresentazionebandabenew;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SitoPresentazioneBandaBeNewApplicationTests {

    @LocalServerPort
    private int port;

    @Test
    void contextLoadsOnAnEphemeralDatabase() {
        assertThat(port).isGreaterThan(0);
    }

    @Test
    void exposesPublicGalleryAndProtectsAdminGallery() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> publicResponse = client.send(
                HttpRequest.newBuilder(uri("/api/gallery/public")).GET().build(), HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> protectedResponse = client.send(
                HttpRequest.newBuilder(uri("/api/gallery")).GET().build(), HttpResponse.BodyHandlers.ofString());

        assertThat(publicResponse.statusCode()).isEqualTo(200);
        assertThat(publicResponse.body()).contains("\"content\"");
        assertThat(publicResponse.headers().firstValue("x-frame-options")).contains("DENY");
        assertThat(protectedResponse.statusCode()).isEqualTo(401);
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }
}
