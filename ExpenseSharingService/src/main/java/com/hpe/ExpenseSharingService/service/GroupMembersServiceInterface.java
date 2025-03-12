package com.hpe.ExpenseSharingService.service;

import com.hpe.ExpenseSharingService.dto.ExpenseSharingGroupResponse;
import com.hpe.ExpenseSharingService.model.GroupDetails;
import com.hpe.ExpenseSharingService.model.GroupMembersDetails;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GroupMembersServiceInterface {

    ResponseEntity<ExpenseSharingGroupResponse> addMembersToGroup(Long groupId, List<String> members);
    List<GroupMembersDetails> getAllGroupMembers(Long id);
    ResponseEntity<ExpenseSharingGroupResponse> makeMemberAdmin(Long groupId, String username);
    ResponseEntity<ExpenseSharingGroupResponse> removeAdminPrivileges(Long groupId, String username);
    ResponseEntity<ExpenseSharingGroupResponse> leaveGroup(Long groupId, String username);
    ResponseEntity<ExpenseSharingGroupResponse> transferGroupOwnership(Long groupId, String newOwner);
    List<GroupDetails> getGroupsByUser(String username);
}
