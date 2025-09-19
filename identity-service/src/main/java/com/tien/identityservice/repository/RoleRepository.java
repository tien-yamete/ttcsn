package com.tien.identityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tien.identityservice.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {}
