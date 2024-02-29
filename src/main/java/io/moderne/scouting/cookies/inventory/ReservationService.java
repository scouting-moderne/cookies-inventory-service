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
        User user = userClient.findUser(userId).orElseThrow(() -> new ApiException(new ApiError("User", "User not found", 500)));
        if (inventoryService.checkAvailability(cookies)) {
            Reservation reservation = createReservation(user, cookies);
            inventoryService.reserve(cookies);
            return reservation;
        }
        throw new ApiException(new ApiError("Inventory", "Not enough inventory", 500));
    }

    private Reservation createReservation(User user, Map<CookieType, Integer> cookies) {
        BigDecimal price = CookieCalculator.calculatePrice(cookies);
        var reservation = new Reservation(UUID.randomUUID().toString(), user, Instant.now(), Instant.now().plusSeconds(60), cookies, price);
        db.put(reservation.getId(), reservation);
        return reservation;
    }

    public Reservation verified(String userId, String reservationId) {
        User user = userClient.findUser(userId).orElseThrow(() -> new ApiException(new ApiError("User", "User not found", 500)));
        Reservation found = db.get(reservationId);
        if (found == null) {
            return null;
        }
        if (found.getExpiresAt() == null || found.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException(new ApiError("Reservation", "Reservation expired", 500));
        }
        if (!found.getUser().equals(user)) {
            throw new ApiException(new ApiError("Reservation", "User mismatch", 500));
        }
        return found;
    }
}
