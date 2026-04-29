package com.example.identityservice.service.impl;

import com.example.identityservice.dto.response.user.BirthdayVoucherResponse;
import com.example.identityservice.dto.response.user.UserVoucherResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.repository.UserRepository;
import com.example.identityservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Override
    public BirthdayVoucherResponse generateBirthdayVouchers(int month) {
        List<User> users = userRepository.findByBirthMonth(month);
        int currentYear = Year.now().getValue();

        List<UserVoucherResponse> results = users.stream()
                .map(user -> {
                    LocalDate validFrom = LocalDate.of(currentYear,
                            user.getDob().getMonthValue(),
                            user.getDob().getDayOfMonth());

                    LocalDate validTo = validFrom.plusDays(10);

                    return UserVoucherResponse.builder()
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .dob(user.getDob())
                            .voucherValidFrom(validFrom)
                            .voucherValidTo(validTo)
                            .build();
                })
                .toList();

        return BirthdayVoucherResponse.builder()
                .users(results)
                .total(results.size())
                .build();

    }
}
