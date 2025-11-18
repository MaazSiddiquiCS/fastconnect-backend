package com.fastconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "hashtags")
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hashtag_id;

    @NotBlank(message = "Tag name is required")
    @Column(unique = true, nullable = false, length = 100)
    private String tag;

    @ManyToMany(
            mappedBy = "hashtags",
            fetch = FetchType.LAZY
                    )
    private Set<Post> posts = new HashSet<>();
}