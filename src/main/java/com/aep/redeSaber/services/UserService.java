package com.aep.redeSaber.services;

import com.aep.redeSaber.models.User;
import com.aep.redeSaber.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User criar(User user){
        return userRepository.save(user);
    }

    public List<User> listar(){
        return userRepository.findAll();
    }

    public Optional<User> listarPorId(Integer id){
        return userRepository.findById(id);
    }
}
