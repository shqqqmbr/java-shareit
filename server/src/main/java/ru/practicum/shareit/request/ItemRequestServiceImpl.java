package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, int ownerId) {
        ItemRequest request = itemRequestMapper.toEntity(itemRequestDto);
        User requestor = userRepository.findById(ownerId).get();
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        ItemRequest savedRequest = itemRequestRepository.save(request);
        return itemRequestMapper.toDto(savedRequest);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Integer ownerId) {
        if (ownerId == null) {
            return itemRequestRepository.findAll().stream()
                    .map(itemRequestMapper::toDto)
                    .collect(Collectors.toList());
        }
        userRepository.findById(ownerId);
        return itemRequestRepository.findByRequestorId(ownerId).stream()
                .map(itemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequest(int requestId) {
        return itemRequestMapper.toDto(itemRequestRepository.findById(requestId).get());
    }
}
