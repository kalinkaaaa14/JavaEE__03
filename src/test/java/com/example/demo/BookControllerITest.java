package com.example.demo;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class  BookControllerITest {
    @Autowired
    private BookRepository bookRepository;

    @LocalServerPort
    void setPort(int port) {
        RestAssured.port = port;
    }

    @BeforeEach
    void initDatabase() {
        bookRepository.saveNewBook(new Book("isbn1","The Town","Chk Hogan"));
        bookRepository.saveNewBook(new Book("isbn2","The Little Prince","Antoine De Saint-Exupery"));
        bookRepository.saveNewBook(new Book("isbn3","Getting Things Done","David Allen"));
        bookRepository.saveNewBook(new Book("isbn4","The Town 2","Chk Hogan"));
    }

    @Test
    public void shouldGetBooks(){
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/get-books")
                .then()
                .statusCode(200)
                .header("h1","Getting all books");
    }

    @Test
    public void shouldGetBookByIsbn() {
        Book[] books = RestAssured
                .given()
                .queryParam("getBy", "isbn2")
                .contentType(ContentType.JSON)
                .when()
                .get("/get-books")
                .then()
                .statusCode(200)
                .extract().body().as(Book[].class);
        for (Book book:books) {
            assertEquals(book.getIsbn(), "isbn2");
            assertEquals(book.getTitle(), "The Little Prince");
            assertEquals(book.getAuthor(), "Antoine De Saint-Exupery");
        }
    }

    @Test
    public void shouldGetBookByTitle(){
        Book[] books = RestAssured
                .given()
                .queryParam("getBy", "the town")
                .contentType(ContentType.JSON)
                .when()
                .get("/get-books")
                .then()
                .statusCode(200)
                .extract().body().as(Book[].class);
        for (Book book:books) {
            assertTrue(book.getIsbn().equals("isbn1")||book.getIsbn().equals("isbn4"));
            assertTrue(book.getTitle().equals("The Town") || book.getTitle().equals("The Town 2"));
            assertTrue(book.getAuthor().equals("Chk Hogan"));
        }
    }
    @Test
    public void shouldGetBookByAuthor(){
        Book[] books = RestAssured
                .given()
                .queryParam("getBy", "David Allen")
                .contentType(ContentType.JSON)
                .when()
                .get("/get-books")
                .then()
                .statusCode(200)
                .extract().body().as(Book[].class);
        for (Book book:books) {
            assertEquals(book.getIsbn(), "isbn3");
            assertEquals(book.getTitle(), "Getting Things Done");
        }
    }

    @Test
    public void shouldNotFoundBook(){
        Book[] books = RestAssured
                .given()
                .queryParam("getBy", "wrong input")
                .contentType(ContentType.JSON)
                .when()
                .get("/get-books")
                .then()
                .statusCode(200)
                .extract().body().as(Book[].class);
        assertEquals(books.length, 0);
    }

    @ParameterizedTest
    @MethodSource("shouldAddBooksSuccessfullyDataProvider")
    public void shouldAddBooksSuccessfully(String isbn, String title, String author){
        Book book = Book.builder().isbn(isbn).title(title).author(author).build();

        RestAssured
                .given()
                .body(book)
                .contentType(ContentType.JSON)
                .when()
                .post("/add-book")
                .then()
                .statusCode(200)
                .body(CoreMatchers.is("The book was added successfully"));
    }

    @ParameterizedTest
    @MethodSource("shouldFailDuringAddingBooksDataProvider")
    public void shouldFailDuringAddingBooks(String isbn, String title, String author){
        Book book = Book.builder().isbn(isbn).title(title).author(author).build();

        RestAssured
                .given()
                .body(book)
                .contentType(ContentType.JSON)
                .when()
                .post("/add-book")
                .then()
                .statusCode(400)
                .body(CoreMatchers.is("The book with such ISBN already exists. It should be unique. Try again."));
    }

    private static Stream<Arguments> shouldAddBooksSuccessfullyDataProvider() {
        return Stream.of(
                Arguments.of("isbn10","Breathe","Cat Whetherill"),
                Arguments.of("isbn11","Peter Pan", "James Barrie"),
                Arguments.of("isbn12","The book thief", "Markus Zusak")
        );
    }
    private static Stream<Arguments> shouldFailDuringAddingBooksDataProvider() {
        return Stream.of(
                Arguments.of("isbn2","Breathe","Cat Whetherill"),
                Arguments.of("isbn1","Peter Pan", "James Barrie")
        );
    }
}
