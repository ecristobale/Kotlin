package com.ecristobale.bookapi.services

import com.ecristobale.bookapi.models.Book
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class BookServiceTest {

    @Autowired
    private lateinit var bookService: BookService

    // using backticks for spaces
    @Test
    fun `should create new book`() {
        val book = Book(
            title = "The Kotlin Programming Language",
            author = "JetBrains",
            publishYear = 2021
        )

        val savedBook = bookService.create(book)

        assertNotNull(savedBook.id)
        assertEquals(book.title, savedBook.title)
        assertEquals(book.author, savedBook.author)
        assertEquals(book.publishYear, savedBook.publishYear)
    }

    @Test
    fun `should throw exception when book not found`() {
        assertThrows(NoSuchElementException::class.java) {
            bookService.findById(-1)
        }
    }
}