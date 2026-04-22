package com.example.identityservice.exception;

import com.example.identityservice.dto.response.APIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Bắt tất cả các lỗi không xác định (Exception.class)
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<APIResponse> handlingUncategorizedException(Exception exception) {
        log.error("Unhandled Exception: ", exception);

        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(APIResponse.builder()
                        .status(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }


    // 2. Bắt lỗi nghiệp vụ do mình tự throw (AppException.class)
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<APIResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(APIResponse.builder()
                        .status(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    // 3. Bắt lỗi phân quyền từ @PreAuthorize (AccessDeniedException.class)
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<APIResponse> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        log.error("Access denied for: ", exception);

        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(APIResponse.builder()
                        .status(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    // 4. Bắt lỗi Validation (@Valid trên DTO)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<APIResponse> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_INPUT;

        // Thử lấy ErrorCode từ message của annotation (nếu bạn đặt message là tên ErrorCode)
        try {
            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
            // keep default INVALID_INPUT
        }

        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(APIResponse.builder()
                        .status(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

}
