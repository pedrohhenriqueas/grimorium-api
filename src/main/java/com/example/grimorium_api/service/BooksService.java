package com.example.grimorium_api.service;

import com.example.grimorium_api.entity.Book;
import com.example.grimorium_api.models.BookDto;
import com.example.grimorium_api.repository.BooksRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class BooksService {

    private final BooksRepository booksRepository;
    private final GoogleBooksApiService googleBooksApiService;

    public BooksService(BooksRepository booksRepository, GoogleBooksApiService googleBooksApiService) {
        this.booksRepository = booksRepository;
        this.googleBooksApiService = googleBooksApiService;
    }

    public Book create(BookDto bookDto){
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setSynopsis(bookDto.getSynopsis());
        book.setPublishYear(bookDto.getPublishYear());
        book.setPages(bookDto.getPages());
        book.setRating(bookDto.getRating());
        book.setCategory(bookDto.getCategory());
        book.setPublisher(bookDto.getPublisher());
        return booksRepository.save(book);
    }

    public Book findById(int id){
        return booksRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Book not found with id: " + id));
    }

    public Book updateBook(BookDto updatedBook){
        Book book = findById(updatedBook.getId());
        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setSynopsis(updatedBook.getSynopsis());
        book.setPublishYear(updatedBook.getPublishYear());
        book.setPages(updatedBook.getPages());
        book.setRating(updatedBook.getRating());
        book.setCategory(updatedBook.getCategory());
        book.setPublisher(updatedBook.getPublisher());
        return booksRepository.save(book);
    }

    public void delete(int id){
        booksRepository.deleteById(id);
    }

    public List<Map<String, Object>> apiSearchByName(String bookName) throws URISyntaxException, IOException{
        Object booksFound = googleBooksApiService.searchBookFromGoogleBooksApi(bookName);
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> booksMap = mapper.convertValue(booksFound, Map.class);

        List<Map<String, Object>> items = (List<Map<String, Object>>) booksMap.get("items");

        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> response = new ArrayList<>();

        for(Map<String, Object> item : items){

            Map<String, Object> volumeInfo = mapper.convertValue(item.get("volumeInfo"), Map.class);

            if (volumeInfo == null) continue;

            String title = volumeInfo.get("title").toString();

            if(title.toLowerCase().contains(bookName.toLowerCase())){
                List<String> categories = (List<String>) volumeInfo.get("categories");
                List<String> authors = (List<String>) volumeInfo.get("authors");

                if (authors != null && categories != null){
                    Map<String, Object> book = new HashMap<>();
                    book.put("googleId", item.get("id"));
                    book.put("title", title);
                    book.put("author", authors.get(0));
                    book.put("category", categories.get(0));

                    response.add(book);
                }
            }
        }

        return response;
    }
}
