package com.poembook.poembook.entities.message;

import com.poembook.poembook.entities.users.User;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class PrivateMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long pmId;
    private ZonedDateTime pmTime;
    private String message;
    @ManyToOne
    @JoinColumn(name = "from_user_fk")
    private User from;
    @ManyToOne
    @JoinColumn(name = "to_user_fk")
    private User to;
}
