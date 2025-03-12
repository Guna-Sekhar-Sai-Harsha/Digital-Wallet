package com.hpe.ExpenseSharingService.repository;

import com.hpe.ExpenseSharingService.model.GroupDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface GroupRepository extends JpaRepository<GroupDetails, Long>{
}