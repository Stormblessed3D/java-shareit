package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithRequestId;
import ru.practicum.shareit.request.dto.RequestDtoPost;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(properties = {"db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequestServiceImplIntegrationTest {
    private final RequestService requestService;
    private final EntityManager em;
    private Request request;
    private User user;
    private RequestDtoResponse requestDtoResponse;
    private RequestDtoPost requestDtoPost;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("user1")
                .email("user1@gmail.com")
                .build();

        request = Request.builder()
                .description("description")
                .requestor(user)
                .created(LocalDateTime.now().plusDays(2))
                .build();

        requestDtoResponse = RequestDtoResponse.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now().plusDays(2))
                .items(List.of(new ItemDtoResponseWithRequestId()))
                .build();

        requestDtoPost = RequestDtoPost.builder()
                .description("description")
                .build();
    }

    @Test
    void createUser() {
        Long userId = 1L;
        em.persist(user);
        em.flush();

        RequestDtoResponse actualRequest = requestService.createRequest(requestDtoPost, userId);

        assertNotNull(actualRequest);
        assertThat(actualRequest.getId(), equalTo(1L));
        assertThat(actualRequest.getDescription(), equalTo("description"));
        assertThat(actualRequest.getCreated(), notNullValue());
    }

    @Test
    void getAllRequests() {
        Long userId = 1L;
        em.persist(user);
        em.persist(request);
        em.flush();
        RequestDtoResponse expectedRequest = RequestMapper.toRequestDtoResponse(request);

        List<RequestDtoResponse> actualRequests = requestService.getAllRequests(userId);

        assertNotNull(actualRequests);
        assertThat(actualRequests.size(), equalTo(1));
        assertThat(actualRequests.get(0).getId(), equalTo(1L));
        assertThat(actualRequests.get(0).getDescription(), equalTo(expectedRequest.getDescription()));
        assertThat(actualRequests.get(0).getCreated(), notNullValue());
        assertThat(actualRequests.get(0).getItems(), notNullValue());
    }
}