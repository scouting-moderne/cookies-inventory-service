package io.moderne.scouting.cookies.inventory;

import io.moderne.scouting.cookies.CookieType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
class InventoryController {

    private final InventoryService inventoryService;
    private final ReservationService reservationService;

    @PostMapping("/reserve")
    public Reservation reserve(@RequestBody InventoryRequest request) {
        return reservationService.reserve(request.cookies());
    }

    @PostMapping("/check")
    public boolean check(@RequestBody InventoryRequest request) {
        return inventoryService.checkAvailability(request.cookies());
    }

    record InventoryRequest(Map<CookieType, Integer> cookies) {
    }
}
