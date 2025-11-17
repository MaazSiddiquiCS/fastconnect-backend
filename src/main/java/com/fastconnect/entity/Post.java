package com.fastconnect.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class  Post{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
}