package com.cse299.skillSphere.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "course")
@Data
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CourseID")
    private int courseId;

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "CourseDate")
    private String courseDate;

    @Column(name = "TotalStudent")
    private int totalStudent;

    @ManyToOne
    @JoinColumn(name = "CategoryID", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "InstructorID")
    private User instructor;
}
