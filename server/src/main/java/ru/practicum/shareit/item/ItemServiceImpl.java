package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto addItem(ItemDto itemDto, int ownerId) {
        Item item = itemMapper.toEntity(itemDto);
        User owner = userRepository.findById(ownerId).get();
        item.setOwner(owner);
        Integer requestId = itemDto.getRequestId();
        if (requestId != null) {
            ItemRequest request = itemRequestRepository.findById(requestId).get();
            item.setRequest(request);
            request.getItems().add(item);
            itemRequestRepository.save(request);
        }
        Item savedItem = itemRepository.save(item);
        return itemMapper.toDto(savedItem);
    }

    @Override
    public ItemDto updateItem(int itemId, ItemDto itemDto, int ownerId) {
        if (ownerId != itemRepository.findById(itemId).get().getOwner().getId()) {
            throw new NotFoundException("Owner id not equal to item`s owner id");
        }
        Item item = itemRepository.findById(itemId).get();
        itemMapper.updateItemFromDto(itemDto, item);
        item.setId(itemId);
        item.setOwner(userRepository.findById(ownerId).get());
        Item savedItem = itemRepository.save(item);
        return itemMapper.toDto(savedItem);
    }

    @Override
    public ItemDto getItem(int itemId) {
        Item item = itemRepository.findById(itemId).get();
        ItemDto itemDto = itemMapper.toDto(item);
        List<CommentDto> commentDtos = commentRepository.findByItemId(itemId).stream()
                .map(c -> commentMapper.toDto(c))
                .collect(Collectors.toList());
        itemDto.setComments(commentDtos);
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllUserItems(int ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(item -> {
                    ItemDto itemDto = itemMapper.toDto(item);
                    itemDto.setLastBooking(
                            bookingRepository.findLastCompletedBooking(item.getId(), LocalDateTime.now())
                                    .map(bookingMapper::toInputDto)
                                    .orElse(null)
                    );
                    itemDto.setNextBooking(
                            bookingRepository.findNextActiveBooking(item.getId(), LocalDateTime.now())
                                    .map(bookingMapper::toInputDto)
                                    .orElse(null)
                    );
                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        return itemRepository.searchAvailableItemsByText(text).stream()
                .map(item -> itemMapper.toDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, int itemId, int userId) {
        User author = userRepository.findById(userId).get();
        Item item = itemRepository.findById(itemId).get();
        boolean hasBooked = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                userId, itemId, LocalDateTime.now());
        if (!hasBooked) {
            throw new BadRequestException("Пользователь не брал эту вещь в аренду или аренда еще не завершена");
        }
        Comment comment = commentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        item.getComments().add(comment);
        itemRepository.save(item);
        return commentMapper.toDto(commentRepository.save(comment));
    }
}
