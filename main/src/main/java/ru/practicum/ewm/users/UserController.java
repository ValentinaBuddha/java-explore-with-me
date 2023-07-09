package ru.practicum.ewm.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.users.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(value = "ids", required = false) List<Long> ids,
                                  @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                  @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        return userService.addUser(userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
