package com.example.demo.validation;


import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookValidator {
    private final BookRepository bookRepository;

    public boolean validateNewBook(final Book newBook) {
        if (bookRepository.isIsbnExists(newBook.getIsbn())) {
            return false;
        }
        return true;
    }

}
