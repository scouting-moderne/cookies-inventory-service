package io.moderne.scouting.cookies.inventory;

import io.moderne.scouting.cookies.CookieType;
import io.moderne.scouting.cookies.error.ApiError;
import io.moderne.scouting.cookies.error.ApiException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InventoryService {
    private final Map<CookieType, Integer> inventory = new HashMap<>();

    public void addToInventory(CookieType type, int quantity) {
        inventory.merge(type, quantity, Integer::sum);
    }

    public boolean checkAvailability(CookieType cookie, int quantity) {
        return inventory.getOrDefault(cookie, 0) >= quantity;
    }

    public boolean checkAvailability(Map<CookieType, Integer> cookies) {
        return cookies.entrySet().stream().allMatch(e -> checkAvailability(e.getKey(), e.getValue()));
    }

    public void reserve(Map<CookieType, Integer> cookies) {
        Map<CookieType, Integer> newInventory = new HashMap<>(inventory);
        cookies.forEach((cookieType, quantity) -> newInventory.merge(cookieType, -quantity, Integer::sum));
        if (newInventory.values().stream().anyMatch(i -> i < 0)) {
            throw new ApiException(new ApiError("Inventory", "Not enough inventory", null));
        }
        inventory.clear();
        inventory.putAll(newInventory);
    }
}
