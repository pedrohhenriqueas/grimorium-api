package com.example.grimorium_api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.grimorium_api.entity.Users;
import com.example.grimorium_api.models.UsersDto;
import com.example.grimorium_api.repository.UsersRepository;

@Service
public class UsersService {
    
    @Autowired
    private UsersRepository usersRepository;

    public Users findById(int id){
        Optional<Users> userOptional = usersRepository.findById(id);
        return userOptional.get();
    }

    public Users create(UsersDto usersDto){
        Users user = new Users();
        user.setName(usersDto.getName());
        user.setEmail(usersDto.getEmail());
        user.setBooksRead(0);

        return usersRepository.save(user);
    }

    public Users update(UsersDto usersDto){
        Users user = findById(usersDto.getId());
        user.setName(usersDto.getName());
        user.setEmail(usersDto.getEmail());
        user.setBooksRead(usersDto.getBooksRead());

        return usersRepository.save(user);
    }

    public void delete(int id){
        usersRepository.deleteById(id);
    }

}
