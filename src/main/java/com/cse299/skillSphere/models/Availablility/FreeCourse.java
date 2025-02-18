package com.cse299.skillSphere.models.Availablility;

import com.cse299.skillSphere.models.Course;
import com.cse299.skillSphere.models.CourseRequest;

public class FreeCourse implements FreeOrPaid{

    @Override
    public Course createCourse(CourseRequest request) {

        Free free = new Free();
        free.setTitle(request.getTitle());

        return free;
    }
}
