package com.cse299.skillSphere.controllers;

import com.cse299.skillSphere.models.QuestionAnswer;
import com.cse299.skillSphere.services.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class ChatRestController {

    @Autowired
    private ChatbotService chatBotService;

    // get responses for your questions
    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
        return chatBotService.handleInput(message);
    }

    // get list of all the questions
    @PostMapping("/getAllQuestions")
    public List<String> questions() {
        List<String> questions = chatBotService.getAllQuestions();
        return questions;
    }

    // Create new questions and answers
    @PostMapping("/qa")
    public QuestionAnswer createQuestionAnswer(@RequestBody QuestionAnswer qa) {
        return chatBotService.createQuestionAnswer(qa);
    }

    // Update existing answers
    @PutMapping("/qa")
    public QuestionAnswer updateQuestionAnswer(@RequestBody QuestionAnswer request) {
        return chatBotService.updateQuestionAnswer(request.getQuestion(),request.getAnswer());
    }


    // Delete a question
    @DeleteMapping("/qa")
    public void deleteQuestionAnswer(@RequestBody String question) {
        chatBotService.deleteQuestionAnswer(question);
    }

}