package com.example.fase1_grupob.service;

import com.example.fase1_grupob.model.UserP;
import com.example.fase1_grupob.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
        UserP user = new UserP();
        this.addUser(user);
    }

    public Collection<UserP> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UserP> findById(long id) {
        return userRepository.findById(id);
    }

    public void addUser(UserP user) {
        userRepository.save(user);
    }

    public void deleteById(long id) {
        this.userRepository.deleteById(id);
    }

    public List<UserP> findByIds(List<Long> ids){
        return userRepository.findAllById(ids);
    }
}
