package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import java.util.Map;

@Controller
public class BookController {

    BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String getMain(Model model) {
        model.addAttribute("books", bookService.returnAllBooks());
        return "main";
    }

    @ResponseBody
    @PostMapping("/add-book")
    public ResponseEntity<String> addBookPost(@RequestBody Book book) {
        boolean nwBook = bookService.createNewBook(book);
        String res = nwBook ? "The book was added successfully" : "The book with such ISBN already exists. It should be unique. Try again.";
        int status = nwBook ? 200 : 400;
        return ResponseEntity.status(status).body(res);
    }

    @ResponseBody
    @GetMapping("/get-books")
    public ResponseEntity<Collection<Book>> searchBook(@RequestParam(name = "getBy", required = false) String search) {
       if(search == null) {
            return ResponseEntity.status(200)
                                 .header("h1","Getting all books")
                                 .body(bookService.returnAllBooks().values());
       }
       Map<String,Book> books = bookService.findBooks(search);
       return ResponseEntity.ok(books.values());
   }
}
