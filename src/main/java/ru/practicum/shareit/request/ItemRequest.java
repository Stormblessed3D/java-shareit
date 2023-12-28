package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.shareit.user.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    @EqualsAndHashCode.Include
    private long id;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name =  "requestor_id", referencedColumnName = "user_id")
    @ToString.Exclude
    private User requestor;
    /*private LocalDateTime created;*/

    public ItemRequest(long id, String description, User requestor) {
        this.id = id;
        this.description = description;
        this.requestor = requestor;
    }
}
