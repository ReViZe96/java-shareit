package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")
public class Booking implements Comparable<Booking> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(name = "reserve_start")
    private LocalDateTime start;

    @Column(name = "reserve_end")
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User requestedUser;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item requestedItem;

    @Override
    public int compareTo(Booking booking) {
        if (this.getStart().equals(booking.getStart())) {
            return 0;
        } else if (this.getStart().isBefore(booking.getStart())) {
            return -1;
        } else {
            return 1;
        }
    }

}
