package com.ecristobale.bookapi.controllers

import com.ecristobale.bookapi.models.Book
import com.ecristobale.bookapi.repositories.BookRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@AutoConfigureMockMvc // auto-magically configures and enables an instance of MockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// Why configure Mockito manually when a JUnit 5 test extension already exists for that very purpose?
//@ExtendWith(SpringExtension::class, MockitoExtension::class)
class BookControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var bookRepository: BookRepository
    var book: Book? = null;

    @BeforeEach
    fun setup() {
        val book = Book(null, "StartBookTitle", author = "StartBookAuthor", publishYear = 2000)
        this.book = bookRepository.save(book)
    }

    @Test
    fun `Test getBookById in the happy path scenario`() {
        mockMvc.get("$BOOKS_PATH/{bookId}", book?.id) {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { jsonPath("$.id") { exists() } }
            content { jsonPath("$.id") { isNotEmpty() } }
            content { jsonPath("$.id") { value(book?.id) } }
            content { json("""{"id":""" + book?.id+""","title":"StartBookTitle","author":"StartBookAuthor","publishYear":2000}""") }
        }
//        verify(bookRepository, times(1)).findById(1)
    }

    @Test
    fun `Test createBook in the happy path scenario`() {
        val book = Book(null, "BookTitle", author = "BookAuthor", publishYear = 2025)
        mockMvc.post(BOOKS_PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(book)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { jsonPath("$.id") { exists() } }
            content { jsonPath("$.id") { isNotEmpty() } }
            content { jsonPath("$.id") { value(this@BookControllerTest.book?.id?.plus(1)) } }
            content { json("""{"id":2,"title":"BookTitle","author":"BookAuthor","publishYear":2025}""") }
        }
//        verify(bookRepository, times(1)).save(book)
    }
}