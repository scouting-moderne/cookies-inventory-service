package io.moderne.scouting.cookies.inventory.user;

import io.moderne.scouting.cookies.user.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@FeignClient(value = "user", url = "${service.user.url:http://cookies-user-service:8080}", dismiss404 = true)
public interface UserClient {
    @PostMapping("/users")
    User createUser(@RequestBody User user);

    @GetMapping("/users/{id}")
    Optional<User> findUser(@PathVariable String id);

    @PutMapping("/users/{id}")
    User updateUser(@PathVariable String id, @RequestBody User user);

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable String id);
}
