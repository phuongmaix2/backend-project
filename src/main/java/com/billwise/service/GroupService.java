package com.billwise.service;

import com.billwise.domain.ExpenseGroup;
import com.billwise.domain.GroupMember;
import com.billwise.domain.GroupRole;
import com.billwise.domain.User;
import com.billwise.exception.ForbiddenOperationException;
import com.billwise.exception.ResourceNotFoundException;
import com.billwise.repository.ExpenseGroupRepository;
import com.billwise.repository.GroupMemberRepository;
import com.billwise.repository.UserRepository;
import com.billwise.web.dto.group.GroupRequest;
import com.billwise.web.dto.group.GroupResponse;
import com.billwise.exception.ConflictException;
import com.billwise.web.dto.group.AddGroupMemberRequest;
import com.billwise.web.dto.group.GroupMemberResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Owns group CRUD and keeps membership/owner authorization in the service layer. */
@Service
public class GroupService {

        private final UserRepository userRepository;
        private final ExpenseGroupRepository groupRepository;
        private final GroupMemberRepository memberRepository;

        public GroupService(
                        UserRepository userRepository,
                        ExpenseGroupRepository groupRepository,
                        GroupMemberRepository memberRepository) {
                this.userRepository = userRepository;
                this.groupRepository = groupRepository;
                this.memberRepository = memberRepository;
        }

        @Transactional
        public GroupResponse createGroup(
                        String username,
                        GroupRequest request) {
                User owner = findUser(username);

                ExpenseGroup group = new ExpenseGroup(
                                request.getName(),
                                request.getDescription(),
                                request.getBaseCurrency(),
                                owner);

                ExpenseGroup savedGroup = groupRepository.save(group);

                GroupMember ownerMembership = new GroupMember(
                                savedGroup,
                                owner,
                                GroupRole.OWNER);

                memberRepository.save(ownerMembership);

                return GroupResponse.from(savedGroup);
        }

        @Transactional
        public GroupResponse updateGroup(
                        Long groupId,
                        String username,
                        GroupRequest request) {
                User currentUser = findUser(username);
                ExpenseGroup group = findGroup(groupId);

                requireOwner(group, currentUser);

                group.setName(request.getName());
                group.setDescription(request.getDescription());
                group.setBaseCurrency(
                                request.getBaseCurrency());

                return GroupResponse.from(group);
        }

        @Transactional
        public void deleteGroup(
                        Long groupId,
                        String username) {
                User currentUser = findUser(username);
                ExpenseGroup group = findGroup(groupId);

                requireOwner(group, currentUser);

                groupRepository.delete(group);
        }

        @Transactional
        public GroupMemberResponse addMember(
                        Long groupId,
                        String currentUsername,
                        AddGroupMemberRequest request) {
                User currentUser = findUser(currentUsername);
                ExpenseGroup group = findGroup(groupId);

                requireOwner(group, currentUser);

                User newMember = findUserByUsernameOrEmail(
                                request.getUsernameOrEmail());

                boolean alreadyMember = memberRepository.existsByGroupIdAndUserId(
                                groupId,
                                newMember.getId());

                if (alreadyMember) {
                        throw new ConflictException(
                                        "User is already a group member");
                }

                GroupMember membership = new GroupMember(
                                group,
                                newMember,
                                GroupRole.MEMBER);

                GroupMember savedMembership = memberRepository.save(membership);

                return GroupMemberResponse.from(savedMembership);
        }

        @Transactional(readOnly = true)
        public List<GroupMemberResponse> findMembers(
                        Long groupId,
                        String currentUsername) {

                User currentUser = findUser(currentUsername);
                ExpenseGroup group = findGroup(groupId);

                boolean isMember = memberRepository.existsByGroupIdAndUserId(
                                group.getId(),
                                currentUser.getId());

                if (!isMember) {
                        throw new ForbiddenOperationException(
                                        "You are not a member of this group");
                }

                return memberRepository
                                .findByGroupIdOrderByJoinedAtAsc(groupId)
                                .stream()
                                .map(GroupMemberResponse::from)
                                .toList();
        }

        @Transactional
        public void removeMember(
                        Long groupId,
                        Long userId,
                        String currentUsername) {

                User currentUser = findUser(currentUsername);
                ExpenseGroup group = findGroup(groupId);

                requireOwner(group, currentUser);

                if (Objects.equals(group.getOwner().getId(), userId)) {
                        throw new ConflictException(
                                        "The group owner cannot be removed");
                }

                GroupMember membership = memberRepository
                                .findByGroupIdAndUserId(groupId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Group member not found"));

                memberRepository.delete(membership);
        }

        @Transactional(readOnly = true)
        public List<GroupResponse> findGroupsForUser(
                        String username) {
                User user = findUser(username);

                return groupRepository
                                .findAllForUser(user.getId())
                                .stream()
                                .map(GroupResponse::from)
                                .toList();
        }

        @Transactional(readOnly = true)
        public GroupResponse findGroupForUser(
                        Long groupId,
                        String username) {
                User user = findUser(username);
                ExpenseGroup group = findGroup(groupId);

                boolean isMember = memberRepository.existsByGroupIdAndUserId(
                                groupId,
                                user.getId());

                if (!isMember) {
                        throw new ForbiddenOperationException(
                                        "You are not a member of this group");
                }

                return GroupResponse.from(group);
        }

        private User findUser(String username) {
                String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);

                return userRepository
                                .findByUsername(normalizedUsername)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found"));
        }

        private ExpenseGroup findGroup(Long groupId) {
                return groupRepository
                                .findById(groupId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Group not found"));
        }

        private void requireOwner(
                        ExpenseGroup group,
                        User currentUser) {
                boolean isOwner = Objects.equals(
                                group.getOwner().getId(),
                                currentUser.getId());

                if (!isOwner) {
                        throw new ForbiddenOperationException(
                                        "Only the group owner can perform this operation");
                }
        }

        private User findUserByUsernameOrEmail(
                        String usernameOrEmail) {
                String normalizedValue = usernameOrEmail
                                .trim()
                                .toLowerCase(Locale.ROOT);

                if (normalizedValue.contains("@")) {
                        return userRepository
                                        .findByEmail(normalizedValue)
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "User not found"));
                }

                return userRepository
                                .findByUsername(normalizedValue)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found"));
        }
}
