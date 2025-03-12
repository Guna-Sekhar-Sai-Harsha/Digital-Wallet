package com.hpe.ExpenseSharingService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpe.ExpenseSharingService.dto.ExpenseSharingGroupResponse;
import com.hpe.ExpenseSharingService.dto.NotificationDTO;
import com.hpe.ExpenseSharingService.model.GroupDetails;
import com.hpe.ExpenseSharingService.model.GroupMembersDetails;
import com.hpe.ExpenseSharingService.repository.GroupMembersRepository;
import com.hpe.ExpenseSharingService.repository.GroupRepository;
import com.hpe.ExpenseSharingService.utils.ExpenseSharingGroupUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class GroupMembersService implements GroupMembersServiceInterface{

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMembersRepository groupMembersRepository;

    @Autowired
    private KafkaTemplate<String, String> template;

    @Override
    public ResponseEntity<ExpenseSharingGroupResponse> addMembersToGroup(Long groupId, List<String> members) {
        GroupDetails groupDetails = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        for (String member : members) {
            if (groupMembersRepository.existsByGroupIdAndUsername(groupId, member)) {
                continue;
            }
            groupMembersRepository.save(GroupMembersDetails.builder()
                    .username(member)
                    .group(groupDetails)
                    .isAdmin(false)
                    .build());

            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setUserName(member);
            notificationDTO.setMessage("You have been added to Expense sharing group named "+groupDetails.getGroupName());
            notificationDTO.setType("Push");
            template.send("Notification", JsonToString(notificationDTO));
        }

        return new ResponseEntity<>(ExpenseSharingGroupResponse.builder()
                .responseCode(ExpenseSharingGroupUtils.GROUP_MEMBERS_ADDED_CODE)
                .responseMessage(ExpenseSharingGroupUtils.GROUP_MEMBERS_ADDED_MESSAGE)
                .group(groupRepository.getReferenceById(groupId))
                .build(), HttpStatus.OK);
    }
    @Override
    public List<GroupMembersDetails> getAllGroupMembers(Long id){
        return groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found!"))
                .getMembers();
    }
    @Override
    public ResponseEntity<ExpenseSharingGroupResponse> makeMemberAdmin(Long groupId, String username) {
        GroupMembersDetails membersDetails = groupMembersRepository.findAllByUsername(username).stream()
                .filter(m -> m.getGroup().getId().equals(groupId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Member not found"));

        membersDetails.setAdmin(true);
        groupMembersRepository.save(membersDetails);

        GroupDetails group = groupRepository.getReferenceById(groupId);

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserName(membersDetails.getUsername());
        notificationDTO.setMessage("You have been added as Admin for Expense sharing group named "+group.getGroupName());
        notificationDTO.setType("Push");
        template.send("Notification", JsonToString(notificationDTO));

        return new ResponseEntity<>(ExpenseSharingGroupResponse.builder()
                .responseCode(ExpenseSharingGroupUtils.ADMIN_ADDED_CODE)
                .responseMessage(ExpenseSharingGroupUtils.ADMIN_ADDED_MESSAGE)
                .group(group)
            .build(), HttpStatus.OK);
    }
    @Override
    public ResponseEntity<ExpenseSharingGroupResponse> removeAdminPrivileges(Long groupId, String username) {
        GroupMembersDetails membersDetails = groupMembersRepository.findAllByUsername(username).stream()
                .filter(m -> m.getGroup().getId().equals(groupId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Member not found"));

        membersDetails.setAdmin(false);
        groupMembersRepository.save(membersDetails);

        GroupDetails group = groupRepository.getReferenceById(groupId);

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserName(membersDetails.getUsername());
        notificationDTO.setMessage("You have been removed as Admin for Expense sharing group named "+group.getGroupName());
        notificationDTO.setType("Push");
        template.send("Notification", JsonToString(notificationDTO));

        return new ResponseEntity<>(ExpenseSharingGroupResponse.builder()
                .responseCode(ExpenseSharingGroupUtils.ADMIN_REMOVED_CODE)
                .responseMessage(ExpenseSharingGroupUtils.ADMIN_REMOVED_MESSAGE)
                .group(group)
                .build(), HttpStatus.OK);
    }
    @Override
    public ResponseEntity<ExpenseSharingGroupResponse> leaveGroup(Long groupId, String username) {
        GroupMembersDetails membersDetails = groupMembersRepository.findAllByUsername(username).stream()
                .filter(m -> m.getGroup().getId().equals(groupId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Member not found"));

        groupMembersRepository.delete(membersDetails);

        GroupDetails group = groupRepository.getReferenceById(groupId);

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserName(membersDetails.getUsername());
        notificationDTO.setMessage("You have left from Expense sharing group named "+group.getGroupName());
        notificationDTO.setType("Push");
        template.send("Notification", JsonToString(notificationDTO));

        return new ResponseEntity<>(ExpenseSharingGroupResponse.builder()
                .responseCode(ExpenseSharingGroupUtils.GROUP_MEMBER_REMOVED_CODE)
                .responseMessage(ExpenseSharingGroupUtils.GROUP_MEMBER_REMOVED_MESSAGE)
                .group(null)
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ExpenseSharingGroupResponse> transferGroupOwnership(Long groupId, String newOwner) {

        GroupDetails group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (groupMembersRepository.existsByGroupIdAndUsername(groupId, newOwner)) {
            group.setCreatedBy(newOwner);
            groupRepository.save(group);

            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setUserName(newOwner);
            notificationDTO.setMessage("Ownership has been transferred to you for Expense sharing group named "+group.getGroupName());
            notificationDTO.setType("Push");
            template.send("Notification", JsonToString(notificationDTO));

            return new ResponseEntity<>(ExpenseSharingGroupResponse.builder()
                    .responseCode(ExpenseSharingGroupUtils.GROUP_OWNER_TRANSFERRED_CODE)
                    .responseMessage(ExpenseSharingGroupUtils.GROUP_OWNER_TRANSFERRED_MESSAGE)
                    .group(group)
                    .build(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ExpenseSharingGroupResponse.builder()
                    .responseCode(ExpenseSharingGroupUtils.GROUP_OWNER_NOT_TRANSFERRED_CODE)
                    .responseMessage(ExpenseSharingGroupUtils.GROUP_OWNER_NOT_TRANSFERRED_MESSAGE)
                    .group(group)
                    .build(), HttpStatus.OK);
        }
    }

    @Override
    public List<GroupDetails> getGroupsByUser(String username){

        return groupMembersRepository.findAllByUsername(username)
                .stream()
                .map(GroupMembersDetails::getGroup)
                .distinct()
                .collect(Collectors.toList());
    }

    public String JsonToString(Object objectDTO){

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String sendNotification = objectMapper.writeValueAsString(objectDTO);
            return sendNotification;
        }catch (Exception e){
            System.out.println(e);
        }
        return null;

    }
}
