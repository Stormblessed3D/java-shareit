package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDtoPost;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RequestDtoResponse createRequest(RequestDtoPost requestDtoPost, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Request request = requestRepository.save(RequestMapper.toRequest(requestDtoPost, user, LocalDateTime.now()));
        return RequestMapper.toRequestDtoResponse(request);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<RequestDtoResponse> getAllRequests(Long userId) {
        checkUser(userId);
        List<Request> requests = requestRepository.findByRequestorId(userId, Sort.by(Sort.Direction.ASC,"created"));
        Map<Request, List<Item>> items = itemRepository.findByRequestIn(requests, Sort.by(Sort.Direction.ASC,"id")).stream()
                .collect(groupingBy(Item::getRequest));
        return requests.stream()
                .map(r -> RequestMapper.toRequestDtoResponse(r,
                        items.getOrDefault(r, Collections.emptyList())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Cacheable(cacheNames = "requests", key = "#requestId")
    public RequestDtoResponse getRequestById(Long requestId, Long userId) {
        checkUser(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Запрос с id %d не найден", requestId)));
        List<Item> items = itemRepository.findByRequest(request, Sort.by(Sort.Direction.ASC, "id"));
        return RequestMapper.toRequestDtoResponse(request, items);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<RequestDtoResponse> getAllByPages(Integer from, Integer size, Long userId) {
        checkUser(userId);
        int page = from / size;
        Page<Request> requestsByPage = requestRepository.findAllByRequestorIdNot(userId, PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "created")));
        List<Request> requests = new ArrayList<>();
        if (requestsByPage != null && requestsByPage.hasContent()) {
            requests = requestsByPage.getContent();
        }
        Map<Request, List<Item>> items = itemRepository.findByRequestIn(requests, Sort.by(Sort.Direction.ASC,"id")).stream()
                .collect(groupingBy(Item::getRequest));
        return requests.stream()
                .map(r -> RequestMapper.toRequestDtoResponse(r,
                        items.getOrDefault(r, Collections.emptyList())))
                .collect(Collectors.toList());
    }

    private void checkUser(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", ownerId));
        }
    }
}
