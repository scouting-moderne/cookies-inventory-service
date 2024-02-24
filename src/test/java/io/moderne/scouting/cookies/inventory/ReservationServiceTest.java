package io.moderne.scouting.cookies.inventory;

import io.moderne.scouting.cookies.CookieCalculator;
import io.moderne.scouting.cookies.CookieType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private InventoryService inventoryService;

    @Test
    void reserve() {
        inventoryService.addToInventory(CookieType.EIERKOEK, 10);
        Reservation reservation = reservationService.reserve(Map.of(CookieType.EIERKOEK, 10));
        assertEquals(CookieType.EIERKOEK, reservation.getCookies().keySet().iterator().next());
        BigDecimal expectedPrice = CookieCalculator.calculatePrice(Map.of(CookieType.EIERKOEK, 10));
        assertEquals(expectedPrice, reservation.getPrice());
    }

    @Test
    void reserveFails() {
        inventoryService.addToInventory(CookieType.KANO, 10);
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.reserve(Map.of(CookieType.EIERKOEK, 10));
        });
    }
}