package com.socket.quizzes.service;

import com.socket.quizzes.dto.NotificationRequest;
import com.socket.quizzes.dto.QuestionRequest;
import com.socket.quizzes.model.Question;
import com.socket.quizzes.model.Quiz;
import com.socket.quizzes.dto.QuizRequest;
import com.socket.quizzes.repo.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final RestTemplate restTemplate;

    public Quiz createQuiz(QuizRequest request, String userId) throws AccessDeniedException {
        validatePermissions(request.companyId(), userId);

        if (request.questions().isEmpty())
            throw new IllegalArgumentException("Quiz must have at least 1 questions");

        for (QuestionRequest q : request.questions()) {
            if (q.options().size() < 2)
                throw new IllegalArgumentException("Each question must have at least 2 options");
        }

        Quiz quiz = Quiz.builder()
                .companyId(request.companyId())
                .title(request.title())
                .description(request.description())
                .frequencyDays(request.frequencyDays())
                .build();

        List<Question> questions = request.questions().stream().map(q ->
                Question.builder()
                        .text(q.text())
                        .options(q.options())
                        .correctAnswer(q.correctAnswers())
                        .quiz(quiz)
                        .build()
        ).toList();

        quiz.setQuestions(questions);

        Quiz saved = quizRepository.save(quiz);

        notifyCompanyMembers(request.companyId(), saved.getTitle());

        return saved;
    }

    public Quiz updateQuiz(Long id, QuizRequest request, String userId) throws AccessDeniedException {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        validatePermissions(quiz.getCompanyId(), userId);

        quiz.setTitle(request.title());
        quiz.setDescription(request.description());
        quiz.setFrequencyDays(request.frequencyDays());

        quiz.getQuestions().clear();
        for (QuestionRequest q : request.questions()) {
            quiz.getQuestions().add(Question.builder()
                    .text(q.text())
                    .options(q.options())
                    .correctAnswer(q.correctAnswers())
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

    private void notifyCompanyMembers(Long companyId, String quizTitle) {
        String membersUrl = "http://COMPANY/api/memberships/users?companyId=" + companyId;
        String[] userIds = restTemplate.getForObject(membersUrl, String[].class);

        if (userIds != null) {
            for (String userId : userIds) {
                NotificationRequest notification = new NotificationRequest(
                        userId,
                        "New quiz available: " + quizTitle
                );

                restTemplate.postForObject("http://NOTIFICATION/api/notifications", notification, Void.class);
            }
        }
    }

}

