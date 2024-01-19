package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDtoPost;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequestServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestRepository requestRepository;
    @InjectMocks
    private RequestServiceImpl requestService;
    private Item item;
    private User owner;
    private User requestor;
    private Request request;
    private RequestDtoPost requestDtoPost;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@gmail.com")
                .build();

        requestor = User.builder()
                .id(3L)
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

        requestDtoPost = RequestDtoPost.builder()
                .description("requestDtoPost_description")
                .build();
    }

    @Test
    void createRequest_whenUserIsFound_thenRequestIsCreatedAndReturned() {
        Long userId = requestor.getId();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(requestRepository.save(RequestMapper.toRequest(requestDtoPost, owner, LocalDateTime.now())))
                .thenReturn(request);

        RequestDtoResponse actualRequest = requestService.createRequest(requestDtoPost, userId);

        assertNotNull(actualRequest);
        assertThat(actualRequest.getId(), equalTo(request.getId()));
        verify(userRepository).findById(anyLong());
        verify(requestRepository).save(RequestMapper.toRequest(requestDtoPost, owner, LocalDateTime.now()));
        verifyNoMoreInteractions(userRepository, requestRepository);
    }

    @Test
    void createRequest_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        Long userId = requestor.getId();
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> requestService.createRequest(requestDtoPost, userId));
        verify(userRepository).findById(anyLong());
        verifyNoMoreInteractions(userRepository, requestRepository);
    }

    @Test
    void getAllRequests_whenUserFound_thenListOfRequestDtoReturned() {
        Long userId = requestor.getId();
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findByRequestorId(anyLong(), any(Sort.class))).thenReturn(List.of(request));
        when(itemRepository.findByRequestIn(anyList(), any(Sort.class))).thenReturn(List.of(item));

        List<RequestDtoResponse> actualRequests = requestService.getAllRequests(userId);

        assertNotNull(actualRequests);
        assertThat(actualRequests.size(), equalTo(1));
        assertThat(actualRequests.get(0).getId(), equalTo(request.getId()));
        verify(userRepository).existsById(anyLong());
        verify(requestRepository).findByRequestorId(anyLong(), any(Sort.class));
        verify(itemRepository).findByRequestIn(anyList(), any(Sort.class));
        verifyNoMoreInteractions(userRepository, requestRepository, itemRepository);
    }

    @Test
    void getAllRequests_whenUserIsNotFound_thenEntityNotFoundExceptionIsThrown() {
        Long userId = requestor.getId();
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> requestService.getAllRequests(userId));

        verify(userRepository).existsById(anyLong());
        verifyNoMoreInteractions(userRepository, requestRepository, itemRepository);
    }

    @Test
    void getRequestById_whenUserFoundAndRequestFound_thenRequestDtoIsReturned() {
        Long userId = requestor.getId();
        Long requestId = request.getId();
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.findByRequest(any(Request.class), any(Sort.class))).thenReturn(List.of(item));

        RequestDtoResponse actualRequest = requestService.getRequestById(requestId, userId);

        assertNotNull(actualRequest);
        assertThat(actualRequest.getId(), equalTo(request.getId()));
        verify(userRepository).existsById(anyLong());
        verify(requestRepository).findById(anyLong());
        verify(itemRepository).findByRequest(any(Request.class), any(Sort.class));
        verifyNoMoreInteractions(userRepository, requestRepository, itemRepository);
    }

    @Test
    void getRequestById_whenUserIsNotFound_thenEntityNotFoundExceptionIsThrown() {
        Long userId = requestor.getId();
        Long requestId = request.getId();
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> requestService.getRequestById(requestId, userId));

        verify(userRepository).existsById(anyLong());
        verify(requestRepository).findById(anyLong());
        verifyNoMoreInteractions(userRepository, requestRepository, itemRepository);
    }

    @Test
    void getRequestById_whenUserIsFoundButRequestIsNotFound_thenEntityNotFoundExceptionIsThrown() {
        Long userId = requestor.getId();
        Long requestId = request.getId();
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> requestService.getRequestById(requestId, userId));

        verify(userRepository).existsById(anyLong());
        verifyNoMoreInteractions(userRepository, requestRepository, itemRepository);
    }

    @Test
    void getAllByPages_whenUserFound_thenListOfRequestDtoResponseIsReturned() {
        Long userId = requestor.getId();
        Integer from = 0;
        Integer size = 10;
        Page<Request> pagedRequests = new PageImpl<>(List.of(request));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findAllByRequestorIdNot(anyLong(), any(Pageable.class))).thenReturn(pagedRequests);
        when(itemRepository.findByRequestIn(anyList(), any(Sort.class))).thenReturn(List.of(item));

        List<RequestDtoResponse> actualRequests = requestService.getAllByPages(from, size, userId);

        assertNotNull(actualRequests);
        assertThat(actualRequests.size(), equalTo(1));
        assertThat(actualRequests.get(0).getId(), equalTo(request.getId()));
        verify(userRepository).existsById(anyLong());
        verify(requestRepository).findAllByRequestorIdNot(anyLong(), any(Pageable.class));
        verify(itemRepository).findByRequestIn(anyList(), any(Sort.class));
        verifyNoMoreInteractions(userRepository, requestRepository, itemRepository);
    }

    @Test
    void getAllByPages_whenUserFoundAndListOfRequestsIsNull_thenEmptyListOfRequestDtoResponseIsReturned() {
        Long userId = requestor.getId();
        Integer from = 0;
        Integer size = 10;
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findAllByRequestorIdNot(anyLong(), any(Pageable.class))).thenReturn(null);
        when(itemRepository.findByRequestIn(anyList(), any(Sort.class))).thenReturn(List.of());

        List<RequestDtoResponse> actualRequests = requestService.getAllByPages(from, size, userId);

        assertNotNull(actualRequests);
        assertThat(actualRequests.size(), equalTo(0));
        verify(userRepository).existsById(anyLong());
        verify(requestRepository).findAllByRequestorIdNot(anyLong(), any(Pageable.class));
        verify(itemRepository).findByRequestIn(anyList(), any(Sort.class));
        verifyNoMoreInteractions(userRepository, requestRepository, itemRepository);
    }

    @Test
    void getAllByPages_whenUserNotFound_thenEntityNotFoundExceptionIsThrown() {
        Long userId = requestor.getId();
        Integer from = 0;
        Integer size = 10;
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> requestService.getAllByPages(from, size, userId));

        verify(userRepository).existsById(anyLong());
        verifyNoMoreInteractions(userRepository, requestRepository, itemRepository);
    }
}