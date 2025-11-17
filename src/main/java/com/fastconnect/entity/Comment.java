package com.fastconnect.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "comment")
public class Comment{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comment_id;
}
