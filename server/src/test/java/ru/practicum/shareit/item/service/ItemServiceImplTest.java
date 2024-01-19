package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDtoRequest;
import ru.practicum.shareit.comment.CommentDtoResponse;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RequestRepository requestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;
    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;
    private ItemDtoRequest itemDtoRequest;
    private Booking booking;
    private Item item;
    private User owner;
    private User requestor;
    private Request request;
    private User booker;
    private Comment comment;
    private CommentDtoRequest commentDtoRequest;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@gmail.com")
                .build();

        booker = User.builder()
                .id(2L)
                .name("booker")
                .email("booker@gmail.com")
                .build();

        requestor = User.builder()
                .id(2L)
                .name("requestor")
                .email("requestor@gmail.com")
                .build();

        request = Request.builder()
                .id(1L)
                .description("description")
                .requestor(requestor)
                .created(LocalDateTime.now().plusDays(2))
                .build();

        item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        itemDtoRequest = ItemDtoRequest.builder()
                .id(1L)
                .name("itemDto_name")
                .description("itemDto_description")
                .available(true)
                .requestId(1L)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("Comment")
                .item(item)
                .author(booker)
                .created(LocalDateTime.now().plusDays(4))
                .build();

        commentDtoRequest = CommentDtoRequest.builder()
                .text("commentDtoRequest_text")
                .build();
    }

    @Test
    void getItems_whenUserIsFound_thenListOfItemsIsReturned() {
        Long userId = owner.getId();
        Integer from = 0;
        Integer size = 10;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findByOwnerId(anyLong(), any())).thenReturn(List.of(item));
        when(bookingRepository.findByItemInAndStatus(anyList(), any(), any())).thenReturn(List.of(booking));
        when(commentRepository.findByItemIn(anyList(), any())).thenReturn(List.of(comment));

        List<ItemDtoResponse> actualItems = itemService.getItems(userId, from, size);

        assertNotNull(actualItems);
        assertThat(actualItems.size(), equalTo(1));
        assertThat(actualItems.get(0).getId(), equalTo(item.getId()));
        verify(userRepository).existsById(any());
        verify(itemRepository).findByOwnerId(anyLong(), any());
        verify(bookingRepository).findByItemInAndStatus(anyList(), any(), any());
        verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository);
    }

    @Test
    void getItems_whenUserIsNotFound_thenEntityNotFoundExceptionIsThrown() {
        Long userId = owner.getId();
        Integer from = 0;
        Integer size = 10;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> itemService.getItems(userId, from, size));

        verify(userRepository).existsById(any());
        verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository);
    }

    @Test
    void getItemById_whenItemIsFoundAndUserIsOwner_thenItemIsReturned() {
        Long itemId = item.getId();
        Long userId = owner.getId();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemAndStatus(any(), any(), any())).thenReturn(List.of(booking));
        when(commentRepository.findByItem(any(Item.class))).thenReturn(List.of(comment));

        ItemDtoResponse actualItem = itemService.getItemById(itemId, userId);

        assertNotNull(actualItem);
        assertThat(actualItem.getId(), equalTo(item.getId()));
        verify(itemRepository).findById(anyLong());
        verify(bookingRepository).findByItemAndStatus(any(), any(), any());
        verify(commentRepository).findByItem(any(Item.class));
        verifyNoMoreInteractions(itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void getItemById_whenItemIsFoundAndUserIsNotOwner_thenItemIsReturned() {
        Long itemId = item.getId();
        Long userId = booker.getId();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemAndStatus(any(), any(), any())).thenReturn(List.of(booking));
        when(commentRepository.findByItem(any(Item.class))).thenReturn(List.of(comment));

        ItemDtoResponse actualItem = itemService.getItemById(itemId, userId);

        assertNotNull(actualItem);
        assertThat(actualItem.getId(), equalTo(item.getId()));
        verify(itemRepository).findById(anyLong());
        verify(bookingRepository).findByItemAndStatus(any(), any(), any());
        verify(commentRepository).findByItem(any(Item.class));
        verifyNoMoreInteractions(itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void getItemById_whenItemIsNotFound_thenItemIsReturned() {
        Long itemId = item.getId();
        Long userId = owner.getId();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getItemById(itemId, userId));
        verify(itemRepository).findById(anyLong());
        verifyNoMoreInteractions(itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void createItem_whenUserIsFoundAndRequestIdNotNull_thenItemCreatedAndReturned() {
        Long userId = owner.getId();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.save(ItemMapper.toItem(itemDtoRequest, owner, request))).thenReturn(item);

        ItemDtoResponse actualItem = itemService.createItem(itemDtoRequest, userId);

        assertNotNull(actualItem);
        assertThat(actualItem.getId(), equalTo(item.getId()));
        verify(userRepository).findById(anyLong());
        verify(requestRepository).findById(anyLong());
        verify(itemRepository).save(ItemMapper.toItem(itemDtoRequest, owner, request));
        verifyNoMoreInteractions(userRepository, requestRepository, itemRepository);
    }

    @Test
    void createItem_whenUserIsFoundAndRequestIdNull_thenItemCreatedAndReturned() {
        Long userId = owner.getId();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        itemDtoRequest.setRequestId(null);
        item.setRequest(null);
        when(itemRepository.save(ItemMapper.toItem(itemDtoRequest, owner, request))).thenReturn(item);

        ItemDtoResponse actualItem = itemService.createItem(itemDtoRequest, userId);

        assertNotNull(actualItem);
        assertThat(actualItem.getId(), equalTo(item.getId()));
        verify(userRepository).findById(anyLong());
        verify(itemRepository).save(ItemMapper.toItem(itemDtoRequest, owner, request));
        verifyNoMoreInteractions(userRepository, itemRepository);
    }

    @Test
    void createItem_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        Long userId = owner.getId();
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.createItem(itemDtoRequest, userId));
        verify(userRepository).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createItem_whenUserIsFoundButRequestIsNotFound_thenEntityNotFoundExceptionThrown() {
        Long userId = owner.getId();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.createItem(itemDtoRequest, userId));
        verify(userRepository).findById(anyLong());
        verify(requestRepository).findById(anyLong());
        verifyNoMoreInteractions(userRepository, requestRepository);
    }

    @Test
    void updateItem_WhenUserIsFoundAndItemIsFound_thenUpdateItemWithItemDtoNonNullFields() {
        Long itemId = item.getId();
        Long userId = owner.getId();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(commentRepository.findByItem(any(Item.class))).thenReturn(List.of(comment));

        ItemDtoResponse actualItem = itemService.updateItem(itemDtoRequest, itemId, userId);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem  = itemArgumentCaptor.getValue();

        assertThat(savedItem.getId(), equalTo(itemDtoRequest.getId()));
        assertThat(savedItem.getName(), equalTo("itemDto_name"));
        assertThat(savedItem.getDescription(), equalTo("itemDto_description"));
        assertThat(savedItem.getAvailable(), equalTo(true));
    }

    @Test
    void updateItem_whenItemNotFound_thenEntityNotFoundExceptionThrown() {
        Long itemId = 1000000L;
        Long userId = owner.getId();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(itemDtoRequest, itemId, userId));
        verify(itemRepository).findById(anyLong());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void updateItem_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        Long itemId = item.getId();
        Long userId = 1000000L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(itemDtoRequest, itemId, userId));
        verify(itemRepository).findById(anyLong());
        verify(userRepository).existsById(anyLong());
        verifyNoMoreInteractions(itemRepository, userRepository);
    }

    @Test
    void updateItem_whenUserIsNotOwner_thenEntityNotFoundExceptionThrown() {
        Long itemId = item.getId();
        Long userId = booker.getId();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(userId)).thenReturn(true);

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(itemDtoRequest, itemId, userId));
        verify(itemRepository).findById(anyLong());
        verify(userRepository).existsById(anyLong());
        verifyNoMoreInteractions(itemRepository, userRepository);
    }

    @Test
    void updateItem_WhenItemDtoNameIsNull_thenUpdateItemWithoutUpdatingItemName() {
        Long itemId = item.getId();
        Long userId = owner.getId();
        itemDtoRequest.setName(null);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(commentRepository.findByItem(any(Item.class))).thenReturn(List.of(comment));

        ItemDtoResponse actualItem = itemService.updateItem(itemDtoRequest, itemId, userId);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem  = itemArgumentCaptor.getValue();

        assertThat(savedItem.getId(), equalTo(itemDtoRequest.getId()));
        assertThat(savedItem.getName(), equalTo("itemName"));
        assertThat(savedItem.getDescription(), equalTo("itemDto_description"));
        assertThat(savedItem.getAvailable(), equalTo(true));
    }

    @Test
    void updateItem_WhenItemDtoDescriptionIsNull_thenUpdateItemWithoutUpdatingItemDescription() {
        Long itemId = item.getId();
        Long userId = owner.getId();
        itemDtoRequest.setDescription(null);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(commentRepository.findByItem(any(Item.class))).thenReturn(List.of(comment));

        ItemDtoResponse actualItem = itemService.updateItem(itemDtoRequest, itemId, userId);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem  = itemArgumentCaptor.getValue();

        assertThat(savedItem.getId(), equalTo(itemDtoRequest.getId()));
        assertThat(savedItem.getName(), equalTo("itemDto_name"));
        assertThat(savedItem.getDescription(), equalTo("itemDescription"));
        assertThat(savedItem.getAvailable(), equalTo(true));
    }

    @Test
    void deleteItem_whenItemIsFound_thenItemIsDeleted() {
        Long itemId = item.getId();
        Long userId = owner.getId();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        itemService.deleteItem(itemId, userId);

        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    void deleteItem_whenItemNotFound_thenEntityNotFoundExceptionThrown() {
        Long itemId = 1000000L;
        Long userId = owner.getId();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.deleteItem(itemId, userId));
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void deleteItem_whenUserIsNotOwner_thenEntityNotFoundExceptionThrown() {
        Long itemId = item.getId();
        Long userId = booker.getId();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class, () -> itemService.deleteItem(itemId, userId));
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void search_whenTextIsNotBlank_thenListOfItemDtoIsReturned() {
        String text = "text";
        Integer from = 0;
        Integer size = 10;
        when(itemRepository.findByNameOrDescriptionContainingIgnoreCase(any(String.class), any(Pageable.class)))
                .thenReturn(List.of(item));
        when(bookingRepository.findByItemInAndStatus(anyList(), any(BookingStatus.class), any(Sort.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.findByItemIn(anyList(), any(Sort.class)))
                .thenReturn(List.of(comment));

        List<ItemDtoResponse> actualItems = itemService.search(text, from, size);

        assertNotNull(actualItems);
        assertThat(actualItems.size(), equalTo(1));
        assertThat(actualItems.get(0).getId(), equalTo(item.getId()));
        verify(itemRepository).findByNameOrDescriptionContainingIgnoreCase(any(String.class), any(Pageable.class));
        verify(bookingRepository).findByItemInAndStatus(anyList(), any(BookingStatus.class), any(Sort.class));
        verify(commentRepository).findByItemIn(anyList(), any(Sort.class));
        verifyNoMoreInteractions(itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void search_whenTextIsBlank_thenEmptyListIsReturned() {
        String text = "";
        Integer from = 0;
        Integer size = 10;

        List<ItemDtoResponse> actualItems = itemService.search(text, from, size);

        assertNotNull(actualItems);
        assertThat(actualItems.size(), equalTo(0));
        verifyNoMoreInteractions(itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void createComment_whenAuthorOfCommentIsBooker_thenCommentIsReturned() {
        Long itemId = item.getId();
        Long userId = booker.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.countByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(1L);
        when(commentRepository.save(CommentMapper.toComment(commentDtoRequest, item, booker))).thenReturn(comment);

        CommentDtoResponse actualComment = itemService.createComment(commentDtoRequest, itemId, userId);

        assertNotNull(actualComment);
        assertThat(actualComment.getId(), equalTo(comment.getId()));
        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
        verify(bookingRepository).countByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class));
        verify(commentRepository).save(CommentMapper.toComment(commentDtoRequest, item, booker));
        verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository, commentRepository);

        verify(commentRepository).save(commentArgumentCaptor.capture());
        Comment savedComment  = commentArgumentCaptor.getValue();
        assertThat(savedComment.getText(), equalTo("commentDtoRequest_text"));
    }

    @Test
    void createComment_whenAuthorOfCommentIsNotBooker_thenUnavailableItemExceptionIsThrown() {
        Long itemId = item.getId();
        Long userId = owner.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.countByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(0L);

        assertThrows(UnavailableItemException.class, () -> itemService.createComment(commentDtoRequest, itemId, userId));
        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
        verify(bookingRepository).countByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class));
        verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void createComment_whenAuthorNotFound_thenEntityNotFoundExceptionIsThrown() {
        Long itemId = item.getId();
        Long userId = owner.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.createComment(commentDtoRequest, itemId, userId));
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void createComment_whenItemNotFound_thenEntityNotFoundExceptionIsThrown() {
        Long itemId = item.getId();
        Long userId = owner.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.createComment(commentDtoRequest, itemId, userId));
        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
        verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository, commentRepository);
    }
}