package com.example.demo.user.infrastructure;

import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Sql("/sql/user-repository-test-data.sql")
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserJpaRepository userRepository;

    @Nested
    class FindByIdAndStatus {
        @Test
        void found() throws Exception {
            // When
            Optional<UserEntity> result = userRepository.findByIdAndStatus(1, UserStatus.ACTIVE);

            // Then
            assertThat(result).isPresent();
        }

        @Test
        void notFound() throws Exception {
            // When
            Optional<UserEntity> result = userRepository.findByIdAndStatus(1, UserStatus.PENDING);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class FindByEmailAndStatus {
        @Test
        void found() throws Exception {
            // When
            Optional<UserEntity> result = userRepository.findByEmailAndStatus("active@gmail.com", UserStatus.ACTIVE);

            // Then
            assertThat(result).isPresent();
        }

        @Test
        void notFound() throws Exception {
            // When
            Optional<UserEntity> result = userRepository.findByEmailAndStatus("active@gmail.com", UserStatus.PENDING);

            // Then
            assertThat(result).isEmpty();
        }
    }

}