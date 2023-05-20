package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 512)
    private String description;
    @ManyToOne
    @JoinColumn(name = "requestor_id", referencedColumnName = "id")
    private User requestor;
    @Column(name = "create_date")
    private LocalDateTime created;
    @OneToMany
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private List<Item> items = new ArrayList<>();
}
