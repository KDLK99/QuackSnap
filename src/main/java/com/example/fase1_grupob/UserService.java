package com.example.fase1_grupob;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    private ConcurrentMap<Long, User> users = new ConcurrentHashMap<>();
    private AtomicLong nextId = new AtomicLong(1);

    public UserService(){
        this.addUser(new User());
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User findById(long id) {
        return users.get(id);
    }

    public void addUser(User user) {

        if(user.getId() == null || user.getId() == 0) {
            long id = nextId.getAndIncrement();
            user.setId(id);
        }

        this.users.put(user.getId(), user);
    }

    public void deleteById(long id) {
        this.users.remove(id);
    }
}
