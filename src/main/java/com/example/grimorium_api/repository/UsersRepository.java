package com.example.grimorium_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.grimorium_api.entity.User;

@Repository
public interface UsersRepository extends JpaRepository<User, Integer>{
    
}
