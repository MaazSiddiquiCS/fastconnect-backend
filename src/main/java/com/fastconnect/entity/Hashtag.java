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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hashtag_seq")
    @SequenceGenerator(
            name = "hashtag_seq",
            sequenceName = "hashtag_sequence",
            allocationSize = 50
    )
    @Column(name = "hashtag_id") 
    private Long hashtagId;

    @NotBlank(message = "Tag name is required")
    @Column(unique = true, nullable = false, length = 100)
    private String tag;

    @ManyToMany(
            mappedBy = "hashtags",
            fetch = FetchType.LAZY
    )
    private Set<Post> posts = new HashSet<>();
}