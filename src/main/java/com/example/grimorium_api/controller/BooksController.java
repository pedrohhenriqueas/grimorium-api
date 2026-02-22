package com.example.grimorium_api.controller;

import com.example.grimorium_api.entity.Book;
import com.example.grimorium_api.models.BookDto;
import com.example.grimorium_api.service.BooksService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/books")
public class BooksController {

    @Autowired
    private BooksService booksService;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody BookDto bookDto){
        Book book = booksService.create(bookDto);
        return ResponseEntity.ok().body(book);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable int id){
        Book book = booksService.findById(id);
        return ResponseEntity.ok().body(book);
    }

    @PutMapping
    public ResponseEntity<Object> update(@RequestBody BookDto bookDto){
        Book book = booksService.updateBook(bookDto);
        return ResponseEntity.ok().body(book);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable int id){
        booksService.delete(id);
        return ResponseEntity.ok().body("Livro excluído com sucesso.");
    }

    @GetMapping
    public ResponseEntity<Object> getBookFromOpenApi(@RequestParam String bookName) throws URISyntaxException, IOException {
        BookDto bookDto = booksService.getBookFromOpenApi(bookName);

        return ResponseEntity.ok().body(bookDto);
    }
}
