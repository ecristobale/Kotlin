package com.ecristobale.bookapi.controllers

import com.ecristobale.bookapi.models.Book
import com.ecristobale.bookapi.services.BookService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

const val BOOKS_PATH: String = "/api/books"

@RestController
@RequestMapping(BOOKS_PATH)
class BookController(private val bookService: BookService) {

    @GetMapping
    fun getAllBooks(): List<Book> = bookService.findAll()

    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: Long): Book = bookService.findById(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createBook(@Valid @RequestBody book: Book): Book = bookService.create(book)

    @PutMapping("/{id}")
    fun updateBook(
        @PathVariable id: Long,
        @Valid @RequestBody book: Book
    ): Book = bookService.update(id, book)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBookById(@PathVariable id: Long) = bookService.deleteById(id)
}