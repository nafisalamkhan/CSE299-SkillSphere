package com.cse299.skillSphere.models.Availablility;

import com.cse299.skillSphere.models.Category;
import com.cse299.skillSphere.models.Course;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("free")
@Setter
@Getter

public class Free extends Course {
   @Column(name = "is_active")
    private boolean isActive = true;

}
