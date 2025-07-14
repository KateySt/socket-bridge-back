package com.socket.quizzes.service;

import com.socket.quizzes.model.Quiz;
import com.socket.quizzes.model.QuizResult;
import com.socket.quizzes.repo.QuizRepository;
import com.socket.quizzes.repo.QuizResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizReminderService {
    private final QuizRepository quizRepo;
    private final QuizResultRepository resultRepo;
    private final CompanyUserClient client;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void runReminderTask() {
        Set<Long> companyIds = quizRepo.findAll()
                .stream().map(Quiz::getCompanyId).collect(Collectors.toSet());

        for (Long companyId : companyIds) {
            List<Quiz> quizzes = quizRepo.findByCompanyId(companyId);
            List<String> users = client.getCompanyUsers(companyId);
            for (String userId : users) {
                for (Quiz quiz : quizzes) {
                    Optional<QuizResult> last = resultRepo
                            .findFirstByUserIdAndQuizOrderByCompletedAtDesc(userId, quiz);
                    boolean needsReminder =
                            last.isEmpty() ||
                                    last.get().getCompletedAt()
                                            .isBefore(LocalDateTime.now().minusDays(quiz.getFrequencyDays()));

                    if (needsReminder) {
                        String email = client.getUserEmail(userId);
                        emailService.sendSimple(
                                email,
                                "Quiz reminder: " + quiz.getTitle(),
                                "By the way, you haven't been through this in a long time. «" + quiz.getTitle() + "»"
                        );
                    }
                }
            }
        }
    }
}
