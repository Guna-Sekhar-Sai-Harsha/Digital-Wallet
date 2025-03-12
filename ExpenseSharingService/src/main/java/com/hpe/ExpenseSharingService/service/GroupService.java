package com.hpe.ExpenseSharingService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpe.ExpenseSharingService.dto.ExpenseSharingGroupResponse;
import com.hpe.ExpenseSharingService.dto.GroupDTO;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService implements GroupServiceInterface{

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMembersRepository groupMembersRepository;

    @Autowired
    private KafkaTemplate<String, String> template;


    @Override
    public ResponseEntity<ExpenseSharingGroupResponse> createGroup(GroupDTO groupDto){

        GroupDetails groupDetails = new GroupDetails();
        groupDetails.setGroupName(groupDto.getGroupName());
        groupDetails.setDescription(groupDto.getDescription());
        groupDetails.setCreatedBy(groupDto.getCreatedBy());
        groupDetails.setCreatedAt(LocalDateTime.now());
        groupDetails.setModifiedAt(LocalDateTime.now());
        GroupDetails savedGroup = groupRepository.save(groupDetails);

        groupMembersRepository.save(GroupMembersDetails.builder()
                                        .username(savedGroup.getCreatedBy())
                                        .group(savedGroup)
                                        .isAdmin(true)
                                        .build());

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserName(savedGroup.getCreatedBy());
        notificationDTO.setMessage("Expense sharing group named "+savedGroup.getGroupName()+" is created by you");
        notificationDTO.setType("Push");
        template.send("Notification", JsonToString(notificationDTO));


        return new ResponseEntity<>(ExpenseSharingGroupResponse.builder()
                .responseCode(ExpenseSharingGroupUtils.GROUP_CREATED_CODE)
                .responseMessage(ExpenseSharingGroupUtils.GROUP_CREATED_MESSAGE)
                .group(savedGroup)
                .build(), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ExpenseSharingGroupResponse> getGroup(Long groupId) {
        GroupDetails group = groupRepository.findById(groupId)
                .orElse(null);
        if (group != null){
            return new ResponseEntity<>(ExpenseSharingGroupResponse.builder()
                    .responseCode(ExpenseSharingGroupUtils.GROUP_FOUND_CODE)
                    .responseMessage(ExpenseSharingGroupUtils.GROUP_FOUND_MESSAGE)
                    .group(group)
                    .build(), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(ExpenseSharingGroupResponse.builder()
                    .responseCode(ExpenseSharingGroupUtils.GROUP_NOT_FOUND_CODE)
                    .responseMessage(ExpenseSharingGroupUtils.GROUP_NOT_FOUND_MESSAGE)
                    .group(null)
                    .build(), HttpStatus.OK);
        }
    }
    @Override
    public ResponseEntity<ExpenseSharingGroupResponse> updateGroup(Long groupId, GroupDTO groupDTO) {

        GroupDetails group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        group.setGroupName(groupDTO.getGroupName());
        group.setDescription(groupDTO.getDescription());
        groupRepository.save(group);

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserName(group.getCreatedBy());
        notificationDTO.setMessage("Expense sharing group named "+group.getGroupName()+" is updated");
        notificationDTO.setType("Push");
        template.send("Notification", JsonToString(notificationDTO));

        return new ResponseEntity<>(ExpenseSharingGroupResponse.builder()
                .responseCode(ExpenseSharingGroupUtils.GROUP_UPDATED_CODE)
                .responseMessage(ExpenseSharingGroupUtils.GROUP_UPDATED_MESSAGE)
                .group(group)
                .build(), HttpStatus.OK);
    }
    @Override
    public ResponseEntity<ExpenseSharingGroupResponse> deleteGroup(Long groupId) {
        if (groupRepository.existsById(groupId)) {
            GroupDetails group = groupRepository.getReferenceById(groupId);

            groupRepository.deleteById(groupId);

            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setUserName(group.getCreatedBy());
            notificationDTO.setMessage("Expense sharing group named "+group.getGroupName()+" is deleted");
            notificationDTO.setType("Push");
            template.send("Notification", JsonToString(notificationDTO));

            return new ResponseEntity<>(ExpenseSharingGroupResponse.builder()
                    .responseCode(ExpenseSharingGroupUtils.GROUP_DELETED_CODE)
                    .responseMessage(ExpenseSharingGroupUtils.GROUP_DELETED_MESSAGE)
                    .group(group)
                    .build(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ExpenseSharingGroupResponse.builder()
                    .responseCode(ExpenseSharingGroupUtils.GROUP_NOT_FOUND_CODE)
                    .responseMessage(ExpenseSharingGroupUtils.GROUP_NOT_FOUND_MESSAGE)
                    .group(null)
                    .build(), HttpStatus.OK);
        }
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
