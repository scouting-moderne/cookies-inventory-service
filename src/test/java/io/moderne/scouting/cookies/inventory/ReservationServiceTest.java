package io.moderne.scouting.cookies.inventory;

import io.moderne.scouting.cookies.CookieCalculator;
import io.moderne.scouting.cookies.CookieType;
import io.moderne.scouting.cookies.error.ApiException;
import io.moderne.scouting.cookies.inventory.user.UserClient;
import io.moderne.scouting.cookies.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private ReservationService reservationService;
    private InventoryService inventoryService = new InventoryService();

    @Mock
    private UserClient userClient;

    @BeforeEach
    public void setUp() {
        reservationService = new ReservationService(inventoryService, userClient);
    }

    @Test
    void reserve() {
        when(userClient.findUser("userId")).thenReturn(Optional.of(new User("user", "name", "email")));
        inventoryService.addToInventory(CookieType.EIERKOEK, 10);
        Reservation reservation = reservationService.reserve("userId", Map.of(CookieType.EIERKOEK, 10));
        assertEquals(CookieType.EIERKOEK, reservation.getCookies().keySet().iterator().next());
        BigDecimal expectedPrice = CookieCalculator.calculatePrice(Map.of(CookieType.EIERKOEK, 10));
        assertEquals(expectedPrice, reservation.getPrice());
    }

    @Test
    void reserveFails() {
        when(userClient.findUser("userId")).thenReturn(Optional.of(new User("user", "name", "email")));
        inventoryService.addToInventory(CookieType.KANO, 10);
        assertThrows(ApiException.class, () -> {
            reservationService.reserve("userId", Map.of(CookieType.EIERKOEK, 10));
        });
    }
}