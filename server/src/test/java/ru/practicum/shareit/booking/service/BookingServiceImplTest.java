package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoReceived;
import ru.practicum.shareit.booking.dto.BookingDtoToReturn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingStatusState;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.StatusException;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Request;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;
    private BookingDtoReceived bookingDtoReceived;
    private Booking booking;
    private Item item;
    private User owner;
    private User requestor;
    private Request request;
    private User booker;

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
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        bookingDtoReceived = BookingDtoReceived.builder()
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusDays(1))
                .itemId(1L)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void createBooking_whenUserAndItemFound_thenBookingIsSaved() {
        Long bookerId = booker.getId();
        Long itemId = item.getId();
        when(itemRepository.findById(bookingDtoReceived.getItemId())).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.save(BookingMapper.toBooking(bookingDtoReceived, item, booker))).thenReturn(booking);

        BookingDtoToReturn actualBooking = bookingService.createBooking(bookingDtoReceived, bookerId);

        verify(itemRepository).findById(bookingDtoReceived.getItemId());
        verify(userRepository).findById(bookerId);
        verify(bookingRepository).save(BookingMapper.toBooking(bookingDtoReceived, item, booker));
        assertNotNull(actualBooking);
        assertThat(actualBooking.getId(), equalTo(booking.getId()));
        assertThat(actualBooking.getBooker().getId(), equalTo(bookerId));
        assertThat(actualBooking.getItem().getId(), equalTo(itemId));
    }

    @Test
    void createBooking_whenItemNotFound_thenEntityNotFoundExceptionThrown() {
        Long bookerId = booker.getId();
        Long itemId = 1000000L;
        bookingDtoReceived.setItemId(itemId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.createBooking(bookingDtoReceived, bookerId));
        verify(itemRepository).findById(anyLong());
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void createBooking_whenItemNotAvailable_thenUnavailableItemExceptionThrown() {
        Long bookerId = booker.getId();
        Long itemId = item.getId();
        item.setAvailable(false);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(UnavailableItemException.class,
                () -> bookingService.createBooking(bookingDtoReceived, bookerId));
        verify(itemRepository).findById(anyLong());
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void createBooking_whenBookerIdAndOwnerIdAreEqual_thenEntityNotFoundExceptionThrown() {
        Long bookerId = item.getOwner().getId();
        Long itemId = item.getId();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.createBooking(bookingDtoReceived, bookerId));
        verify(itemRepository).findById(anyLong());
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void createBooking_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        Long bookerId = 1000000L;
        Long itemId = item.getId();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.createBooking(bookingDtoReceived, bookerId));
        verify(itemRepository).findById(anyLong());
        verify(userRepository).findById(anyLong());
        verify(bookingRepository, never()).save(BookingMapper.toBooking(bookingDtoReceived, item, booker));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void approveBooking_whenBookingFoundAndApproved_thenBookingStatusApprovedAndBookingUpdated() {
        Long bookingId = booking.getId();
        Boolean isApproved = true;
        Long userId = owner.getId();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.save(booking)).thenReturn(booking);

        bookingService.approveBooking(bookingId, isApproved, userId);

        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();

        assertThat(savedBooking.getId(), equalTo(booking.getId()));
        assertThat(savedBooking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void approveBooking_whenBookingFoundAndNotApproved_thenBookingStatusRejectedAndBookingUpdated() {
        Long bookingId = booking.getId();
        Boolean isApproved = false;
        Long userId = owner.getId();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.save(booking)).thenReturn(booking);

        bookingService.approveBooking(bookingId, isApproved, userId);

        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();

        assertThat(savedBooking.getId(), equalTo(booking.getId()));
        assertThat(savedBooking.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void approveBooking_whenBookingNotFound_thenEntityNotFoundExceptionThrown() {
        Long bookingId = 1000000L;
        Boolean isApproved = true;
        Long userId = owner.getId();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.approveBooking(bookingId, isApproved, userId));
    }

    @Test
    void approveBooking_whenApprovedNotByOwner_thenEntityNotFoundExceptionThrown() {
        Long bookingId = booking.getId();
        Boolean isApproved = false;
        Long userId = booker.getId();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.approveBooking(bookingId, isApproved, userId));
        verify(bookingRepository).findById(anyLong());
        verify(userRepository).existsById(anyLong());
        verify(bookingRepository, never()).save(booking);
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void approveBooking_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        Long bookingId = booking.getId();
        Boolean isApproved = true;
        Long userId = 1000000L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.approveBooking(bookingId, isApproved, userId));
        verify(bookingRepository).findById(anyLong());
        verify(userRepository).existsById(anyLong());
        verify(bookingRepository, never()).save(booking);
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void approveBooking_whenBookingAlreadyApproved_thenValidationExceptionThrown() {
        Long bookingId = booking.getId();
        Boolean isApproved = true;
        Long userId = owner.getId();
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);

        UnavailableItemException exception = assertThrows(UnavailableItemException.class,
                () -> bookingService.approveBooking(bookingId, isApproved, userId));
        verify(bookingRepository).findById(anyLong());
        verify(userRepository).existsById(anyLong());
        verify(bookingRepository, never()).save(booking);
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getBookingById_whenBookingFound_thenBookingIsReturned() {
        Long bookingId = booking.getId();
        Long userId = owner.getId();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDtoToReturn actualBooking = bookingService.getBookingById(bookingId, userId);

        assertNotNull(actualBooking);
        assertThat(actualBooking.getId(), equalTo(booking.getId()));
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getBookingById_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        Long bookingId = booking.getId();
        Long userId = 1000000L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingById(bookingId, userId));
        verify(userRepository).existsById(anyLong());
        verify(bookingRepository, never()).findById(anyLong());
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getBookingById_whenBookingNotFound_thenEntityNotFoundExceptionThrown() {
        Long bookingId = 1000000L;
        Long userId = owner.getId();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingById(bookingId, userId));
        verify(userRepository).existsById(anyLong());
        verify(bookingRepository).findById(anyLong());
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getBookingById_whenUserIsNotOwner_thenEntityNotFoundExceptionThrown() {
        Long bookingId = booking.getId();
        Long userId = 3L;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingById(bookingId, userId));
        verify(userRepository).existsById(anyLong());
        verify(bookingRepository).findById(anyLong());
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getAllBookingsByUser_whenBookingStatusIsAll_thenListOfBookingsWithAllStatusReturned() {
        Long userId = booker.getId();
        BookingStatusState state = BookingStatusState.ALL;
        Integer from = 0;
        Integer size = 10;
        Page<Booking> pagedBookings = new PageImpl<>(List.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any())).thenReturn(pagedBookings);

        List<BookingDtoToReturn> actualBookings = bookingService.getAllBookingsByUser(userId, state, from, size);

        assertNotNull(actualBookings);
        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0).getId(), equalTo(booking.getId()));
        verify(userRepository).existsById(any());
        verify(bookingRepository).findAllByBookerIdOrderByStartDesc(anyLong(), any());
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getAllBookingsByUser_whenBookingStatusIsCURRENT_thenListOfBookingsWithAllStatusReturned() {
        Long userId = booker.getId();
        BookingStatusState state = BookingStatusState.CURRENT;
        Integer from = 0;
        Integer size = 10;
        Page<Booking> pagedBookings = new PageImpl<>(List.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(pagedBookings);

        List<BookingDtoToReturn> actualBookings = bookingService.getAllBookingsByUser(userId, state, from, size);

        assertNotNull(actualBookings);
        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0).getId(), equalTo(booking.getId()));
        verify(userRepository).existsById(any());
        verify(bookingRepository).findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any());
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getAllBookingsByUser_whenBookingStatusIsPAST_thenListOfBookingsWithAllStatusReturned() {
        Long userId = booker.getId();
        BookingStatusState state = BookingStatusState.PAST;
        Integer from = 0;
        Integer size = 10;
        Page<Booking> pagedBookings = new PageImpl<>(List.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(pagedBookings);

        List<BookingDtoToReturn> actualBookings = bookingService.getAllBookingsByUser(userId, state, from, size);

        assertNotNull(actualBookings);
        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0).getId(), equalTo(booking.getId()));
        verify(userRepository).existsById(any());
        verify(bookingRepository).findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getAllBookingsByUser_whenBookingStatusIsFUTURE_thenListOfBookingsWithAllStatusReturned() {
        Long userId = booker.getId();
        BookingStatusState state = BookingStatusState.FUTURE;
        Integer from = 0;
        Integer size = 10;
        Page<Booking> pagedBookings = new PageImpl<>(List.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(pagedBookings);

        List<BookingDtoToReturn> actualBookings = bookingService.getAllBookingsByUser(userId, state, from, size);

        assertNotNull(actualBookings);
        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0).getId(), equalTo(booking.getId()));
        verify(userRepository).existsById(any());
        verify(bookingRepository).findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getAllBookingsByUser_whenBookingStatusIsWAITING_thenListOfBookingsWithAllStatusReturned() {
        Long userId = booker.getId();
        BookingStatusState state = BookingStatusState.WAITING;
        Integer from = 0;
        Integer size = 10;
        Page<Booking> pagedBookings = new PageImpl<>(List.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(pagedBookings);

        List<BookingDtoToReturn> actualBookings = bookingService.getAllBookingsByUser(userId, state, from, size);

        assertNotNull(actualBookings);
        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0).getId(), equalTo(booking.getId()));
        verify(userRepository).existsById(any());
        verify(bookingRepository).findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any());
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getAllBookingsByUser_whenBookingStatusIsREJECTED_thenListOfBookingsWithAllStatusReturned() {
        Long userId = booker.getId();
        BookingStatusState state = BookingStatusState.REJECTED;
        Integer from = 0;
        Integer size = 10;
        Page<Booking> pagedBookings = new PageImpl<>(List.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(pagedBookings);

        List<BookingDtoToReturn> actualBookings = bookingService.getAllBookingsByUser(userId, state, from, size);

        assertNotNull(actualBookings);
        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0).getId(), equalTo(booking.getId()));
        verify(userRepository).existsById(any());
        verify(bookingRepository).findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any());
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getAllBookingsByUser_whenBookingStatusIsUnknown_thenStatusExceptionIsThrown() {
        Long userId = booker.getId();
        BookingStatusState state = BookingStatusState.UNSUPPORTED_STATUS;
        Integer from = 0;
        Integer size = 10;
        when(userRepository.existsById(userId)).thenReturn(true);

        assertThrows(StatusException.class,
                () -> bookingService.getAllBookingsByUser(userId, state, from, size));
        verify(userRepository).existsById(any());
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getAllBookingsByOwner_whenBookingStatusIsAll_thenListOfBookingsWithAllStatusReturned() {
        Long userId = booker.getId();
        BookingStatusState state = BookingStatusState.ALL;
        Integer from = 0;
        Integer size = 10;
        Page<Booking> pagedBookings = new PageImpl<>(List.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdOrderByStart(anyLong(), any())).thenReturn(pagedBookings);

        List<BookingDtoToReturn> actualBookings = bookingService.getAllBookingsByOwner(userId, state, from, size);

        assertNotNull(actualBookings);
        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0).getId(), equalTo(booking.getId()));
        verify(userRepository).existsById(any());
        verify(bookingRepository).findAllByItemOwnerIdOrderByStart(anyLong(), any());
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getAllBookingsByOwner_whenBookingStatusIsCURRENT_thenListOfBookingsWithAllStatusReturned() {
        Long userId = booker.getId();
        BookingStatusState state = BookingStatusState.CURRENT;
        Integer from = 0;
        Integer size = 10;
        Page<Booking> pagedBookings = new PageImpl<>(List.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStart(anyLong(), any(), any(), any()))
                .thenReturn(pagedBookings);

        List<BookingDtoToReturn> actualBookings = bookingService.getAllBookingsByOwner(userId, state, from, size);

        assertNotNull(actualBookings);
        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0).getId(), equalTo(booking.getId()));
        verify(userRepository).existsById(any());
        verify(bookingRepository).findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStart(anyLong(), any(), any(), any());
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getAllBookingsByOwner_whenBookingStatusIsPAST_thenListOfBookingsWithAllStatusReturned() {
        Long userId = booker.getId();
        BookingStatusState state = BookingStatusState.PAST;
        Integer from = 0;
        Integer size = 10;
        Page<Booking> pagedBookings = new PageImpl<>(List.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStart(anyLong(), any(), any()))
                .thenReturn(pagedBookings);

        List<BookingDtoToReturn> actualBookings = bookingService.getAllBookingsByOwner(userId, state, from, size);

        assertNotNull(actualBookings);
        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0).getId(), equalTo(booking.getId()));
        verify(userRepository).existsById(any());
        verify(bookingRepository).findAllByItemOwnerIdAndEndBeforeOrderByStart(anyLong(), any(), any());
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getAllBookingsByOwner_whenBookingStatusIsFUTURE_thenListOfBookingsWithAllStatusReturned() {
        Long userId = booker.getId();
        BookingStatusState state = BookingStatusState.FUTURE;
        Integer from = 0;
        Integer size = 10;
        Page<Booking> pagedBookings = new PageImpl<>(List.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStart(anyLong(), any(), any()))
                .thenReturn(pagedBookings);

        List<BookingDtoToReturn> actualBookings = bookingService.getAllBookingsByOwner(userId, state, from, size);

        assertNotNull(actualBookings);
        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0).getId(), equalTo(booking.getId()));
        verify(userRepository).existsById(any());
        verify(bookingRepository).findAllByItemOwnerIdAndStartAfterOrderByStart(anyLong(), any(), any());
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getAllBookingsByOwner_whenBookingStatusIsWAITING_thenListOfBookingsWithAllStatusReturned() {
        Long userId = booker.getId();
        BookingStatusState state = BookingStatusState.WAITING;
        Integer from = 0;
        Integer size = 10;
        Page<Booking> pagedBookings = new PageImpl<>(List.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStart(anyLong(), any(), any()))
                .thenReturn(pagedBookings);

        List<BookingDtoToReturn> actualBookings = bookingService.getAllBookingsByOwner(userId, state, from, size);

        assertNotNull(actualBookings);
        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0).getId(), equalTo(booking.getId()));
        verify(userRepository).existsById(any());
        verify(bookingRepository).findAllByItemOwnerIdAndStatusOrderByStart(anyLong(), any(), any());
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getAllBookingsByOwner_whenBookingStatusIsREJECTED_thenListOfBookingsWithAllStatusReturned() {
        Long userId = booker.getId();
        BookingStatusState state = BookingStatusState.REJECTED;
        Integer from = 0;
        Integer size = 10;
        Page<Booking> pagedBookings = new PageImpl<>(List.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStart(anyLong(), any(), any()))
                .thenReturn(pagedBookings);

        List<BookingDtoToReturn> actualBookings = bookingService.getAllBookingsByOwner(userId, state, from, size);

        assertNotNull(actualBookings);
        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0).getId(), equalTo(booking.getId()));
        verify(userRepository).existsById(any());
        verify(bookingRepository).findAllByItemOwnerIdAndStatusOrderByStart(anyLong(), any(), any());
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getAllBookingsByOwner_whenBookingStatusIsUnknown_thenStatusExceptionIsThrown() {
        Long userId = booker.getId();
        BookingStatusState state = BookingStatusState.UNSUPPORTED_STATUS;
        Integer from = 0;
        Integer size = 10;
        when(userRepository.existsById(userId)).thenReturn(true);

        assertThrows(StatusException.class,
                () -> bookingService.getAllBookingsByOwner(userId, state, from, size));
        verify(userRepository).existsById(any());
        verifyNoMoreInteractions(bookingRepository, userRepository);
    }
}