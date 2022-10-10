package openproject.where42.member.dto;

import openproject.where42.member.domain.Member;

public class MemberInfo {// 이미 해당 멤버가 그 그룹 이름을 가지고 있는지 확인하는 메서드
    private Member member;
    private int flag;
    private int inOutState;
    private String seat;

    //내 상태 조회 메소드
    public MemberInfo getMyInfo(Member member) {
//        if (42 hane is 출근) {
//            if (api_ok) // api 자리 정보 있으면
//                return getMyAutoInfo(member); // api 정보 다시 조회 안하게 매개 변수로 넘겨 줄 수 있는지?
//            else
//                return getMySelfInfo(member);
//        }
//        return getMyOutInfo(member);
        return null;
    }
    private MemberInfo getMyAutoInfo(Member member) {
        this.flag = 0; // 자동 정보 플래그
        this.inOutState = 1; // 출근
        this.seat = "42api 정보"; // string??? 모르겠음
        this.member = member;

        return this;
    }

    private MemberInfo getMySelfInfo(Member member) {
        this.flag = 1; // 수동 정보 플래그
        this.inOutState = 1;

        return this; // cluster, floor, locate 정보 여부는 프론트에서 확인해서 처리
    }

    private MemberInfo getMyOutInfo(Member member) {
        this.flag = 1;
        this.inOutState = 0;
        // 외출 관련 로직 추가 여부에 따라 시간 계산 로직 필요
        member.updateLocate(null, null, null);
        this.member = member;
        return this;
    }
}
