package com.example.grimorium_api.service;

import com.example.grimorium_api.entity.Book;
import com.example.grimorium_api.models.BookDto;
import com.example.grimorium_api.repository.BooksRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BooksService {

    private static final String TITLE = "title";

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

    public Book findById(int id) throws NotFoundException{
        Optional<Book> book = booksRepository.findById(id);
        if (book.isEmpty())
            throw new NotFoundException();
        
        return book.get();
    }

    public Book updateBook(BookDto updatedBook) throws NotFoundException{
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

        Map<String, Object> booksMap = convert(mapper, booksFound, new TypeReference<>() {});

        List<Map<String, Object>> items = convert(mapper, booksMap.get("items"), new TypeReference<>() {});

        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> response = new ArrayList<>();

        for(Map<String, Object> item : items){
            Map<String, Object> volumeInfo = convert(mapper, item.get("volumeInfo"), new TypeReference<>() {});

            if (volumeInfo == null) continue;

            String title = volumeInfo.get(TITLE).toString();

            if(title.toLowerCase().contains(bookName.toLowerCase())){
                List<String> categories = convert(mapper,  volumeInfo.get("categories"), new TypeReference<>() {});
                List<String> authors = convert(mapper, volumeInfo.get("authors"), new TypeReference<>() {});

                if (authors != null && categories != null){
                    Map<String, Object> book = new HashMap<>();
                    book.put("googleId", item.get("id"));
                    book.put(TITLE, title);
                    book.put("author", authors.get(0));
                    book.put("category", categories.get(0));

                    response.add(book);
                }
            }
        }

        return response;
    }

    public Map<String, Object> searchInApiByGoogleId(String googleId) throws URISyntaxException, IOException{
        Object googleApiResponse = googleBooksApiService.searchBookFromApiById(googleId);
        Map<String, Object> response = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> bookFoundMap = convert(mapper, googleApiResponse, new TypeReference<Map<String, Object>>() {});
        Map<String, Object> volumeInfo = convert(mapper, bookFoundMap.get("volumeInfo"), new TypeReference<>() {});
        List<String> categories = convert(mapper, volumeInfo.get("categories"), new TypeReference<>() {});
        List<String> authors = convert(mapper, volumeInfo.get("authors"), new TypeReference<>() {});
        Map<String, Object> imageLinks = convert(mapper, volumeInfo.get("imageLinks"), new TypeReference<>() {});

        response.put("id", bookFoundMap.get("id").toString());
        response.put(TITLE, volumeInfo.get(TITLE).toString());
        response.put("author", authors.get(0));
        response.put("category", categories.get(0));
        response.put("rating", volumeInfo.get("averageRating").toString());
        response.put("pageCount", volumeInfo.get("pageCount").toString());
        response.put("description", volumeInfo.get("description").toString());
        response.put("publishedDate", volumeInfo.get("publishedDate").toString());
        response.put("imageLink", imageLinks.get("medium"));

        return response;
    }

    private <T> T convert(ObjectMapper mapper, Object value, TypeReference<T> typeRef) {
        return mapper.convertValue(value, typeRef);
    }
}
