package com.example.grimorium_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.grimorium_api.entity.User;
import com.example.grimorium_api.models.UserDto;
import com.example.grimorium_api.repository.UsersRepository;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {
    
    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UsersService usersService;

    @Test
    void shouldReturnUserById(){
        // arrange
        User user = new User(1, "John Doe", "johndoe@email.com", 0);
        when(usersRepository.findById(1)).thenReturn(Optional.of(user));

        // act
        User result = usersService.findById(1);

        // assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(usersRepository, times(1)).findById(1);
    }

    @Test
    void shouldThrowExceptionWithUserNotFound(){
        // arrange
        // act
        when(usersRepository.findById(99)).thenReturn(Optional.empty());

        // assert
        assertThrows(NoSuchElementException.class, 
            () -> usersService.findById(99)
        );
    }

    @Test
    void shouldSaveUser(){
        // arrange
        UserDto newUserDto = new UserDto(null, "John Doe", "johndoe@email.com", 0);
        User userSaved = new User(1, "John Doe", "johndoe@email.com", 0);

        when(usersRepository.save(any(User.class))).thenReturn(userSaved);

        // act
        User result = usersService.create(newUserDto);

        // assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John Doe", result.getName());

        verify(usersRepository).save(any(User.class));
    }
}
