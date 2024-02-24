package io.moderne.scouting.cookies.inventory;

import io.moderne.scouting.cookies.CookieType;
import io.moderne.scouting.cookies.CookieCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final Map<String, Reservation> db = new HashMap<>();
    private final InventoryService inventoryService;

    public Reservation reserve(Map<CookieType, Integer> cookies) {
        if (inventoryService.checkAvailability(cookies)) {
            Reservation reservation = createReservation(cookies);
            inventoryService.reserve(cookies);
            return reservation;
        }
        throw new IllegalArgumentException("Not enough inventory");
    }

    private Reservation createReservation(Map<CookieType, Integer> cookies) {
        BigDecimal price = CookieCalculator.calculatePrice(cookies);
        var reservation = new Reservation(UUID.randomUUID().toString(), Instant.now(), Instant.now().plusSeconds(60), cookies, price);
        db.put(reservation.getId(), reservation);
        return reservation;
    }
}
