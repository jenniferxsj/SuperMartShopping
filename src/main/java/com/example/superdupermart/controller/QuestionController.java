package com.example.superdupermart.controller;

import com.example.superdupermart.domain.Question;
import com.example.superdupermart.dto.common.DataResponse;
import com.example.superdupermart.dto.question.QuestionCreationRequest;
import com.example.superdupermart.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/question/all")
    @ResponseBody
    public DataResponse getAllQuestions() {
        return DataResponse.builder()
                .success(true)
                .message("Success")
                .data(questionService.getAllQuestions())
                .build();
    }

    @GetMapping("/question/{id}")
    @ResponseBody
    public DataResponse getQuestionById(@PathVariable int id) {
        return DataResponse.builder()
                .success(true)
                .message("Success")
                .data(questionService.getQuestionById(id))
                .build();
    }

    @PostMapping("/question")
    @ResponseBody
    public DataResponse addQuestion(@Valid @RequestBody QuestionCreationRequest request, BindingResult result) {

        if (result.hasErrors()) return DataResponse.builder()
                                            .success(false)
                                            .message("Something went wrong")
                                            .build();

        Question question = Question.builder()
                .description(request.getDescription())
                .isActive(request.isActive())
                .build();

        questionService.addQuestion(question);

        return DataResponse.builder()
                .success(true)
                .message("Success")
                .build();
    }
}
