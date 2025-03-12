package com.hpe.UserService.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpe.UserService.dto.*;
import com.hpe.UserService.model.UserDetails;
import com.hpe.UserService.repository.UserRepository;
import com.hpe.UserService.utils.UserNameGeneration;
import com.hpe.UserService.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Service
public class UserService implements UserServiceInterface{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KafkaTemplate<String, String> template;

    private WebClient webClient;

    @Override
    public ResponseEntity<UserResponse> createUser(UserDTO userDTO) {

        if(userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())){
            return new ResponseEntity<>(UserResponse.builder()
                    .responseCode(UserUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(UserUtils.ACCOUNT_EXISTS_MESSAGE1)
                    .username(null)
                    .build(), HttpStatus.FORBIDDEN);
        } else if (userRepository.existsByEmail(userDTO.getEmail())) {
            return new ResponseEntity<>(UserResponse.builder()
                    .responseCode(UserUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(UserUtils.ACCOUNT_EXISTS_MESSAGE2)
                    .username(null)
                    .build(), HttpStatus.FORBIDDEN);
        } else if (Period.between(userDTO.getDateOfBirth(),LocalDate.now()).getYears()>=18) {
             return new ResponseEntity<>(UserResponse.builder()
                    .responseCode(UserUtils.AGE_NOT_PERMITED_CODE)
                    .responseMessage(UserUtils.AGE_NOT_PERMITED_MESSAGE)
                    .username(null)
                    .build(), HttpStatus.FORBIDDEN);
        }


        UserDetails userDetails = new UserDetails();
        String username = UserNameGeneration.genUserName(userDTO.getFirstName(), userDTO.getLastName());
        userDetails.setUsername(username);
        userDetails.setPassword(userDTO.getPassword());
        userDetails.setFirstName(userDTO.getFirstName());
        userDetails.setLastName(userDTO.getLastName());
        userDetails.setEmail(userDTO.getEmail());
        userDetails.setPhoneNumber(userDTO.getPhoneNumber());
        userDetails.setCountryCode(userDTO.getCountryCode());
        userDetails.setDateOfBirth(userDTO.getDateOfBirth());
        userDetails.setAddressLine1(userDTO.getAddressLine1());
        userDetails.setAddressLine2(userDTO.getAddressLine2());
        userDetails.setState(userDTO.getState());
        userDetails.setPincode(userDTO.getPincode());
        userDetails.setCountry(userDTO.getCountry());
        userDetails.setCurrencyCode(userDTO.getCurrencyCode());
        userDetails.setEnabled(true);
        userDetails.setCreatedAt(LocalDateTime.now());
        userDetails.setModifiedAt(LocalDateTime.now());
        userDetails.setMember(false);
        UserDetails savedUser = userRepository.save(userDetails);

        HttpEntity Rewardresponse = webClient.post()
                .uri( "http://rewards/createuser/{userName}", savedUser.getUsername())
                .retrieve()
                .bodyToMono(ResponseEntity.class)
                .block();

        HttpEntity Walletresponse = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("http://wallet/createWallet")
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(WalletDTO.builder()
                        .username(savedUser.getUsername())
                        .Currency(savedUser.getCurrencyCode())
                        .balance("0.00")
                        .build())
                .retrieve()
                .bodyToMono(ResponseEntity.class)
                .block();

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserName(savedUser.getUsername());
        notificationDTO.setMessage(UserUtils.ACCOUNT_CREATED_MESSAGE+".\nThe User ID is : "+savedUser.getUsername());
        notificationDTO.setType("EMail");
        notificationDTO.setEmail(EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("User Account Created")
                .messageBody(UserUtils.ACCOUNT_CREATED_MESSAGE+".\nThe User ID is : "+savedUser.getUsername()+".\nThe Balance is 0.00")
                .build());
        
        template.send("Notification", JsonToString(notificationDTO));

        return new ResponseEntity<>(UserResponse.builder()
                .responseCode(UserUtils.ACCOUNT_CREATED_CODE)
                .responseMessage(UserUtils.ACCOUNT_CREATED_MESSAGE)
                .username(savedUser.getUsername())
                .build(), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<UserResponse> updateMembership(String userName){
        UserDetails user = userRepository.findByUsername(userName);
        user.setMember(!user.isMember());
        user.setModifiedAt(LocalDateTime.now());
        userRepository.save(user);
        return new ResponseEntity<>(UserResponse.builder()
                .responseCode(UserUtils.MEMBERSHIP_UPDATED_CODE)
                .responseMessage(UserUtils.MEMBERSHIP_UPDATED_MESSADE)
                .username(user.getUsername())
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserResponse> isMember(String userName){
        UserDetails user = userRepository.findByUsername(userName);
        if(user.isMember()){
            return new ResponseEntity<>(UserResponse.builder()
                    .responseCode(UserUtils.USER_HAS_MEMBERSHIP_CODE)
                    .responseMessage(UserUtils.USER_HAS_MEMBERSHIP_MESSAGE)
                    .username(user.getUsername())
                    .build(), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(UserResponse.builder()
                    .responseCode(UserUtils.USER_HAS_NO_MEMBERSHIP_CODE)
                    .responseMessage(UserUtils.USER_HAS_NO_MEMBERSHIP_MESSAGE)
                    .username(user.getUsername())
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
