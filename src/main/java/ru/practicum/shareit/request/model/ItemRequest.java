package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest implements Comparable<ItemRequest> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_created")
    private Boolean created;

    @Column(name = "user_id")
    private Long requestedUserId;
    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item requestedItems;

    @Override
    public int compareTo(ItemRequest request) {
        if (this.creationDate.equals(request.creationDate)) {
            return 0;
        } else if (this.creationDate.isBefore(request.creationDate)) {
            return -1;
        } else {
            return 1;
        }
    }

}
