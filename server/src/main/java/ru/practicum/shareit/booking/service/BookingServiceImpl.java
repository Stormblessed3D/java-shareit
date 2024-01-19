package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingDtoToReturn createBooking(BookingDtoReceived bookingDtoReceived, Long userId) {
        Item item = itemRepository.findById(bookingDtoReceived.getItemId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещь с id %d не найдена",
                        bookingDtoReceived.getItemId())));
        if (!item.getAvailable()) {
            throw new UnavailableItemException("Вещь недоступна для бронирования");
        }
        if (Objects.equals(userId, item.getOwner().getId())) {
            throw new EntityNotFoundException("Владелец не может бронировать свои вещи");
        }
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDtoReceived, item, booker));
        return BookingMapper.toBookingDtoToReturn(booking);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingDtoToReturn approveBooking(Long bookingId, Boolean isApproved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Бронирование с id %d не найдено", bookingId)));
        checkUser(userId);
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new EntityNotFoundException("Статус бронирования может изменять только владелец");
        }
        if (isApproved && Objects.equals(BookingStatus.APPROVED, booking.getStatus())) {
            throw new UnavailableItemException("Бронирование уже имеет статус approved");
        }
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDtoToReturn(updatedBooking);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Cacheable(cacheNames = "bookings", key = "#bookingId")
    public BookingDtoToReturn getBookingById(Long bookingId, Long userId) {
        checkUser(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Бронирование с id %d не найдено", bookingId)));
        if ((!Objects.equals(booking.getItem().getOwner().getId(), userId)) &&
                (!Objects.equals(booking.getBooker().getId(), userId))) {
            throw new EntityNotFoundException("Бронирование может просматривать автор бронирования либо владелец вещи");
        }
        return BookingMapper.toBookingDtoToReturn(booking);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<BookingDtoToReturn> getAllBookingsByUser(Long userId, BookingStatusState state, Integer from,
                                                         Integer size) {
        checkUser(userId);
        PageRequest page = PageRequest.of(from / size, size);
        switch (state) {
            case ALL:
                return BookingMapper.toBookingDtoToReturn(bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page)
                        .getContent());
            case CURRENT:
                return BookingMapper.toBookingDtoToReturn(
                        bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now(), page).getContent());
            case PAST:
                return BookingMapper.toBookingDtoToReturn(
                        bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId,
                                LocalDateTime.now(), page).getContent());
            case FUTURE:
                return BookingMapper.toBookingDtoToReturn(
                        bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(),
                                page).getContent());
            case WAITING:
                return BookingMapper.toBookingDtoToReturn(
                        bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING,
                                page).getContent());
            case REJECTED:
                return BookingMapper.toBookingDtoToReturn(
                        bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED,
                                page).getContent());
            default:
                throw new StatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<BookingDtoToReturn> getAllBookingsByOwner(Long ownerId, BookingStatusState state, Integer from,
                                                          Integer size) {
        checkUser(ownerId);
        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        switch (state) {
            case ALL:
                return BookingMapper.toBookingDtoToReturn(bookingRepository.findAllByItemOwnerIdOrderByStart(ownerId,
                        page).getContent());
            case CURRENT:
                return BookingMapper.toBookingDtoToReturn(
                        bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStart(ownerId,
                                LocalDateTime.now(), LocalDateTime.now(), page).getContent());
            case PAST:
                return BookingMapper.toBookingDtoToReturn(
                        bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStart(ownerId, LocalDateTime.now(),
                                page).getContent());
            case FUTURE:
                return BookingMapper.toBookingDtoToReturn(
                        bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStart(ownerId, LocalDateTime.now(),
                                page).getContent());
            case WAITING:
                return BookingMapper.toBookingDtoToReturn(
                        bookingRepository.findAllByItemOwnerIdAndStatusOrderByStart(ownerId, BookingStatus.WAITING,
                                page).getContent());
            case REJECTED:
                return BookingMapper.toBookingDtoToReturn(
                        bookingRepository.findAllByItemOwnerIdAndStatusOrderByStart(ownerId, BookingStatus.REJECTED,
                                page).getContent());
            default:
                throw new StatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
    }
}
