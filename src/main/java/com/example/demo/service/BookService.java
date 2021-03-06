package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import com.example.demo.validation.BookValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookValidator bookValidator;

    public boolean createNewBook(final Book newBook) {
        log.info("Try to create new book: {}", newBook.getIsbn());
        boolean validatorResult = bookValidator.validateNewBook(newBook);
        if(validatorResult) {
            final Book book = bookRepository.saveNewBook(newBook);
            log.info("New book is created: {}", book);
        }
        return validatorResult;
    }

    public Map<String,Book> returnAllBooks(){
        return  bookRepository.returnBooks();
    }

    public Map<String,Book> findBooks(final String search){
      Map<String,Book> allbooks = returnAllBooks();
        Map<String, Book> filteredMap = new HashMap<>();
        for (Map.Entry<String, Book> entry : allbooks.entrySet()) {
            Book book = entry.getValue();
            if (book.getIsbn().toLowerCase().contains(search.toLowerCase()) ||
                book.getTitle().toLowerCase().contains(search.toLowerCase()) ||
                book.getAuthor().toLowerCase().contains(search.toLowerCase())){
                filteredMap.put(entry.getKey(), book);
                System.out.println(filteredMap);
            }
        }

        System.out.println(filteredMap);
        return filteredMap;
    }
}

