package com.cse299.skillSphere.models.Availablility;

import com.cse299.skillSphere.models.Course;
import com.cse299.skillSphere.models.CourseRequest;

public class PaidCourse implements FreeOrPaid{

    @Override
    public Course createCourse(CourseRequest request) {
        Paid paid = new Paid();
        paid.setTitle(request.getTitle());
        paid.setStartTime(request.getStartTime());
        paid.setEndTime(request.getEndTime());
        return paid;
    }
}
