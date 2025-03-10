package com.cse299.skillSphere.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String description;

    private String fileName;
    private String fileType;

    @Lob
    private String videoBase64;

    private String filePath;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;
}
