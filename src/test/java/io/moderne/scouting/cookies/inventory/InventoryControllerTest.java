package io.moderne.scouting.cookies.inventory;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.moderne.scouting.cookies.CookieType;
import io.moderne.scouting.cookies.error.ApiError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("wiremock")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {WireMockConfig.class})
public class InventoryControllerTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    private WireMockServer mockUserService;

    @Autowired
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        UserMocks.setupUserMocks(mockUserService);
        inventoryService.addToInventory(CookieType.KANO, 10);
        inventoryService.addToInventory(CookieType.STROOPWAFEL, 10);
    }

    @Test
    void createReservation() {
        Map<CookieType, Integer> cookies = Map.of(CookieType.STROOPWAFEL, 1, CookieType.KANO, 2);
        InventoryController.ReservationRequest request = new InventoryController.ReservationRequest("abc123", cookies);
        ResponseEntity<Reservation> response = testRestTemplate.postForEntity("/inventory/reserve", request, Reservation.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getPrice()).isEqualTo(BigDecimal.valueOf(2.5));
        assertThat(response.getBody().getCookies()).isEqualTo(cookies);
        assertThat(response.getBody().getUser().id()).isEqualTo("abc123");
    }

    @Test
    void createOrderWithUnknownUserThrowsApiError() {
        Map<CookieType, Integer> cookies = Map.of(CookieType.STROOPWAFEL, 1, CookieType.KANO, 2);
        InventoryController.ReservationRequest request = new InventoryController.ReservationRequest("unknown", cookies);
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity("/inventory/reserve", request, ApiError.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo(new ApiError("User", "User not found"));
    }

    @Test
    void createOrderWithTooLargeOrderThrowsApiError() {
        Map<CookieType, Integer> cookies = Map.of(CookieType.STROOPWAFEL, 100, CookieType.KANO, 200);
        InventoryController.ReservationRequest request = new InventoryController.ReservationRequest("abc123", cookies);
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity("/inventory/reserve", request, ApiError.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo(new ApiError("Inventory", "Not enough inventory"));
    }
}
