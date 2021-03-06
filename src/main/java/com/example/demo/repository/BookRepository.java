package com.example.demo.repository;

import com.example.demo.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class BookRepository {
    private static final Map<String, Book> BOOK_DATABASE = initDatabase();

    public Book saveNewBook(final Book newBook) {
        log.info("Creating new book: {}", newBook);

        final Book book =  Book.builder()
                .isbn(newBook.getIsbn())
                .title(newBook.getTitle())
                .author(newBook.getAuthor())
                .build();

        BOOK_DATABASE.put(book.getIsbn(), book);
        return book;
    }


    public Map<String,Book> returnBooks(){
        return BOOK_DATABASE;
    }

    public boolean isIsbnExists(final String isbn) {
        log.info("Check that book with isbn: {} exists", isbn);

        return BOOK_DATABASE.containsKey(isbn);
    }

    private static Map<String, Book> initDatabase() {
        final Map<String, Book> database = new HashMap<>();
        database.put("isbn1", Book.builder().isbn("isbn1").title("Pride and Prejudice").author("Jane Austen").build());
        database.put("isbn2", Book.builder().isbn("isbn2").title("Da Vinci Code").author("Dan Braun").build());
        database.put("isbn3", Book.builder().isbn("isbn3").title("Harry Potter and the Deathly Hallows").author("J. K. Rowling").build());
        return database;
    }

}
