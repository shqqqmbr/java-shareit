package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public ItemDto addItem(ItemDto itemDto, int ownerId) {
        Item item = itemMapper.toEntity(itemDto);
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Owner with id=" + ownerId + " not found"));
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);
        return itemMapper.toDto(savedItem);
    }

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

    public ItemDto getItem(int itemId) {
        Item item = itemRepository.findById(itemId).get();
        return itemMapper.toDto(item);
    }

    public List<ItemDto> getAllUserItems(int ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(item -> itemMapper.toDto(item))
                .collect(Collectors.toList());
    }

    public List<ItemDto> getItemsByText(String text) {
        return itemRepository.searchAvailableItemsByText(text).stream()
                .map(item -> itemMapper.toDto(item))
                .collect(Collectors.toList());
    }
}
