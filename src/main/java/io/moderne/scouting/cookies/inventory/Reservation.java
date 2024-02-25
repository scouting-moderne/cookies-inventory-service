package io.moderne.scouting.cookies.inventory;

import io.moderne.scouting.cookies.CookieType;
import io.moderne.scouting.cookies.user.User;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Value
public class Reservation {
    String id;
    User user;
    Instant reservedAt;
    Instant expiresAt;
    Map<CookieType, Integer> cookies;
    BigDecimal price;
}
