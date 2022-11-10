package openproject.where42.groupFriend;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.Groups;
import openproject.where42.group.GroupRepository;
import openproject.where42.groupFriend.domain.GroupFriend;
import openproject.where42.member.domain.Member;
import openproject.where42.member.MemberRepository;
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
	}

	@Transactional
	public void saveGroupFriend(String friendName, Groups group) {
		GroupFriend groupFriend = new GroupFriend(friendName, group);
		groupFriendRepository.save(groupFriend);
	}

	@Transactional
	public void addFriendsToGroup(List<String> friendNames, Groups group) {
		for (String friendName : friendNames) {
			saveGroupFriend(friendName, group);
		}
	}

	@Transactional
	public void deleteGroupFriend(Long friendId) {
		groupFriendRepository.deleteGroupFriendByGroupFriendId(friendId);
	}

	@Transactional
	public void deleteGroupFriend(Long groupId, List<String> friendNames) {
//		groupFriendRepository.deleteGroupFriendsByGroupFriendId(friendNames); // groupId + friendNames 조합으로 삭제할 수 있게
	}

	@Transactional
	public void deleteFriend(Long memberId, String friendName) { // 이게 현재 딜리트프렌즈그룹바이네임
		Member member = memberRepository.findById(memberId);
		groupFriendRepository.deleteFriendByFriendName(member, friendName);
	}

	public List<GroupFriend> findAllFriends(Long memberId) {
		return groupFriendRepository.findAllGroupFriendByOwnerId(memberId);
	}

	public List<String> findAllGroupFriendNameByGroupId(Long groupId) {
		return groupFriendRepository.findGroupFriendsByGroupId(groupId);
	}
}