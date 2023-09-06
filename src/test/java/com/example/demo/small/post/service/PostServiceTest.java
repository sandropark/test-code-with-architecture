package com.example.demo.small.post.service;

import com.example.demo.mock.infrastructure.FakePostRepository;
import com.example.demo.mock.infrastructure.FakeUserRepository;
import com.example.demo.post.controller.port.PostService;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.service.PostServiceImpl;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.service.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostServiceTest {

    private static final long NOW = 10L;
    PostService postService;
    PostRepository postRepository;
    UserRepository userRepository;
    User user;
    Post post;

    @BeforeEach
    void setUp() {
        postRepository = new FakePostRepository();
        userRepository = new FakeUserRepository();
        postService = initPostService();
        user = getUser();
        post = getPost();
        initData();
    }

    @Test
    void getById() throws Exception {
        // When
        Post foundPost = postService.getById(post.getId());

        // Then
        assertThat(foundPost).isEqualTo(post);
    }

    @Test
    void create() throws Exception {
        // Given
        PostCreate postCreate = PostCreate.builder()
                .content("content!!")
                .writerId(user.getId())
                .build();

        // When
        Post post = postService.create(postCreate);

        // Then
        assertThat(post.getId()).isNotNull();
        assertThat(post.getContent()).isEqualTo(postCreate.getContent());
        assertThat(post.getWriter()).isEqualTo(user);
        assertThat(post.getCreatedAt()).isEqualTo(NOW);
    }

    @Test
    void update() throws Exception {
        // Given
        PostUpdate postUpdate = PostUpdate.builder()
                .content("Update content!!!")
                .build();
        Long postId = post.getId();

        // When
        postService.update(postId, postUpdate);

        // Then
        Post updated = postService.getById(postId);
        assertThat(updated.getModifiedAt()).isEqualTo(NOW);
        assertThat(updated.getContent()).isEqualTo(postUpdate.getContent());
    }

    private PostService initPostService() {
        return PostServiceImpl.builder()
                .postRepository(postRepository)
                .userRepository(userRepository)
                .clockHolder(() -> NOW)
                .build();
    }

    private void initData() {
        userRepository.save(user);
        postRepository.save(post);
    }

    private User getUser() {
        User temp = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("Sandro")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("1234")
                .lastLoginAt(1L)
                .build();
        assertThat(temp.getStatus()).isEqualTo(UserStatus.ACTIVE);
        validateNotNullFields(temp);
        return temp;
    }

    private Post getPost() {
        Post temp = Post.builder()
                .id(1L)
                .content("content")
                .createdAt(1L)
                .modifiedAt(1L)
                .writer(user)
                .build();
        validateNotNullFields(temp);
        return temp;
    }

    private static void validateNotNullFields(Post post) {
        assertThat(post.getId()).isNotNull();
        assertThat(post.getContent()).isNotNull();
        assertThat(post.getCreatedAt()).isNotNull();
        assertThat(post.getModifiedAt()).isNotNull();
        assertThat(post.getWriter()).isNotNull();
    }

    private static void validateNotNullFields(User temp) {
        assertThat(temp.getId()).isNotNull();
        assertThat(temp.getEmail()).isNotNull();
        assertThat(temp.getNickname()).isNotNull();
        assertThat(temp.getAddress()).isNotNull();
        assertThat(temp.getCertificationCode()).isNotNull();
        assertThat(temp.getLastLoginAt()).isNotNull();
    }

}