package com.billwise.service;

import com.billwise.domain.ExpenseGroup;
import com.billwise.domain.GroupMember;
import com.billwise.domain.GroupRole;
import com.billwise.domain.User;
import com.billwise.repository.ExpenseGroupRepository;
import com.billwise.repository.GroupMemberRepository;
import com.billwise.repository.UserRepository;
import com.billwise.web.dto.group.GroupRequest;
import com.billwise.web.dto.group.GroupResponse;
import com.billwise.exception.ForbiddenOperationException;
import com.billwise.web.dto.group.AddGroupMemberRequest;
import com.billwise.exception.ConflictException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class GroupServiceTests {

        @Mock
        private UserRepository userRepository;

        @Mock
        private ExpenseGroupRepository groupRepository;

        @Mock
        private GroupMemberRepository memberRepository;

        @InjectMocks
        private GroupService groupService;

        @Test
        void createsGroupAndOwnerMembership() {
                User owner = new User(
                                "phuong",
                                "phuong@example.com",
                                "encoded-password");

                GroupRequest request = new GroupRequest();
                request.setName("Finland Trip");
                request.setDescription("Shared travel expenses");
                request.setBaseCurrency("EUR");

                when(userRepository.findByUsername("phuong"))
                                .thenReturn(Optional.of(owner));

                when(groupRepository.save(any(ExpenseGroup.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                GroupResponse response = groupService.createGroup(
                                "  Phuong  ",
                                request);

                assertThat(response.name())
                                .isEqualTo("Finland Trip");

                assertThat(response.baseCurrency())
                                .isEqualTo("EUR");

                ArgumentCaptor<GroupMember> memberCaptor = ArgumentCaptor.forClass(GroupMember.class);

                verify(memberRepository)
                                .save(memberCaptor.capture());

                GroupMember savedMembership = memberCaptor.getValue();

                assertThat(savedMembership.getUser())
                                .isSameAs(owner);

                assertThat(savedMembership.getRole())
                                .isEqualTo(GroupRole.OWNER);

                verify(groupRepository)
                                .save(any(ExpenseGroup.class));
        }

        @Test
        void rejectsUserWhoIsNotAGroupMember() {
                User user = mock(User.class);
                ExpenseGroup group = mock(ExpenseGroup.class);

                when(user.getId()).thenReturn(5L);

                when(userRepository.findByUsername("phuong"))
                                .thenReturn(Optional.of(user));

                when(groupRepository.findById(10L))
                                .thenReturn(Optional.of(group));

                when(memberRepository.existsByGroupIdAndUserId(
                                10L,
                                5L)).thenReturn(false);

                assertThatThrownBy(() -> groupService.findGroupForUser(
                                10L,
                                "phuong"))
                                .isInstanceOf(
                                                ForbiddenOperationException.class)
                                .hasMessage(
                                                "You are not a member of this group");
        }

        @Test
        void rejectsGroupUpdateByNonOwner() {
                User currentUser = mock(User.class);
                User owner = mock(User.class);
                ExpenseGroup group = mock(ExpenseGroup.class);

                when(currentUser.getId()).thenReturn(2L);
                when(owner.getId()).thenReturn(1L);
                when(group.getOwner()).thenReturn(owner);

                when(userRepository.findByUsername("member"))
                                .thenReturn(Optional.of(currentUser));

                when(groupRepository.findById(10L))
                                .thenReturn(Optional.of(group));

                GroupRequest request = new GroupRequest();
                request.setName("Changed name");
                request.setDescription("Changed description");
                request.setBaseCurrency("EUR");

                assertThatThrownBy(() -> groupService.updateGroup(
                                10L,
                                "member",
                                request))
                                .isInstanceOf(
                                                ForbiddenOperationException.class)
                                .hasMessage(
                                                "Only the group owner can perform this operation");
        }

        @Test
        void allowsOwnerToDeleteGroup() {
                User owner = mock(User.class);
                ExpenseGroup group = mock(ExpenseGroup.class);

                when(owner.getId()).thenReturn(1L);
                when(group.getOwner()).thenReturn(owner);

                when(userRepository.findByUsername("owner"))
                                .thenReturn(Optional.of(owner));

                when(groupRepository.findById(10L))
                                .thenReturn(Optional.of(group));

                groupService.deleteGroup(10L, "owner");

                verify(groupRepository).delete(group);
        }

        @Test
        void allowsOwnerToAddMember() {
                User owner = mock(User.class);
                User newMember = mock(User.class);
                ExpenseGroup group = mock(ExpenseGroup.class);

                when(owner.getId()).thenReturn(1L);
                when(newMember.getId()).thenReturn(2L);
                when(newMember.getUsername()).thenReturn("member");
                when(newMember.getEmail())
                                .thenReturn("member@example.com");
                when(group.getOwner()).thenReturn(owner);

                when(userRepository.findByUsername("owner"))
                                .thenReturn(Optional.of(owner));

                when(userRepository.findByEmail(
                                "member@example.com"))
                                .thenReturn(Optional.of(newMember));

                when(groupRepository.findById(10L))
                                .thenReturn(Optional.of(group));

                when(memberRepository.existsByGroupIdAndUserId(
                                10L,
                                2L))
                                .thenReturn(false);

                when(memberRepository.save(any(GroupMember.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                AddGroupMemberRequest request = new AddGroupMemberRequest();

                request.setUsernameOrEmail(
                                " MEMBER@EXAMPLE.COM ");

                var response = groupService.addMember(
                                10L,
                                "owner",
                                request);

                assertThat(response.userId()).isEqualTo(2L);
                assertThat(response.username()).isEqualTo("member");
                assertThat(response.role())
                                .isEqualTo(GroupRole.MEMBER);

                verify(memberRepository)
                                .save(any(GroupMember.class));
        }

        @Test
        void rejectsDuplicateGroupMember() {
                User owner = mock(User.class);
                User existingMember = mock(User.class);
                ExpenseGroup group = mock(ExpenseGroup.class);

                when(owner.getId()).thenReturn(1L);
                when(existingMember.getId()).thenReturn(2L);
                when(group.getOwner()).thenReturn(owner);

                when(userRepository.findByUsername("owner"))
                                .thenReturn(Optional.of(owner));

                when(userRepository.findByUsername("member"))
                                .thenReturn(Optional.of(existingMember));

                when(groupRepository.findById(10L))
                                .thenReturn(Optional.of(group));

                when(memberRepository.existsByGroupIdAndUserId(
                                10L,
                                2L))
                                .thenReturn(true);

                AddGroupMemberRequest request = new AddGroupMemberRequest();

                request.setUsernameOrEmail("member");

                assertThatThrownBy(() -> groupService.addMember(
                                10L,
                                "owner",
                                request))
                                .isInstanceOf(ConflictException.class)
                                .hasMessage(
                                                "User is already a group member");

                verify(memberRepository, never())
                                .save(any(GroupMember.class));
        }

        @Test
        void allowsOwnerToRemoveMember() {
                User owner = mock(User.class);
                ExpenseGroup group = mock(ExpenseGroup.class);
                GroupMember membership = mock(GroupMember.class);

                when(owner.getId()).thenReturn(1L);
                when(group.getOwner()).thenReturn(owner);

                when(userRepository.findByUsername("owner"))
                                .thenReturn(Optional.of(owner));

                when(groupRepository.findById(10L))
                                .thenReturn(Optional.of(group));

                when(memberRepository.findByGroupIdAndUserId(
                                10L,
                                2L))
                                .thenReturn(Optional.of(membership));

                groupService.removeMember(
                                10L,
                                2L,
                                "owner");

                verify(memberRepository).delete(membership);
        }

        @Test
        void rejectsRemovingGroupOwner() {
                User owner = mock(User.class);
                ExpenseGroup group = mock(ExpenseGroup.class);

                when(owner.getId()).thenReturn(1L);
                when(group.getOwner()).thenReturn(owner);

                when(userRepository.findByUsername("owner"))
                                .thenReturn(Optional.of(owner));

                when(groupRepository.findById(10L))
                                .thenReturn(Optional.of(group));

                assertThatThrownBy(() -> groupService.removeMember(
                                10L,
                                1L,
                                "owner"))
                                .isInstanceOf(ConflictException.class)
                                .hasMessage(
                                                "The group owner cannot be removed");

                verify(memberRepository, never())
                                .delete(any(GroupMember.class));
        }

        @Test
        void rejectsMemberRemovalByNonOwner() {
                User currentMember = mock(User.class);
                User owner = mock(User.class);
                ExpenseGroup group = mock(ExpenseGroup.class);

                when(currentMember.getId()).thenReturn(2L);
                when(owner.getId()).thenReturn(1L);
                when(group.getOwner()).thenReturn(owner);

                when(userRepository.findByUsername("member"))
                                .thenReturn(Optional.of(currentMember));

                when(groupRepository.findById(10L))
                                .thenReturn(Optional.of(group));

                assertThatThrownBy(() -> groupService.removeMember(
                                10L,
                                3L,
                                "member"))
                                .isInstanceOf(
                                                ForbiddenOperationException.class)
                                .hasMessage(
                                                "Only the group owner can perform this operation");

                verify(memberRepository, never())
                                .delete(any(GroupMember.class));
        }
}