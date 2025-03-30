package com.cse299.skillSphere.repositories;

import com.cse299.skillSphere.models.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {
    QuestionAnswer findByQuestion(String question);
}
