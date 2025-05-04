package com.ecristobale.bookapi.services

import com.ecristobale.bookapi.models.Book
import com.ecristobale.bookapi.repositories.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BookService(private val bookRepository: BookRepository) {

    // Kotlin's expression bodies
    fun findAll() = bookRepository.findAll()

    fun findById(id: Long) = bookRepository.findById(id)
        .orElseThrow { NoSuchElementException("Book not found with ID: $id") }

    fun create(book: Book) = bookRepository.save(book)

    fun update(id: Long, book: Book): Book {
        return if (bookRepository.existsById(id)) {
            bookRepository.save(book.copy(id = id)) // copy() function provided by the data class for updating entities
        } else {
            throw NoSuchElementException("Book not found with ID: $id")
        }
    }

    fun deleteById(id: Long) {
        if (!bookRepository.existsById(id)) {
            throw NoSuchElementException("Book not found with ID: $id")
        }
        bookRepository.deleteById(id)
    }
}