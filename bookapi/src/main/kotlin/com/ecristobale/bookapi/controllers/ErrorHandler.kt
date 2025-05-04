package com.ecristobale.bookapi.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ErrorHandler {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<Map<String, String>> =
        ResponseEntity(mapOf("error" to (e.message ?: "Not Found")), HttpStatus.NOT_FOUND)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(e: MethodArgumentNotValidException): ResponseEntity<Map<String, List<String>>> {
        val errors = e.bindingResult.fieldErrors
            .map { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity(mapOf("errors" to errors), HttpStatus.BAD_REQUEST)
    }
}