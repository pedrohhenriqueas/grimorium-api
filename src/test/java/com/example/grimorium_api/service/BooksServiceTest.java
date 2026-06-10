package com.example.grimorium_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.grimorium_api.entity.Book;
import com.example.grimorium_api.models.BookDto;
import com.example.grimorium_api.repository.BooksRepository;

@ExtendWith(MockitoExtension.class)
public class BooksServiceTest {

    @InjectMocks
    BooksService booksService;

    @Mock
    BooksRepository booksRepository;
    
    @Test
    void shouldCreateBook() {
        // arrange
        BookDto newBookDto = new BookDto(0, "Clean Code", "Robert Martin", "A book about clean code",
                2008, 431, 5.0, List.of("Technology"), "Prentice Hall");

        Book bookSaved = new Book(1, "Clean Code", "Robert Martin", "A book about clean code",
                2008, 431, 5.0, List.of("Technology"), "Prentice Hall");

        when(booksRepository.save(any(Book.class))).thenReturn(bookSaved);

        // act
        Book result = booksService.create(newBookDto);

        // assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Clean Code", result.getTitle());
        assertEquals("Robert Martin", result.getAuthor());

        verify(booksRepository).save(any(Book.class));
    }

    @Test
    void shouldFindBookById() {
        // arrange
        Book book = new Book(1, "Clean Code", "Robert Martin", "A book about clean code",
                2008, 431, 5.0, List.of("Technology"), "Prentice Hall");

        when(booksRepository.findById(1)).thenReturn(Optional.of(book));

        // act
        Book result = booksService.findById(1);

        // assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Clean Code", result.getTitle());

        verify(booksRepository).findById(1);
    }

    @Test
    void shouldThrowWhenBookNotFound() {
        // arrange
        when(booksRepository.findById(99)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(NoSuchElementException.class, () -> booksService.findById(99));

        verify(booksRepository).findById(99);
    }

    @Test
    void shouldUpdateBook() {
        // arrange
        BookDto updatedBookDto = new BookDto(1, "Clean Code 2nd Ed", "Robert Martin", "A book about clean code",
                2020, 450, 5.0, List.of("Technology"), "Prentice Hall");
        Book bookSaved = new Book(1, "Clean Code 2nd Ed", "Robert Martin", "A book about clean code",
                2020, 450, 5.0, List.of("Technology"), "Prentice Hall");

        when(booksRepository.findById(1)).thenReturn(Optional.of(bookSaved));
        when(booksRepository.save(any(Book.class))).thenReturn(bookSaved);

        // act
        Book result = booksService.updateBook(updatedBookDto);

        // assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Clean Code 2nd Ed", result.getTitle());

        verify(booksRepository).save(any(Book.class));
    }

    @Test
    void shouldDeleteBook() {
        // arrange
        doNothing().when(booksRepository).deleteById(1);

        // act
        booksService.delete(1);

        // assert
        verify(booksRepository).deleteById(1);
    }
    
}
