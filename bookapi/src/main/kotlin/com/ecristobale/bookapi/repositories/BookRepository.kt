package com.ecristobale.bookapi.repositories

import com.ecristobale.bookapi.models.Book
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Long> {
}