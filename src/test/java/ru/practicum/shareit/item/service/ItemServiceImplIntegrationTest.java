package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Request;
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
class ItemServiceImplIntegrationTest {
    private final ItemService itemService;
    private final EntityManager em;
    private Request request;
    private User user;
    private User owner;
    private Item item;
    ItemDtoRequest itemDtoRequest;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .name("owner")
                .email("owner@gmail.com")
                .build();

        user = User.builder()
                .name("user1")
                .email("user1@gmail.com")
                .build();

        request = Request.builder()
                .description("description")
                .requestor(user)
                .created(LocalDateTime.now().plusDays(2))
                .build();

        item = Item.builder()
                .name("item1_name")
                .description("item1_description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        itemDtoRequest = ItemDtoRequest.builder()
                .name("item1_name")
                .description("item1_description")
                .available(true)
                .requestId(1L)
                .build();
    }

    @Test
    void createItem() {
        Long ownerId = 1L;
        em.persist(owner);
        em.persist(user);
        em.persist(request);
        em.flush();

        ItemDtoResponse actualItem = itemService.createItem(itemDtoRequest, ownerId);

        assertNotNull(actualItem);
        assertThat(actualItem.getId(), equalTo(1L));
        assertThat(actualItem.getName(), equalTo(item.getName()));
        assertThat(actualItem.getDescription(), equalTo(item.getDescription()));
        assertThat(actualItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(actualItem.getRequestId(), equalTo(itemDtoRequest.getRequestId()));
    }

    @Test
    void getItems() {
        Long ownerId = 1L;
        Integer from = 0;
        Integer size = 10;
        em.persist(owner);
        em.persist(user);
        em.persist(request);
        em.persist(item);
        em.flush();
        ItemDtoResponse expectedItem = ItemMapper.toItemDtoWithoutBookings(item, List.of(new Comment(1L, "comment",
                item, user, LocalDateTime.now())));

        List<ItemDtoResponse> actualItems = itemService.getItems(ownerId, from, size);

        assertNotNull(actualItems);
        assertThat(actualItems.size(), equalTo(1));
        assertThat(actualItems.get(0).getId(), equalTo(1L));
        assertThat(actualItems.get(0).getName(), equalTo(expectedItem.getName()));
        assertThat(actualItems.get(0).getDescription(), equalTo(expectedItem.getDescription()));
        assertThat(actualItems.get(0).getAvailable(), equalTo(expectedItem.getAvailable()));
        assertThat(actualItems.get(0).getRequestId(), equalTo(expectedItem.getRequestId()));
        assertThat(actualItems.get(0).getComments(), notNullValue());
    }
}