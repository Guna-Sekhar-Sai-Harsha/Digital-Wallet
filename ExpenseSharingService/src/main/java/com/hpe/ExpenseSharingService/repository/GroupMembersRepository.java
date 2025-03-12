package com.hpe.ExpenseSharingService.repository;


import com.hpe.ExpenseSharingService.model.GroupMembersDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMembersRepository extends JpaRepository<GroupMembersDetails, Long>{
    List<GroupMembersDetails> findAllByUsername(String username);

    boolean existsByGroupIdAndUsername(Long groupId, String member);
}

