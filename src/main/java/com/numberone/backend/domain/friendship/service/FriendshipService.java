package com.numberone.backend.domain.friendship.service;

import com.numberone.backend.domain.friendship.dto.response.GetFriendsResponse;
import com.numberone.backend.domain.friendship.dto.response.InviteFriendResponse;
import com.numberone.backend.domain.friendship.entity.Friendship;
import com.numberone.backend.domain.friendship.repository.FriendshipRepository;
import com.numberone.backend.domain.member.entity.Member;
import com.numberone.backend.domain.member.repository.MemberRepository;
import com.numberone.backend.domain.notification.entity.NotificationTag;
import com.numberone.backend.domain.token.util.SecurityContextProvider;
import com.numberone.backend.exception.notfound.NotFoundMemberException;
import com.numberone.backend.support.fcm.service.FcmMessageProvider;
import com.numberone.backend.support.notification.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendshipService {
    private final MemberRepository memberRepository;
    private final FriendshipRepository friendshipRepository;
    private final FcmMessageProvider fcmMessageProvider;

    @Transactional
    public InviteFriendResponse inviteFriend(Long memberId) {
        String principal = SecurityContextProvider.getAuthenticatedUserEmail();
        Member invitedMember = memberRepository.findByEmail(principal)
                .orElseThrow(NotFoundMemberException::new);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);
        Friendship savedFriendship = friendshipRepository.save(Friendship.createFriendship(member, invitedMember));
        return InviteFriendResponse.of(savedFriendship);
    }

    public GetFriendsResponse getFriends() {
        String principal = SecurityContextProvider.getAuthenticatedUserEmail();
        Member member = memberRepository.findByEmail(principal)
                .orElseThrow(NotFoundMemberException::new);
        List<GetFriendsResponse.MemberDto> friends = member.getFriendships()
                .stream().map(friendship -> {
                    Member friend = friendship.getFriend();
                    return GetFriendsResponse.MemberDto.of(friend);
                })
                .toList();
        return GetFriendsResponse.builder()
                .friends(friends)
                .build();
    }

    @Transactional
    public void sendFcmToFriend(Long friendId) {
        String principal = SecurityContextProvider.getAuthenticatedUserEmail();
        Member member = memberRepository.findByEmail(principal)
                .orElseThrow(NotFoundMemberException::new);
        Member friend = memberRepository.findById(friendId)
                .orElseThrow(NotFoundMemberException::new);

        String memberName = member.getRealName() != null ? member.getRealName() : member.getNickName();
        String friendName = friend.getRealName() != null ? friend.getRealName() : friend.getNickName();

        String title = String.format("[ %s님! %s님께서 회원님이 안전한지 걱정하고 있어요. 🥲]", friendName, memberName);
        String body = String.format(" %s님께 현재 상태를 보내볼까요? ", memberName);

        fcmMessageProvider.sendFcm(friend, title, body, NotificationTag.FAMILY);
    }

}
