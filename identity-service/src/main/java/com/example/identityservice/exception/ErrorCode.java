package com.example.identityservice.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    // Infrastructure Errors
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi hệ thống không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncaught Error", HttpStatus.BAD_REQUEST),

    // Auth & Security Errors
    UNAUTHENTICATED(1002, "Tài khoản hoặc mật khẩu không chính xác", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1003, "Bạn không có quyền truy cập tài nguyên này", HttpStatus.FORBIDDEN),
    INVALID_TOKEN(1004, "Token không hợp lệ hoặc đã hết hạn", HttpStatus.UNAUTHORIZED),

    // Business Logic Errors (User)
    USER_EXISTED(1005, "Tên đăng nhập đã tồn tại", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1006, "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    PASSWORD_NOT_MATCHED(1007, "Mật khẩu không khớp", HttpStatus.BAD_REQUEST),

    // Validation Errors
    INVALID_INPUT(1018, "Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1009, "Username phải có ít nhất 4 ký tự", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1010, "Password phải có ít nhất 8 ký tự", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1011, "Định dạng Email không hợp lệ", HttpStatus.BAD_REQUEST),
    FIELD_REQUIRED(1012, "Trường này không được để trống", HttpStatus.BAD_REQUEST),
    INVALID_COMPANY_EMAIL(1013, "Email phải có định dạng @smartosc.com", HttpStatus.BAD_REQUEST),
    ;

    int code;

    String message;
    HttpStatusCode httpStatusCode;

    public void setMessage(String message) {
        this.message = message;
    }
}
