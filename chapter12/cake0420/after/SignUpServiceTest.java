package com.cake7.database.service;

import com.cake7.database.config.AppConfig;
import com.cake7.database.config.DatabaseConfig;
import com.cake7.database.domain.Users;
import com.cake7.database.model.repository.UserRepository;
import com.cake7.database.util.Encrypt;
import com.cake7.database.util.UuidToBinary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {AppConfig.class, DatabaseConfig.class})
@WebAppConfiguration
@Transactional
@Rollback
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // JUnit이 생성자 주입 허용
public class SignUpServiceTest {

    private final UserRepository userRepository;
    private final UuidToBinary uuidToBinary;
    private final Encrypt encrypt;
    private Users testUser;

    @Autowired
    public SignUpServiceTest(UserRepository userRepository, UuidToBinary uuidToBinary, Encrypt encrypt) {
        this.userRepository = userRepository;
        this.uuidToBinary = uuidToBinary;
        this.encrypt = encrypt;
    }

    @BeforeEach
    void setUp() {
        UUID uuid = UUID.randomUUID();
        byte[] binaryUuid = uuidToBinary.uuidToBytes(uuid);
        String password = "q1w@e3r$";
        String salt = encrypt.generateSalt();
        String newPassword = encrypt.getEncrypt(password, salt);

        testUser = new Users(
                binaryUuid,
                "테스트 유저",
               "test@example.com",
                newPassword,
                salt
        );
    }

    @Test
    void existByEmail() throws Exception {
        String email = "test@example.com";

        boolean exist = userRepository.existByEmail(email);
        assertFalse(exist, "존재하지 않는 이메일은 false를 반환해야 합니다.");
    }

    @Test
    void save_and_existByEmail() throws Exception {
        String email = "test2@example.com";

        UUID uuid = UUID.randomUUID();
        byte[] binaryUuid = uuidToBinary.uuidToBytes(uuid);
        String password = "q1w@e3r$";
        String salt = encrypt.generateSalt();
        String newPassword = encrypt.getEncrypt(password, salt);

        testUser = new Users(
                binaryUuid,
                "테스트 유저",
                email,
                newPassword,
                salt
        );

        userRepository.save(testUser);
        boolean exist = userRepository.existByEmail(email);

        assertTrue(exist, "저장된 이메일은 existByEmail에서 true를 반환해야 합니다.");

    }

    @Test
    void entityToMap_valid() throws Exception {
        // when
        var map = userRepository.entityToMap(testUser);

        // then
        assertEquals(testUser.getId(), map.get("id"));
        assertEquals(testUser.getName(), map.get("name"));
        assertEquals(testUser.getEmail(), map.get("email"));
        assertEquals(testUser.getPassword(), map.get("password"));
        assertEquals(testUser.getSalt(), map.get("salt"));
    }

}
