package io.moderne.scouting.cookies.inventory;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class UserMocks {

    public static void setupUserMocks(WireMockServer mockService) {
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/users/abc123"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "id": "abc123",
                                    "username": "User Name"
                                }
                                """)));

        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/users/unknown"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(404)
                        .withBody("""
                                {
                                    "error": "not found"
                                }
                                """)));
    }
}
