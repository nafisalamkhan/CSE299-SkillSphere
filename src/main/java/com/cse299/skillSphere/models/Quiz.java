package com.cse299.skillSphere.models;

import jakarta.persistence.*;

public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QuizID")
    private int quizId;

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "QuizDate")
    private String quizDate;

    @ManyToOne
    @JoinColumn(name = "CategoryID", nullable = false)
    private Category category;

    @ManyToMany
    @JoinColumn(name = "StudentID")
    private User student;

}
