package io.moderne.scouting.cookies.inventory;

import io.moderne.scouting.cookies.CookieCalculator;
import io.moderne.scouting.cookies.CookieType;
import io.moderne.scouting.cookies.error.ApiError;
import io.moderne.scouting.cookies.error.ApiException;
import io.moderne.scouting.cookies.inventory.user.UserClient;
import io.moderne.scouting.cookies.user.User;
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
    private final UserClient userClient;

    public Reservation reserve(String userId, Map<CookieType, Integer> cookies) {
        User user = userClient.findUser(userId).orElseThrow(() -> new ApiException(new ApiError("User", "User not found")));
        if (inventoryService.checkAvailability(cookies)) {
            Reservation reservation = createReservation(user, cookies);
            inventoryService.reserve(cookies);
            return reservation;
        }
        throw new ApiException(new ApiError("Inventory", "Not enough inventory"));
    }

    private Reservation createReservation(User user, Map<CookieType, Integer> cookies) {
        BigDecimal price = CookieCalculator.calculatePrice(cookies);
        var reservation = new Reservation(UUID.randomUUID().toString(), user, Instant.now(), Instant.now().plusSeconds(60), cookies, price);
        db.put(reservation.getId(), reservation);
        return reservation;
    }
}
