package com.wanderlust.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wanderlust.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
