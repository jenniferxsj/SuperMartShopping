package com.example.superdupermart.service;

import com.bfs.hibernateprojectdemo.dao.QuestionDao;
import com.bfs.hibernateprojectdemo.domain.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class QuestionService {
    private QuestionDao questionDao;

    @Autowired
    public void setQuestionDao(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Transactional
    public List<Question> getAllQuestions() {
        return questionDao.getAllQuestions();
    }

    @Transactional
    public Question getQuestionById(int id) {
        return questionDao.getQuestionById(id);
    }

    @Transactional
    public void addQuestion(Question... questions) {
        for (Question q : questions) {
            questionDao.addQuestion(q);
        }
    }

}
