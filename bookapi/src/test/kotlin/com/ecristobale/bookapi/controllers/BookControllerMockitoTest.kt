package com.ecristobale.bookapi.controllers

import com.ecristobale.bookapi.models.Book
import com.ecristobale.bookapi.repositories.BookRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.Optional

@AutoConfigureMockMvc // auto-magically configures and enables an instance of MockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerMockitoTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var bookRepository: BookRepository

    var book: Book = Book(1L, "StartBookTitle", author = "StartBookAuthor", publishYear = 2000)


    @Test
    fun `Test getAllBooks with Kotlin-Mockito in positive scenario`() {
        // use mockito-kotlin for a more idiomatic way of setting up your test expectations
        whenever(bookRepository.findAll()).thenAnswer {
            listOf(book)
        }
        mockMvc.get(BOOKS_PATH, 1) {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { jsonPath("$.length") { equals(1) } }
        }
        verify(bookRepository, times(1)).findAll()
    }

    @Test
    fun `Test getBookById with Kotlin-Mockito in positive scenario`() {
        // use mockito-kotlin for a more idiomatic way of setting up your test expectations
        whenever(bookRepository.findById(1)).thenAnswer {
//            it.arguments.first()
            Optional.of(book)
        }
        mockMvc.get("$BOOKS_PATH/{bookId}", 1) {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { jsonPath("$.id") { exists() } }
            content { jsonPath("$.id") { isNotEmpty() } }
            content { jsonPath("$.id") { value(book.id) } }
            content { json("""{"id":""" + book.id +""","title":"StartBookTitle","author":"StartBookAuthor","publishYear":2000}""") }
        }
        verify(bookRepository, times(1)).findById(1)
    }

    @Test
    fun `Test getBookById negative scenario`() {
        doThrow(NoSuchElementException()).whenever(bookRepository).findById(7)
        mockMvc.get("$BOOKS_PATH/{bookId}", 7) {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { jsonPath("$.id") { doesNotExist() } }
            content { jsonPath("$.error") { exists() } }
            content { jsonPath("$.error") { isNotEmpty() } }
            content { jsonPath("$.error") { value("Not Found") } }
            content { json("""{"error":"Not Found"}""") }
        }
        verify(bookRepository, times(1)).findById(7)
    }

    @Test
    fun `Test createBook in positive scenario`() {
        val createBook = Book(null, "BookTitle", author = "BookAuthor", publishYear = 2025)
        whenever(bookRepository.save<Book>(createBook)).thenAnswer {
            val book = it.arguments.first() as Book
            book.copy(id = 2)
        }
        mockMvc.post(BOOKS_PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(createBook)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { jsonPath("$.id") { exists() } }
            content { jsonPath("$.id") { isNotEmpty() } }
            content { jsonPath("$.id") { value(2) } }
            content { json("""{"id":2,"title":"BookTitle","author":"BookAuthor","publishYear":2025}""") }
        }
        verify(bookRepository, times(1)).save(createBook)
    }

    @Test
    fun `Test deleteBookById with Kotlin-Mockito positive scenario`() {
        // use mockito-kotlin for a more idiomatic way of setting up your test expectations
        whenever(bookRepository.existsById(any())).thenAnswer{true}
        whenever(bookRepository.deleteById(any())).thenAnswer{}
        mockMvc.delete("$BOOKS_PATH/{bookId}", 1) {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }
        verify(bookRepository, times(1)).deleteById(1)
    }

    @Test
    fun `Test deleteBookById with Kotlin-Mockito negative scenario`() {
        val bookId = 1L
        // use mockito-kotlin for a more idiomatic way of setting up your test expectations
        doThrow(NoSuchElementException("Book not found with ID: $bookId")).whenever(bookRepository).existsById(bookId)
        whenever(bookRepository.deleteById(any())).thenAnswer{}
        mockMvc.delete("$BOOKS_PATH/{bookId}", bookId) {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { jsonPath("$.id") { doesNotExist() } }
            content { jsonPath("$.error") { exists() } }
            content { jsonPath("$.error") { isNotEmpty() } }
            content { jsonPath("$.error") { value("Book not found with ID: $bookId") } }
            content { json("""{"error":"Book not found with ID: $bookId"}""") }
        }

        verify(bookRepository, times(1)).existsById(bookId)
        verify(bookRepository, times(0)).deleteById(bookId)
    }

    @Test
    fun `Test updateBook with Kotlin-Mockito positive scenario`() {
        val bookId = 1L
        // Configure the mock to return the existing book when findById is called
        whenever(bookRepository.save(book)).thenReturn(book)
        whenever(bookRepository.existsById(bookId)).thenAnswer{true}

        // Convert the updated book to JSON
        val json = ObjectMapper().writeValueAsString(book)

        // Perform the PUT request
        mockMvc.perform(
            MockMvcRequestBuilders.put("$BOOKS_PATH/{bookId}", book.id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(json))

        // Verify that save was called with the updated book
        verify(bookRepository, times(1)).save(eq(book))
    }

    @Test
    fun `Test updateBook with Kotlin-Mockito negative scenario`() {
        val bookId = 1L
        doThrow(NoSuchElementException("Book not found with ID: $bookId")).whenever(bookRepository).existsById(bookId)

        // Convert the updated book to JSON
        val json = ObjectMapper().writeValueAsString(book)

        // Perform the PUT request
        mockMvc.perform(MockMvcRequestBuilders.put("$BOOKS_PATH/{bookId}", book.id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound)

        // Verify that the service method was called
        verify(bookRepository, times(0)).save(eq(book))
    }
}