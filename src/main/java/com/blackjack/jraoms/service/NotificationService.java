package com.blackjack.jraoms.service;

import com.blackjack.jraoms.dto.InterviewerNotificationDto;
import com.blackjack.jraoms.dto.NotificationContentDto;
import com.blackjack.jraoms.dto.NotificationDto;
import com.blackjack.jraoms.dto.OfferMailNotificationDto;
import com.blackjack.jraoms.entity.*;
import com.blackjack.jraoms.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final VacancyRepository vacancyRepository;
    private final UserRepository userRepository;
    private final NotificationUserRepository notificationUserRepository;
    private final InterviewerNotificationRepository interviewerNotificationRepository;
    private final InterviewerNotificationUserRepository interviewerNotificationUserRepository;
    private final CandidateRepository candidateRepository;
    private final OfferMailNotificationRepository offerMailNotificationRepository;
    private final OfferMailNotificationUserRepository offerMailNotificationUserRepository;

    public Notification saveNotification(NotificationDto notificationDto){
        log.info("vacancyId : "+notificationDto.getVacancyId());
        var notification = Notification.builder()
                .receive(LocalDateTime.now())
                .vacancyId(notificationDto.getVacancyId())
                .build();
        log.info("save notification");
        return notificationRepository.save(notification);
    }

    public List<NotificationUser> saveNotificationUser(List<Notification> notifications, int userId){
        User user = userRepository.findById(userId).orElse(null);
        log.info("Fetch user to save in NotificationUser");
        List<NotificationUser> notificationUserList = new ArrayList<>();
        for (Notification notification : notifications){
            var newNotificationUser = NotificationUser.builder()
                    .notification(notification)
                    .user(user)
                    .build();
            var notificationUser = notificationUserRepository.save(newNotificationUser);
            log.info("save NotificationUser");
            notificationUserList.add(notificationUser);
        }
        return notificationUserList;
    }

    public List<NotificationUser> clearAllNotification(
            List<NotificationContentDto> notificationContentDtoList,
            int userId){

        List<NotificationUser> notificationUsers = new ArrayList<>();

        for (NotificationContentDto notificationContentDto: notificationContentDtoList){
           var newNotificationUsers = saveNotificationUser(notificationContentDto.getNotifications(),userId);
           notificationUsers.addAll(newNotificationUsers);
        }
        return notificationUsers;
    }

    public List<NotificationContentDto> notificationContent(int userId){
        List<Vacancy> vacancies = vacancyRepository.findAll();
        log.info("Fetch vacancy list to show notification");
        List<NotificationContentDto> notificationContentDtoList = new ArrayList<>();
        for (Vacancy vacancy : vacancies) {
            List<Notification> notificationList = notificationRepository.getNotificationsByUserAndPost(userId, vacancy.getId());
            if (notificationList != null && !notificationList.isEmpty()) {
                var notificationContentDto = NotificationContentDto.builder()
                        .vacancy(vacancy)
                        .notifications(notificationList)
                        .build();
                notificationContentDtoList.add(notificationContentDto);
            }
        }
        return notificationContentDtoList;
    }

    public InterviewerNotification saveInterviewerNotification(InterviewerNotificationDto interviewerNotificationDto){
        var candidate = candidateRepository.findById(interviewerNotificationDto.getCandidateId()).orElse(null);
        var interviewerNotification = InterviewerNotification.builder()
                .receive(LocalDateTime.now())
                .action(interviewerNotificationDto.getAction())
                .interviewDate(interviewerNotificationDto.getInterviewDate())
                .candidate(candidate)
                .vacancyId(interviewerNotificationDto.getVacancyId())
                .interviewFormat(interviewerNotificationDto.getInterviewFormat())
                .build();
        return interviewerNotificationRepository.save(interviewerNotification);
    }

    public InterviewerNotificationUser saveInterviewerNotificationUser(InterviewerNotification notification, int userId){
        User user = userRepository.findById(userId).orElse(null);
        log.info("Fetch user to save in InterviewerNotificationUser");

        var interviewerNotificationUser = InterviewerNotificationUser.builder()
                .interviewerNotification(notification)
                .user(user)
                .build();
        return interviewerNotificationUserRepository.save(interviewerNotificationUser);
    }

    public List<InterviewerNotificationUser> clearAllInterviewerNotification(
            List<InterviewerNotification> interviewerNotifications,int userId
    ){
        List<InterviewerNotificationUser> interviewerNotificationUsers = new ArrayList<>();
        for (InterviewerNotification notification : interviewerNotifications){
            var interviewerNotification = saveInterviewerNotificationUser(notification,userId);
            interviewerNotificationUsers.add(interviewerNotification);
        }
        return interviewerNotificationUsers;
    }

    public OfferMailNotification saveOfferMailNotification(OfferMailNotificationDto offerMailNotificationDto){
        var candidate = candidateRepository.findById(offerMailNotificationDto.getCandidateId()).orElse(null);
        var vacancy = vacancyRepository.findById(offerMailNotificationDto.getVacancyId()).orElse(null);
        var offerMailNotification = OfferMailNotification.builder()
                .receive(LocalDateTime.now())
                .vacancy(vacancy)
                .candidate(candidate)
                .build();
        return offerMailNotificationRepository.save(offerMailNotification);
    }

    public OfferMailNotificationUser saveOfferMailNotificationUser(OfferMailNotification offerMailNotification,int userId){
        var user = userRepository.findById(userId).orElse(null);
        var offerMailNotificationUser = OfferMailNotificationUser.builder()
                .offerMailNotification(offerMailNotification)
                .user(user)
                .build();
        return offerMailNotificationUserRepository.save(offerMailNotificationUser);
    }

    public List<OfferMailNotificationUser> clearAllOfferMailNotification(
            List<OfferMailNotification> offerMailNotifications,int userId
    ){
        List<OfferMailNotificationUser> offerMailNotificationUsers = new ArrayList<>();
        for (OfferMailNotification notification : offerMailNotifications){
            var offerMailNotificationUser = saveOfferMailNotificationUser(notification,userId);
            offerMailNotificationUsers.add(offerMailNotificationUser);
        }
        return offerMailNotificationUsers;
    }


}