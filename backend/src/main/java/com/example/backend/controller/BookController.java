package com.example.backend.controller;

import com.example.backend.model.Book;
import com.example.backend.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookRepository bookRepo;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return bookRepo.save(book);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        return bookRepo.findById(id).map(book -> {
            book.setTitle(updatedBook.getTitle());
            book.setAuthor(updatedBook.getAuthor());
            book.setDescription(updatedBook.getDescription());
            book.setIsbn(updatedBook.getIsbn());
            book.setPublishedDate(updatedBook.getPublishedDate());
            return bookRepo.save(book);
        }).orElseThrow(() -> new RuntimeException("Book not found"));
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookRepo.deleteById(id);
    }
}
