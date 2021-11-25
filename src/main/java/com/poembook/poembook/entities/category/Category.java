package com.poembook.poembook.entities.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.poembook.poembook.entities.poem.Poem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int categoryId;
    @Column(nullable = false, unique = true)
    private String categoryTitle;
    private boolean isActive;
    private Date creationDate;
    private String creatorUsername;
    private Date lastUpdateDate;
    private String updateUsername;
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    @ToString.Exclude
    private List<Poem> poems;

}
