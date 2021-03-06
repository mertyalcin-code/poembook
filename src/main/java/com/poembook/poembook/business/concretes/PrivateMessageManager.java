package com.poembook.poembook.business.concretes;

import com.poembook.poembook.business.abstracts.NoticeService;
import com.poembook.poembook.business.abstracts.PrivateMessageService;
import com.poembook.poembook.core.utilities.result.*;
import com.poembook.poembook.core.utilities.service.EmailService;
import com.poembook.poembook.entities.message.PrivateMessage;
import com.poembook.poembook.entities.users.User;
import com.poembook.poembook.repository.PrivateMessageRepo;
import com.poembook.poembook.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.poembook.poembook.constant.PrivateMessageConstant.MESSAGE_LISTED;
import static com.poembook.poembook.constant.PrivateMessageConstant.MESSAGE_SENT;
import static com.poembook.poembook.constant.UserConstant.USER_NOT_FOUND;

@Service
@AllArgsConstructor
public class PrivateMessageManager implements PrivateMessageService {
    private PrivateMessageRepo privateMessageRepo;
    private UserRepo userRepo;
    private NoticeService noticeService;
    private EmailService emailService;

    @Override
    public Result sendMessage(String fromUsername, String toUsername, String message) throws MessagingException {
        User fromUser = userRepo.findUserByUsername(fromUsername);
        User toUser = userRepo.findUserByUsername(toUsername);
        if (fromUser == null || toUser == null) {
            return new ErrorResult(USER_NOT_FOUND);
        }
        if (!StringUtils.isNotBlank(message)) {
            return new ErrorResult("");
        }
        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.setFrom(fromUser);
        privateMessage.setTo(toUser);
        privateMessage.setMessage(message);
        privateMessage.setPmTime(LocalDateTime.now().atZone(ZoneId.of("UTC")));
       /* List<PrivateMessage> messages = privateMessageRepo.findAllByFromAndTo(fromUser, toUser);
        messages.removeIf(pm -> pm.getPmTime().isBefore(ChronoZonedDateTime.from(LocalDateTime.now().minusHours(24))));
        if (messages.size() < 1) {
            emailService.sendYouHaveMessageEmail(toUser.getUsername(), toUser.getEmail(), fromUser.getFirstName() + " " + fromUser.getLastName());
        }*/

        privateMessageRepo.save(privateMessage);
        noticeService.create(
                fromUser.getFirstName() + " " + fromUser.getLastName() + MESSAGE_SENT
                , toUsername);
        return new SuccessResult(MESSAGE_SENT);
    }

    @Override
    public DataResult<List<PrivateMessage>> usersAllMessages(String username) {
        User fromUser = userRepo.findUserByUsername(username);
        User toUser = userRepo.findUserByUsername(username);
        if (fromUser == null) {
            return new ErrorDataResult<>(USER_NOT_FOUND);
        }
        List<PrivateMessage> fromMessage = privateMessageRepo.findAllByFrom(fromUser);
        List<PrivateMessage> toMessage = privateMessageRepo.findAllByTo(toUser);
        List<PrivateMessage> usersAllMessages = new ArrayList<>();
        if (fromMessage != null) {
            usersAllMessages.addAll(fromMessage);
        }
        if (toMessage != null) {
            usersAllMessages.addAll(toMessage);
        }
        return new SuccessDataResult<>(usersAllMessages, MESSAGE_LISTED);
    }

    @Override
    public DataResult<List<PrivateMessage>> usersAllMessagesWith(String username, String withUsername) {
        List<PrivateMessage> privateMessages = new ArrayList<>();
        User user = userRepo.findUserByUsername(username);
        User withUser = userRepo.findUserByUsername(withUsername);
        if (user == null || withUser == null) {
            return new ErrorDataResult<>(USER_NOT_FOUND);
        }
        if (privateMessageRepo.findAllByFromAndTo(user, withUser) != null) {
            privateMessages.addAll(privateMessageRepo.findAllByFromAndTo(user, withUser));
        }
        if (privateMessageRepo.findAllByFromAndTo(withUser, user) != null) {
            privateMessages.addAll(privateMessageRepo.findAllByFromAndTo(withUser, user));
        }
        privateMessages.sort(Comparator.comparing(PrivateMessage::getPmTime));

        return new SuccessDataResult<>(privateMessages, MESSAGE_LISTED);
    }

    @Override
    public DataResult<List<User>> usersMessageList(String username) {
        List<User> users = new ArrayList<>();
        List<PrivateMessage> messages = usersAllMessages(username).getData();
        messages.sort(Comparator.comparing(PrivateMessage::getPmTime).reversed());
        for (PrivateMessage pm : messages) {
            if (!users.contains(pm.getTo())) {
                users.add(pm.getTo());
            }
            if (!users.contains(pm.getFrom())) {
                users.add(pm.getFrom());
            }
        }
        users.remove(userRepo.findUserByUsername(username));
        return new SuccessDataResult<>(users, MESSAGE_LISTED);
    }
}
