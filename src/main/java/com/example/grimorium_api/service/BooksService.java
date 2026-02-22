package com.example.grimorium_api.service;

import com.example.grimorium_api.entity.Book;
import com.example.grimorium_api.entity.Users;
import com.example.grimorium_api.models.BookDto;
import com.example.grimorium_api.repository.BooksRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BooksService {

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private UsersService usersService;

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
        Optional<Book> book = booksRepository.findById(id);
        return book.get();
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

    public Object searchBookFromOpenApi(String bookName) throws URISyntaxException, IOException {
        HttpGet httpGet = new HttpGet("https://openlibrary.org/search.json");
        URI uri = new URIBuilder(httpGet.getUri())
                .addParameter("title", bookName)
                .build();

        httpGet.setUri(uri);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            ObjectMapper mapper = new ObjectMapper();
            Object result = httpClient.execute(httpGet, response ->
                    mapper.readValue(
                            EntityUtils.toString(response.getEntity()),
                            Object.class
                    )
            );
            return result;
        }
    }

    public List<Book> findUserBooks(int userId){
        Users user = usersService.findById(userId);
        return user.getFavoriteBooks();
    }

    public BookDto getBookFromOpenApi(String bookName) throws URISyntaxException, IOException{
        Object booksFromOpenApi = searchBookFromOpenApi(bookName);
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> responseMap = mapper.convertValue(booksFromOpenApi, Map.class);

        List<Map<String, Object>> booksFound = (List<Map<String, Object>>) responseMap.get("docs");

        return attributeValuesToBooks(booksFound);
    }

    private BookDto attributeValuesToBooks(List<Map<String,Object>> booksFound) {

            Map<String, Object> book = booksFound.get(0);

            BookDto bookToAddToResponse = new BookDto();
            bookToAddToResponse.setAuthor(((List<String>) book.get("author_name")).get(0).toString());
            bookToAddToResponse.setTitle(book.get("title").toString());
            bookToAddToResponse.setPublishYear((int) book.get("first_publish_year"));

            String bookKeyToRequestMoreDetails = book.get("key").toString();
            String authorKey = ((List<String>) book.get("author_name")).get(0).toString();

//            requestDescriptionAndSubjects(bookKeyToRequestMoreDetails, bookToAddToResponse);
//            requestPublishersAndNumOfPages(bookKeyToRequestMoreDetails, bookToAddToResponse, authorKey);
//            requestRatings(bookKeyToRequestMoreDetails, bookToAddToResponse);

            return bookToAddToResponse;
    }

    private void requestDescriptionAndSubjects(String bookKey, BookDto bookDto) throws URISyntaxException, IOException{
        String url = "https://openlibrary.org" + "/works" + bookKey + ".json";

        HttpGet httpGet = new HttpGet(url);
        URI uri = new URIBuilder(httpGet.getUri())
                .build();

        httpGet.setUri(uri);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            ObjectMapper mapper = new ObjectMapper();
            Object result = httpClient.execute(httpGet, response ->
                    mapper.readValue(
                            EntityUtils.toString(response.getEntity()),
                            Object.class
                    )
            );

            Map<String, Object> resultMap = mapper.convertValue(result, Map.class);

            if(resultMap.get("description") != null) {
                bookDto.setSynopsis(resultMap.get("description").toString());
            }

            if(resultMap.get("subjects") != null){
                bookDto.setCategory((List<String>) resultMap.get("subjects"));        
            }
        }
    }

    private void requestPublishersAndNumOfPages(String bookKey, BookDto bookDto, String authorKey) throws URISyntaxException, IOException{
        String url = "https://openlibrary.org" + bookKey + "/editions.json";

        HttpGet httpGet = new HttpGet(url);
        URI uri = new URIBuilder(httpGet.getUri())
                .build();

        httpGet.setUri(uri);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            ObjectMapper mapper = new ObjectMapper();
            Object result = httpClient.execute(httpGet, response ->
                    mapper.readValue(
                            EntityUtils.toString(response.getEntity()),
                            Object.class
                    )
            );

            Map<String, Object> resultMap = mapper.convertValue(result, Map.class);

            List<Map<String, Object>> entries = (List<Map<String, Object>>) resultMap.get("entries");

            for(Map<String, Object> map : entries){
                String authorKeyWithoutUrl = "";
                if(map.get("authors") != null){
                    List<Map<String, Object>> authors = (List<Map<String, Object>>) map.get("authors");
                    String authorKeyFromRequest = authors.get(0).get("key").toString();
                    authorKeyWithoutUrl = authorKeyFromRequest.replace("/authors/", "");
                }


                String bookTitle = map.get("title").toString();

                if(authorKeyWithoutUrl.equals(authorKey) && bookTitle.equals(bookDto.getTitle())){
                    bookDto.setPublisher(((List<String>) map.get("publishers")).get(0).toString());

                    int numberOfPages = (int) map.get("number_of_pages");
                    bookDto.setPages(numberOfPages);
                }
            }
        }
    }

    private void requestRatings(String bookKey, BookDto bookDto) throws URISyntaxException, IOException{
        String url = "https://openlibrary.org" + bookKey + "/ratings.json";

        HttpGet httpGet = new HttpGet(url);
        URI uri = new URIBuilder(httpGet.getUri())
                .build();

        httpGet.setUri(uri);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            ObjectMapper mapper = new ObjectMapper();
            Object result = httpClient.execute(httpGet, response ->
                    mapper.readValue(
                            EntityUtils.toString(response.getEntity()),
                            Object.class
                    )
            );

            Map<String, Object> resultMap = mapper.convertValue(result, Map.class);

            Map<String, Object> summary = mapper.convertValue(resultMap.get("summary"), Map.class);

            double averageRating = (Double) summary.get("average");

            bookDto.setRating(averageRating);
        }
    }

}
