package com.example.fase1_grupob.repository;

import com.example.fase1_grupob.model.UserP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserP, Long> {

    Optional<UserP> findByUsername(String name);
}
