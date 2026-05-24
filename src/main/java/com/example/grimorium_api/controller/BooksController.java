package com.example.grimorium_api.controller;

import com.example.grimorium_api.entity.Book;
import com.example.grimorium_api.models.BookDto;
import com.example.grimorium_api.models.response.MessageResponse;
import com.example.grimorium_api.service.BooksService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/books")
public class BooksController {

    private final BooksService booksService;    

    public BooksController(BooksService booksService) {
        this.booksService = booksService;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody BookDto bookDto){
        Book book = booksService.create(bookDto);
        return ResponseEntity.ok().body(book);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable int id){
        Book book;
        try {
            book = booksService.findById(id);
        } catch (NotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("Livro Não Encontrado"));
        }
        return ResponseEntity.ok().body(book);
    }

    @PutMapping
    public ResponseEntity<Object> update(@RequestBody BookDto bookDto) throws NotFoundException{
        Book book = booksService.updateBook(bookDto);
        return ResponseEntity.ok().body(book);
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

    @GetMapping("googleId")
    public ResponseEntity<Object> getBookFromApiByGoogleId(@RequestParam String googleId) throws URISyntaxException, IOException{

        Map<String, Object> response = booksService.searchInApiByGoogleId(googleId);

        return ResponseEntity.ok(response);
    }

}
