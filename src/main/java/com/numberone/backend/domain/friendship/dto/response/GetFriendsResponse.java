package com.numberone.backend.domain.friendship.dto.response;

import com.numberone.backend.domain.member.entity.Member;
import lombok.*;

import java.util.List;

@ToString
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GetFriendsResponse {
    List<MemberDto> friends;
    @Builder
    @AllArgsConstructor
    public static class MemberDto {
        private Long friendMemberId;
        private String realName;
        private String nickName;
        private String profileImageUrl;
        private String address;
        private Boolean session;
        private Boolean isSafety;

        public static MemberDto of(Member member) {
            return MemberDto.builder()
                    .friendMemberId(member.getId())
                    .realName(member.getRealName())
                    .nickName(member.getNickName())
                    .profileImageUrl(member.getProfileImageUrl())
                    .address(member.getAddress())
                    .session(member.getSession())
                    .isSafety(member.getIsSafety())
                    .build();
        }
    }
}
