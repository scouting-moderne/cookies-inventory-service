package io.moderne.scouting.cookies.inventory;

import io.moderne.scouting.cookies.CookieType;
import io.moderne.scouting.cookies.error.ErrorHandling;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Import(ErrorHandling.class)
class InventoryController {

    private final InventoryService inventoryService;
    private final ReservationService reservationService;

    @PostMapping("/reserve")
    public Reservation reserve(@RequestBody ReservationRequest request) {
        return reservationService.reserve(request.userId(), request.cookies());
    }

    @PostMapping("/check")
    public boolean check(@RequestBody InventoryRequest request) {
        return inventoryService.checkAvailability(request.cookies());
    }

    record InventoryRequest(Map<CookieType, Integer> cookies) {
    }

    record ReservationRequest(String userId, Map<CookieType, Integer> cookies) {
    }
}
