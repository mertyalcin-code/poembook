package com.poembook.poembook.business.concretes;

import com.poembook.poembook.business.abstracts.FollowerService;
import com.poembook.poembook.business.abstracts.NoticeService;
import com.poembook.poembook.core.utilities.result.*;
import com.poembook.poembook.entities.users.Follower;
import com.poembook.poembook.entities.users.User;
import com.poembook.poembook.repository.FollowersRepo;
import com.poembook.poembook.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.poembook.poembook.constant.FollowerConstant.*;
import static com.poembook.poembook.constant.UserConstant.USER_NOT_FOUND;

@Service
@AllArgsConstructor
public class FollowerManager implements FollowerService {
    private final UserRepo userRepo;
    private final FollowersRepo followersRepo;
    private final NoticeService noticeService;


    @Override
    public Result create(String from, String to) {
        User fromUser = userRepo.findUserByUsername(from);
        User toUser = userRepo.findUserByUsername(to);
        if (fromUser == null || toUser == null) {
            return new ErrorResult(USER_NOT_FOUND);
        }
        Follower follower = followersRepo.findByFromAndTo(fromUser, toUser);
        if (follower != null) {
            return new ErrorResult(FOLLOWER_EXIST);
        }
        Follower newFollowers = new Follower();
        newFollowers.setFrom(fromUser);
        newFollowers.setTo(toUser);
        newFollowers.setFollowTime(LocalDateTime.now().atZone(ZoneId.of("UTC+3")));
        followersRepo.save(newFollowers);

        noticeService.create((fromUser.getFirstName() + " " + fromUser.getLastName() + " artık seni takip ediyor."), to);

        return new SuccessResult(FOLLOWER_CREATED);
    }

    @Override
    public Result isFollowing(String from, String to) {
        if (followersRepo.findByFromAndTo(userRepo.findUserByUsername(from), userRepo.findUserByUsername(to)) == null) {
            return new ErrorResult();
        }
        return new SuccessResult();
    }


    @Override
    public Result delete(String fromUsername, String toUsername) {
        User fromUser = userRepo.findUserByUsername(fromUsername);
        User toUser = userRepo.findUserByUsername(toUsername);

        Follower deletedFollower = followersRepo.findByFromAndTo(fromUser, toUser);
        if (deletedFollower == null) {
            return new ErrorResult(FOLLOWER_NOT_EXIST);
        } else {
            followersRepo.delete(deletedFollower);
            return new SuccessResult(FOLLOWER_REMOVED);
        }
    }

    @Override
    public DataResult<List<String>> getUsersFollowers(String username) {
        List<Follower> followers = followersRepo.findAllByTo(userRepo.findUserByUsername(username));
        followers.removeIf(follower -> !follower.getFrom().isActive()); //süper kısaltma imiş
        if (followers.size() < 1) {
            return new ErrorDataResult<>(NO_FOLLOWERS_FOUND);
        } else {
            return new SuccessDataResult<>(getFollowersUsername(followers), FOLLOWERS_LISTED);
        }
    }

    @Override
    public DataResult<List<String>> getUsersFallowing(String username) {
        List<Follower> followings = followersRepo.findAllByFrom(userRepo.findUserByUsername(username));
        followings.removeIf(following -> !following.getFrom().isActive());
        if (followings.size() < 1) {
            return new ErrorDataResult<>(NO_FOLLOWINGS_FOUND);
        } else {
            return new SuccessDataResult<>(getFollowingsUsername(followings), FOLLOWERS_LISTED);
        }
    }

    private List<String> getFollowingsUsername(List<Follower> followings) {
        List<String> followingsUsernames = new ArrayList<>();
        for (Follower following : followings) {
            followingsUsernames.add(following.getTo().getUsername());
        }
        return followingsUsernames;
    }

    private List<String> getFollowersUsername(List<Follower> followers) {
        List<String> followersUsernames = new ArrayList<>();
        for (Follower follower : followers) {
            followersUsernames.add(follower.getFrom().getUsername());
        }
        return followersUsernames;
    }


}
