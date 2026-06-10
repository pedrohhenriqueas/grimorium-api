package com.example.grimorium_api.controller;

import com.example.grimorium_api.entity.Book;
import com.example.grimorium_api.models.BookDto;
import com.example.grimorium_api.models.response.MessageResponse;
import com.example.grimorium_api.service.BooksService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/books")
public class BooksController {

    private BooksService booksService;

    public BooksController(BooksService booksService) {
        this.booksService = booksService;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody BookDto bookDto){
        Book book = booksService.create(bookDto);
        BookDto createdBook = BookDto.toDTO(book);

        return ResponseEntity.ok().body(createdBook);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable int id){
        Book book = booksService.findById(id);
        BookDto bookFound = BookDto.toDTO(book);

        return ResponseEntity.ok().body(bookFound);
    }

    @PutMapping
    public ResponseEntity<Object> update(@RequestBody BookDto bookDto){
        Book book = booksService.updateBook(bookDto);
        BookDto updatedBook = BookDto.toDTO(book);

        return ResponseEntity.ok().body(updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable int id){
        booksService.delete(id);
        return ResponseEntity.ok().body(new MessageResponse("Livro excluído com sucesso."));
    }

    @GetMapping
    public ResponseEntity<Object> searchBookFromGoogleBooksApi(@RequestParam String book) throws URISyntaxException, IOException {
        List<Map<String, Object>> response = booksService.apiSearchByName(book);

        return ResponseEntity.ok().body(response);
    }
}
