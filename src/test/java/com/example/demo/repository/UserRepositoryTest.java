package com.example.demo.repository;

import com.example.demo.TestContext;
import com.example.demo.model.UserStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = TestContext.class)
@DataJpaTest
public class UserRepositoryTest {
    public static final long ID = 1L;
    public static final String MAIL = "birdob@gmail.com";
    @Autowired
    UserRepository userRepository;

    @Nested
    class FindByIdAndStatus {
        @Test
        void found() throws Exception {
            // When
            Optional<UserEntity> result = userRepository.findByIdAndStatus(ID, UserStatus.ACTIVE);

            // Then
            assertThat(result).isPresent();
        }

        @Test
        void notFound() throws Exception {
            // When
            Optional<UserEntity> result = userRepository.findByIdAndStatus(ID, UserStatus.PENDING);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class FindByEmailAndStatus {
        @Test
        void found() throws Exception {
            // When
            Optional<UserEntity> result = userRepository.findByEmailAndStatus(MAIL, UserStatus.ACTIVE);

            // Then
            assertThat(result).isPresent();
        }

        @Test
        void notFound() throws Exception {
            // When
            Optional<UserEntity> result = userRepository.findByEmailAndStatus(MAIL, UserStatus.PENDING);

            // Then
            assertThat(result).isEmpty();
        }
    }

}