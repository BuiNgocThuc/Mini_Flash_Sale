package com.example.identityservice.utils;

import com.example.identityservice.entity.Permission;
import com.example.identityservice.entity.Role;
import com.example.identityservice.entity.User;
import com.example.identityservice.enums.RoleType;
import com.example.identityservice.enums.UserStatus;
import com.example.identityservice.repository.PermissionRepository;
import com.example.identityservice.repository.RoleRepository;
import com.example.identityservice.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//@Configuration
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataInitializer {
    SecurityUtils securityUtils;

//    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository) {
        return args -> {
            log.info("--- Bắt đầu quá trình Reset và Migrate dữ liệu ---");

            // Xóa sạch theo thứ tự để tránh lỗi khóa ngoại
            userRepository.deleteAll();
            roleRepository.deleteAll();
            permissionRepository.deleteAll();

            log.info("--- Đã xóa sạch dữ liệu cũ ---");

            if (permissionRepository.count() == 0) {
                initPermissions(permissionRepository);
            }

            if (roleRepository.count() == 0) {
                initRoles(roleRepository, permissionRepository);
            }

            if (userRepository.count() == 0) {
                initUsers(userRepository, roleRepository);
            }
            log.info("--- Quá trình khởi tạo dữ liệu thành công! ---");
        };
    }

    private void initPermissions(PermissionRepository repo) {
        List<Permission> permissions = List.of(
                Permission.builder().name("CREATE_DATA").description("Quyền tạo mới").build(),
                Permission.builder().name("UPDATE_DATA").description("Quyền chỉnh sửa").build(),
                Permission.builder().name("VIEW_DATA").description("Quyền xem").build(),
                Permission.builder().name("DELETE_DATA").description("Quyền xóa").build(),
                Permission.builder().name("APPROVE_DATA").description("Quyền phê duyệt").build()
        );
        repo.saveAll(permissions);
    }

    private void initRoles(RoleRepository roleRepo, PermissionRepository permRepo) {
        var allPermissions = new HashSet<>(permRepo.findAll());
        var viewPermission = permRepo.findByName("VIEW_DATA").stream().collect(Collectors.toSet());

        Role adminRole = Role.builder().name(RoleType.ADMIN).permissions(allPermissions).build();
        Role userRole = Role.builder().name(RoleType.USER).permissions(viewPermission).build();

        roleRepo.saveAll(List.of(adminRole, userRole));
    }

    private void initUsers(UserRepository userRepo, RoleRepository roleRepo) {
        Role adminRole = roleRepo.findByName(RoleType.ADMIN)
                .orElseThrow(() -> new RuntimeException("Role ADMIN không tồn tại"));
        Role userRole = roleRepo.findByName(RoleType.USER)
                .orElseThrow(() -> new RuntimeException("Role USER không tồn tại"));

        String commonPassword = securityUtils.encryptPassword("Admin@1234");
        List<User> users = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            String username;
            Set<Role> roles;

            // Phân bổ 5 ADMIN và 15 USER
            if (i <= 5) {
                username = "admin" + i;
                roles = Set.of(adminRole);
            } else {
                username = "user" + (i - 5);
                roles = Set.of(userRole);
            }

            // --- QUAN TRỌNG: XỬ LÝ DOB ĐỂ TRÁNH ORA-01400 ---
            // Bước 1: Luôn gán một giá trị mặc định để không bị NULL
            LocalDate dateOfBirth = LocalDate.of(1995, 1, 1);

            // Bước 2: Logic cài bẫy tháng 2 cho đúng 10 Khách hàng (USER)
            if (i > 5 && i <= 15) {
                // i từ 6 đến 15 tương ứng với user1 đến user10
                if (username.equals("user1")) {
                    // "Ông kẹ" sinh ngày nhuận để demo bug
                    dateOfBirth = LocalDate.of(2024, 2, 29);
                    log.info(">>> Đã cài bẫy Leap Year cho: {} (2024-02-29)", username);
                } else {
                    // 9 Users khác (user2 - user10) sinh rải rác trong tháng 2
                    dateOfBirth = LocalDate.of(1998, 2, (i % 28) + 1);
                }
            } else {
                // Các Admin và 5 Users còn lại sinh vào các tháng khác
                dateOfBirth = LocalDate.of(2000, (i % 11) + 1, 10);
            }

            users.add(User.builder()
                    .username(username)
                    .password(commonPassword)
                    .email(username + "@gmail.com")
                    .status(UserStatus.ACTIVE)
                    .roles(roles)
                    .dob(dateOfBirth) // Đảm bảo luôn có giá trị
                    .build());
        }

        userRepo.saveAll(users);
        log.info("--- Đã khởi tạo 20 Users mẫu (5 Admin, 15 User). 10 Users sinh tháng 2. ---");
    }
}