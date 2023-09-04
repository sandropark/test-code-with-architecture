package com.example.demo.repository;

import com.example.demo.TestContext;
import com.example.demo.model.UserStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static com.example.demo.TestContext.ACTIVE_ID;
import static com.example.demo.TestContext.ACTIVE_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = TestContext.class)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Nested
    class FindByIdAndStatus {
        @Test
        void found() throws Exception {
            // When
            Optional<UserEntity> result = userRepository.findByIdAndStatus(ACTIVE_ID, UserStatus.ACTIVE);

            // Then
            assertThat(result).isPresent();
        }

        @Test
        void notFound() throws Exception {
            // When
            Optional<UserEntity> result = userRepository.findByIdAndStatus(ACTIVE_ID, UserStatus.PENDING);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class FindByEmailAndStatus {
        @Test
        void found() throws Exception {
            // When
            Optional<UserEntity> result = userRepository.findByEmailAndStatus(ACTIVE_EMAIL, UserStatus.ACTIVE);

            // Then
            assertThat(result).isPresent();
        }

        @Test
        void notFound() throws Exception {
            // When
            Optional<UserEntity> result = userRepository.findByEmailAndStatus(ACTIVE_EMAIL, UserStatus.PENDING);

            // Then
            assertThat(result).isEmpty();
        }
    }

}