package com.example.identityservice.service;

import com.example.identityservice.dto.response.user.BirthdayVoucherResponse;

public interface UserService {
    BirthdayVoucherResponse generateBirthdayVouchers(int month);
}
