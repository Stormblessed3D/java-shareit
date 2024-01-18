package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private Item item1;
    private Item item2;
    private User owner1;
    private User owner2;

    @BeforeEach
    void setUp() {
        owner1 = User.builder()
                .id(1L)
                .name("owner1")
                .email("owner1@gmail.com")
                .build();

        owner2 = User.builder()
                .id(2L)
                .name("owner2")
                .email("owner2@gmail.com")
                .build();

        item1 = Item.builder()
                .id(1L)
                .name("item1_name")
                .description("item1_description")
                .available(false)
                .owner(owner1)
                .request(null)
                .build();

        item2 = Item.builder()
                .id(2L)
                .name("item2_name")
                .description("item2_description")
                .available(true)
                .owner(owner2)
                .request(null)
                .build();

        userRepository.save(owner1);
        userRepository.save(owner2);
        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @Test
    void findByNameOrDescriptionContainingIgnoreCase_whenInvoked_thenReturnListOfAvailableItemsWithNameOrDescriptionContaingText() {
        String text = "nAMe";

        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCase(text,
                PageRequest.ofSize(10));

        assertNotNull(items);
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(item2.getId()));
        assertThat(items.get(0).getName(), equalTo(item2.getName()));
        assertThat(items.get(0).getDescription(), equalTo(item2.getDescription()));
        assertThat(items.get(0).getAvailable(), equalTo(true));
    }


}