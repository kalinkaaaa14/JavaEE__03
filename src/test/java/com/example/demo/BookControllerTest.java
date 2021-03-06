package com.example.demo;

import com.example.demo.controller.BookController;
import com.example.demo.model.Book;
import com.example.demo.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@WebMvcTest(BookController.class)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookService bookService;

    private static Map<String, Book> database = new HashMap<>();

    @BeforeEach
    private  void initDatabase() {
        database.put("isbn1", Book.builder().isbn("isbn1").title("Pride and Prejudice").author("Jane Austen").build());
        database.put("isbn2", Book.builder().isbn("isbn2").title("Da Vinci Code").author("Dan Braun").build());
        database.put("isbn3", Book.builder().isbn("isbn3").title("Harry Potter and the Deathly Hallows").author("J. K. Rowling").build());
    }

    @Test
    void shouldGetBooks() throws Exception {
        String expected = objectMapper.writeValueAsString(database.values());
        Mockito.when(bookService.returnAllBooks()).thenReturn(database);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/get-books").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("h1","Getting all books"))
                .andExpect(MockMvcResultMatchers.content().json(expected));
        Mockito.verify(bookService, Mockito.times(1)).returnAllBooks();
    }

    @Test
    public void shouldGetBookByTitle() throws Exception {
        String search = "town";
        Book book = new Book("isbn4","The Town 2","Chk Hogan");
        HashMap<String,Book> bookHashMap = new HashMap<>();
        bookHashMap.put("isbn11",book);

        String expected = objectMapper.writeValueAsString(bookHashMap.values());
        Mockito.when(bookService.findBooks(search)).thenReturn(bookHashMap);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/get-books").queryParam("getBy",search)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expected));
        Mockito.verify(bookService, Mockito.times(1)).findBooks(search);
    }

    @ParameterizedTest
    @MethodSource("addBooksDataProvider")
    public void shouldAddBooksSuccessfully(String isbn, String title, String author) throws Exception {
        Book book = Book.builder().isbn(isbn).title(title).author(author).build();
        String bookjson = objectMapper.writeValueAsString(book);

        Mockito
                .when(bookService.createNewBook(book))
                .thenReturn(true);


        mockMvc.perform(MockMvcRequestBuilders
                .post("/add-book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookjson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("The book was added successfully"));
    }

    @ParameterizedTest
    @MethodSource("addBooksDataProvider")
    public void shouldFailDuringAddingBooks(String isbn, String title, String author) throws Exception {
        Book book = Book.builder().isbn(isbn).title(title).author(author).build();
        String bookjson = objectMapper.writeValueAsString(book);

        Mockito
                .when(bookService.createNewBook(book))
                .thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/add-book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookjson))
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().string("The book with such ISBN already exists. It should be unique. Try again."));
    }

    private static Stream<Arguments> addBooksDataProvider() {
        return Stream.of(
                Arguments.of("isbn10","Breathe","Cat Whetherill"),
                Arguments.of("isbn11","Peter Pan", "James Barrie"),
                Arguments.of("isbn12","The book thief", "Markus Zusak")
        );
    }
}
