package ru.practicum.shareit.request.model;

import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ItemRequest {

    private Long id;
    private String description;
    private Boolean created;
    private Long requestedUserId;
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "requestId")
    private List<Item> requestedItems;

}
