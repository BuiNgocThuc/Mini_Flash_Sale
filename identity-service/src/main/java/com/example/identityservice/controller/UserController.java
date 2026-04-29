package com.example.identityservice.controller;

import com.example.identityservice.dto.response.APIResponse;
import com.example.identityservice.dto.response.user.BirthdayVoucherResponse;
import com.example.identityservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping("/birthday-vouchers/{month}")
    public APIResponse<BirthdayVoucherResponse> getBirthdayVouchers(@PathVariable("month") Integer month) {
        var result = userService.generateBirthdayVouchers(month);
        return APIResponse.<BirthdayVoucherResponse>builder()
                .result(result)
                .message("Lấy voucher sinh nhật thành công!")
                .build();
    }
}
