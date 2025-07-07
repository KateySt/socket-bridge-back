package com.socket.quizzes.service;

import com.socket.quizzes.dto.Question;
import com.socket.quizzes.dto.QuestionRequest;
import com.socket.quizzes.dto.Quiz;
import com.socket.quizzes.dto.QuizRequest;
import com.socket.quizzes.repo.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final RestTemplate restTemplate;

    public Quiz createQuiz(QuizRequest request, String userId) throws AccessDeniedException {
        validatePermissions(request.getCompanyId(), userId);

        if (request.getQuestions().isEmpty())
            throw new IllegalArgumentException("Quiz must have at least 1 questions");

        for (QuestionRequest q : request.getQuestions()) {
            if (q.getOptions().size() < 2)
                throw new IllegalArgumentException("Each question must have at least 2 options");
        }

        Quiz quiz = Quiz.builder()
                .companyId(request.getCompanyId())
                .title(request.getTitle())
                .description(request.getDescription())
                .frequencyDays(request.getFrequencyDays())
                .build();

        List<Question> questions = request.getQuestions().stream().map(q ->
                Question.builder()
                        .text(q.getText())
                        .options(q.getOptions())
                        .correctAnswer(q.getCorrectAnswers())
                        .quiz(quiz)
                        .build()
        ).toList();

        quiz.setQuestions(questions);
        return quizRepository.save(quiz);
    }

    public Quiz updateQuiz(Long id, QuizRequest request, String userId) throws AccessDeniedException {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        validatePermissions(quiz.getCompanyId(), userId);

        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setFrequencyDays(request.getFrequencyDays());

        quiz.getQuestions().clear();
        for (QuestionRequest q : request.getQuestions()) {
            quiz.getQuestions().add(Question.builder()
                    .text(q.getText())
                    .options(q.getOptions())
                    .correctAnswer(q.getCorrectAnswers())
                    .quiz(quiz)
                    .build());
        }

        return quizRepository.save(quiz);
    }

    public void deleteQuiz(Long id, String userId) throws AccessDeniedException {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        validatePermissions(quiz.getCompanyId(), userId);
        quizRepository.delete(quiz);
    }

    public List<Quiz> findAllByCompany(Long companyId) {
        return quizRepository.findByCompanyId(companyId);
    }

    private void validatePermissions(Long companyId, String userId) throws AccessDeniedException {
        String url = "http://COMPANY/api/memberships/check?userId=" + userId + "&companyId=" + companyId;
        var response = restTemplate.getForObject(url, Boolean.class);
        if (Boolean.FALSE.equals(response)) {
            throw new AccessDeniedException("Only owners and admins can manage quizzes");
        }
    }
}

