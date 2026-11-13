package com.billwise.repository;

import com.billwise.domain.ExpenseGroup;
import com.billwise.domain.GroupMember;
import com.billwise.domain.GroupRole;
import com.billwise.domain.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class GroupRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseGroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository memberRepository;

    @Test
    void savesGroupAndFindsItForMember() {
        User owner = new User(
                "mai",
                "mai@example.com",
                "encoded-password");

        userRepository.saveAndFlush(owner);

        ExpenseGroup group = new ExpenseGroup(
                "Finland Trip",
                "Shared travel expenses",
                "EUR",
                owner);

        groupRepository.saveAndFlush(group);

        GroupMember membership = new GroupMember(
                group,
                owner,
                GroupRole.OWNER);

        memberRepository.saveAndFlush(membership);

        List<ExpenseGroup> groups = groupRepository.findAllForUser(owner.getId());

        assertThat(groups)
                .extracting(ExpenseGroup::getName)
                .containsExactly("Finland Trip");

        GroupMember savedMembership = memberRepository
                .findByGroupIdAndUserId(
                        group.getId(),
                        owner.getId())
                .orElseThrow();

        assertThat(savedMembership.getRole())
                .isEqualTo(GroupRole.OWNER);
    }

    @Test
    void preventsDuplicateGroupMembership() {
        User user = new User(
                "phuong",
                "phuong@example.com",
                "encoded-password");

        userRepository.saveAndFlush(user);

        ExpenseGroup group = new ExpenseGroup(
                "Dinner",
                "Friday dinner",
                "EUR",
                user);

        groupRepository.saveAndFlush(group);

        memberRepository.saveAndFlush(
                new GroupMember(
                        group,
                        user,
                        GroupRole.OWNER));

        GroupMember duplicateMembership = new GroupMember(
                group,
                user,
                GroupRole.MEMBER);

        assertThatThrownBy(() -> memberRepository.saveAndFlush(
                duplicateMembership)).isInstanceOf(
                        DataIntegrityViolationException.class);
    }
}