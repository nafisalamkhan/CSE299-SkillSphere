package com.cse299.skillSphere.models.Availablility;

import com.cse299.skillSphere.models.Course;
import com.cse299.skillSphere.models.CourseRequest;

public interface FreeOrPaid {
    Course createCourse(CourseRequest request);
}
