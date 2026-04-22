package com.akash.supplychain.gateway;

import com.akash.supplychain.gateway.dto.LoginRequest;
import com.akash.supplychain.gateway.dto.TokenResponse;
import com.akash.supplychain.gateway.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtService jwtService;

    @Test
    void healthEndpointIsPublic() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    void protectedApiRequiresBearerToken() {
        webTestClient.get()
                .uri("/api/v1/dashboard/data")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void tokenEndpointIssuesValidJwtForDemoUser() {
        TokenResponse response = webTestClient.post()
                .uri("/auth/token")
                .bodyValue(new LoginRequest("akash", "supplychain2026"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(jwtService.validate(response.accessToken())).isPresent();
    }
}
