package com.cse299.skillSphere.models;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "enrollment")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EnrollmentID")
    private int enrollmentId;

    @ManyToMany
    @JoinColumn(name = "StudentID")
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

}
