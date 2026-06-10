package com.example.grimorium_api.service;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.grimorium_api.entity.User;
import com.example.grimorium_api.models.UserDto;
import com.example.grimorium_api.repository.UsersRepository;

@Service
public class UsersService {
    
    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public User findById(int id){
        return usersRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
    }

    public User create(UserDto usersDto){
        User user = new User();
        user.setName(usersDto.getName());
        user.setEmail(usersDto.getEmail());
        user.setBooksRead(0);

        return usersRepository.save(user);
    }

    public User update(UserDto usersDto){
        User user = findById(usersDto.getId());
        user.setName(usersDto.getName());
        user.setEmail(usersDto.getEmail());
        user.setBooksRead(usersDto.getBooksRead());

        return usersRepository.save(user);
    }

    public void delete(int id){
        usersRepository.deleteById(id);
    }

}
