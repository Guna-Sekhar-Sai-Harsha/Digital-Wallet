package com.hpe.ExpenseSharingService.service;

import com.hpe.ExpenseSharingService.dto.ExpenseSharingGroupResponse;
import com.hpe.ExpenseSharingService.dto.GroupDTO;
import com.hpe.ExpenseSharingService.model.GroupDetails;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GroupServiceInterface {

    ResponseEntity<ExpenseSharingGroupResponse> createGroup(GroupDTO groupDto);

    ResponseEntity<ExpenseSharingGroupResponse> getGroup(Long groupId);
    ResponseEntity<ExpenseSharingGroupResponse> updateGroup(Long groupId, GroupDTO groupDTO);
    ResponseEntity<ExpenseSharingGroupResponse> deleteGroup(Long groupId);

}
