package openproject.where42.groupFriend;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.Groups;
import openproject.where42.group.repository.GroupRepository;
import openproject.where42.groupFriend.domain.GroupFriend;
import openproject.where42.groupFriend.domain.GroupFriendInfo;
import openproject.where42.groupFriend.repository.GroupFriendRepository;
import openproject.where42.member.OAuthToken;
import openproject.where42.member.domain.Member;
import openproject.where42.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupFriendService {
	private final MemberRepository memberRepository;
	private final GroupFriendRepository groupFriendRepository;
	private final GroupRepository groupRepository;

	// 친구 1명에 대한 그룹 추가
	@Transactional
	public void saveGroupFriend(String friendName, Long groupId) {
		Groups group = groupRepository.findById(groupId);

		GroupFriend groupFriend = new GroupFriend(friendName, group);
		groupFriendRepository.save(groupFriend);
//		return groupFriend.getId();
	}

	// 다중 친구 그룹 추가
	@Transactional
	public void multiSaveGroupFriend(List<GroupFriend> groupFriendList) {
		groupFriendRepository.multiSave(groupFriendList);
	}

	// 해당 친구가 포함되지 않은 그룹 목록 front 반환 ---> API
//	public List<String> notIncludeGroupByFriend(Member member, String friendName) {
//		return groupMemberRepository.notIncludeGroupByFriend(member, friendName);
//	}

	// 해당 그룹에 포함되지 않는 친구 목록 front 반환 ---> API
	public List<String> notIncludeFriendByGroup(Member member, Long groupId) {
		return groupFriendRepository.notIncludeFriendByGroup(member, groupId);
	}

	@Transactional
//	public void deleteGroupFriend(Long groupId, String friendName) {
	public void deleteGroupFriend(GroupFriend groupFriend) {
//		Groups group = groupRepository.findById(groupId);
//		GroupFriend groupFriend = groupMemberRepository.findByName(group, friendName); --> GroupFriend타입으로 못 받아올 경우
		groupFriendRepository.deleteGroupFriend(groupFriend);
	}

	@Transactional
	public void deleteGroupFriends(List<GroupFriend> groupFriends) {
		groupFriendRepository.deleteGroupFriends(groupFriends);
	}

	// 친구가 해당된 모든 그룹에서 삭제하기 --> 이거 어디서 불리는지 명확하지가 않아서 일단 보류
	@Transactional
	public void deleteFriendsGroupByName(Member member, String friendName) {
		groupFriendRepository.deleteFriendsGroupByName(member, friendName);
	}

	public List<GroupFriendInfo> findGroupFriendInfo(Long groupId) {
		List<String> nameList = groupFriendRepository.findGroupFriendsByGroupId(groupId);
		List<GroupFriendInfo> result = null;
		for (String i: nameList)
			result.add(new GroupFriendInfo().setting(i));
		return result;
	}

	public List<String> findAllGroupFriendNameByGroupId(Long groupId) {
		return groupFriendRepository.findGroupFriendsByGroupId(groupId);
	}
}

//	private void validateDuplicateGroupFriend(Groups group, String friend_name) {
//		for (GroupFriend gm: group.getGroupFriends()){
//			if (gm.getFriendName() == friend_name)
//				throw new IllegalStateException("이미 등록된 친구입니다.");
//		}
//	}