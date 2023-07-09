package ru.practicum.ewm.users;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.users.dto.UserDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> userIds, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        if (userIds == null) {
            return Collections.emptyList();
        }
        return userRepository.findAllByIdIn(userIds, pageable).stream().map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto addUser(UserDto userDto) {
        if (userRepository.existsByName(userDto.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User name is already used.");
        }
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        userRepository.deleteById(user.getId());
    }

}
