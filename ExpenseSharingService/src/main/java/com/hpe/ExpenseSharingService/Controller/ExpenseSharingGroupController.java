package com.hpe.ExpenseSharingService.Controller;

import com.hpe.ExpenseSharingService.dto.ExpenseSharingGroupResponse;
import com.hpe.ExpenseSharingService.dto.GroupDTO;
import com.hpe.ExpenseSharingService.model.GroupDetails;
import com.hpe.ExpenseSharingService.model.GroupMembersDetails;
import com.hpe.ExpenseSharingService.service.GroupMembersService;
import com.hpe.ExpenseSharingService.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenseSharing/group")
public class ExpenseSharingGroupController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private GroupMembersService groupMembersService;

    @PostMapping("/create")
    public ResponseEntity<ExpenseSharingGroupResponse> createGroup(@RequestBody GroupDTO groupDto){
        return groupService.createGroup(groupDto);
    }
    @PostMapping("/update/{groupId}")
    public ResponseEntity<ExpenseSharingGroupResponse> updateGroup(@PathVariable Long groupId,@RequestBody GroupDTO groupDTO){
        return groupService.updateGroup(groupId,groupDTO);
    }
    @PostMapping("/add/{groupId}")
    public ResponseEntity<ExpenseSharingGroupResponse> addMembersToGroup(@PathVariable Long groupId, @RequestBody List<String> members){
        return groupMembersService.addMembersToGroup(groupId, members);
    }
    @PostMapping("/makeAdmin/{groupId}/{username}")
    public ResponseEntity<ExpenseSharingGroupResponse> makeMemberAdmin(@PathVariable Long groupId, @PathVariable String username){
        return groupMembersService.makeMemberAdmin(groupId, username);
    }
    @PostMapping("/removeAdmin/{groupId}/{username}")
    public ResponseEntity<ExpenseSharingGroupResponse> removeAdminPrivileges(@PathVariable Long groupId, @PathVariable String username){
        return groupMembersService.removeAdminPrivileges(groupId, username);
    }
    @PostMapping("/transferOwner/{groupId}/{username}")
    public ResponseEntity<ExpenseSharingGroupResponse> transferGroupOwnership(@PathVariable Long groupId, @PathVariable String newOwner){
        return groupMembersService.transferGroupOwnership(groupId, newOwner);
    }

    @GetMapping("/groupDetails/{groupId}")
    public ResponseEntity<ExpenseSharingGroupResponse> getGroup(@PathVariable Long groupId){
        return groupService.getGroup(groupId);
    }
    @GetMapping("/userGroups/{username}")
    public List<GroupDetails> getGroupsByUser(@PathVariable String username){
        return groupMembersService.getGroupsByUser(username);
    }
    @GetMapping("/groupMembers/{groupId}")
    public List<GroupMembersDetails> getAllGroupMembers(@PathVariable Long id){
        return groupMembersService.getAllGroupMembers(id);
    }

    @DeleteMapping("/delete/{groupId}")
    public ResponseEntity<ExpenseSharingGroupResponse> deleteGroup(@PathVariable Long groupId){
        return groupService.deleteGroup(groupId);
    }
    @DeleteMapping("/leve/{groupId}/{username}")
    public ResponseEntity<ExpenseSharingGroupResponse> leaveGroup(@PathVariable Long groupId, @PathVariable String username){
        return groupMembersService.leaveGroup(groupId, username);
    }

}
