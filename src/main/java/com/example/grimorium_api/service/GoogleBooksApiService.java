package com.example.grimorium_api.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GoogleBooksApiService {

    @Value("${google.api.key}")
    private String key;
    private String url = "https://www.googleapis.com/books/v1/volumes";

    public Object searchBookFromGoogleBooksApi(String book) throws URISyntaxException, IOException {

        book = book.replaceAll("\\s+","+");

        HttpGet httpGet = new HttpGet(url);
        URI uri = new URIBuilder(httpGet.getUri())
                .addParameter("q", book)
                .addParameter("langRestrict", "pt")
                .addParameter("maxRrsults", "5")
                .addParameter("key", key)
                .build();

        httpGet.setUri(uri);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            ObjectMapper mapper = new ObjectMapper();
            return httpClient.execute(httpGet, response ->
                    mapper.readValue(
                            EntityUtils.toString(response.getEntity()),
                            Object.class
                    )
            );
        }
    }

   
}
