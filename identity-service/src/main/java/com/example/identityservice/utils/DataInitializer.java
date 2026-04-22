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

            // 1. XÓA DATA TRƯỚC (Theo thứ tự ưu tiên khóa ngoại)
            // Xóa sạch User (bao gồm cả liên kết trong bảng USER_ROLES)
            userRepository.deleteAll();
            // Xóa sạch Role (bao gồm cả liên kết trong bảng ROLE_PERMISSIONS)
            roleRepository.deleteAll();
            // Xóa sạch Permission
            permissionRepository.deleteAll();

            log.info("--- Đã xóa sạch dữ liệu cũ ---");

            if (permissionRepository.count() == 0) {
                log.info("--- Khởi tạo Permissions ---");
                initPermissions(permissionRepository);
            }

            if (roleRepository.count() == 0) {
                log.info("--- Khởi tạo Roles ---");
                initRoles(roleRepository, permissionRepository);
            }

            // Đổi từ initAdminUser sang initUsers để tạo nhiều tài khoản
            if (userRepository.count() == 0) {
                log.info("--- Khởi tạo danh sách Users mẫu ---");
                initUsers(userRepository, roleRepository);
            }
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
        // Lấy sẵn 2 Role để gán
        Role adminRole = roleRepo.findByName(RoleType.ADMIN)
                .orElseThrow(() -> new RuntimeException("Role ADMIN không tồn tại"));
        Role userRole = roleRepo.findByName(RoleType.USER)
                .orElseThrow(() -> new RuntimeException("Role USER không tồn tại"));

        String commonPassword = securityUtils.encryptPassword("Admin@1234");

        List<User> users = List.of(
                // 3 Tài khoản ADMIN
                User.builder().username("admin").password(commonPassword).email("admin@smartosc.com")
                        .status(UserStatus.ACTIVE).roles(Set.of(adminRole)).build(),
                User.builder().username("admin1").password(commonPassword).email("admin1@smartosc.com")
                        .status(UserStatus.ACTIVE).roles(Set.of(adminRole)).build(),
                User.builder().username("admin2").password(commonPassword).email("admin2@smartosc.com")
                        .status(UserStatus.ACTIVE).roles(Set.of(adminRole)).build(),

                // 2 Tài khoản USER
                User.builder().username("user1").password(commonPassword).email("user1@smartosc.com")
                        .status(UserStatus.ACTIVE).roles(Set.of(userRole)).build(),
                User.builder().username("user2").password(commonPassword).email("user2@smartosc.com")
                        .status(UserStatus.ACTIVE).roles(Set.of(userRole)).build()
        );

        userRepo.saveAll(users);
        log.info("Đã khởi tạo thành công 3 Admin và 2 User mẫu!");
    }
}