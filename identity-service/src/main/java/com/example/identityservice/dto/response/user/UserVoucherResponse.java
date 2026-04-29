package com.example.identityservice.dto.response.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserVoucherResponse {
    String username;
    String email;
    LocalDate dob;
    LocalDate voucherValidFrom;
    LocalDate voucherValidTo;
}