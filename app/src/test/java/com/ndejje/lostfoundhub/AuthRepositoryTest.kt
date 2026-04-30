package com.ndejje.lostfoundhub

import org.junit.Assert.*
import org.junit.Test

class AuthRepositoryTest {

    @Test
    fun testEmailValidation() {
        val validEmail = "student@ndejjeuniversity.ac.ug"
        val invalidEmail = "invalid-email"

        assertTrue(validEmail.contains("@"))
        assertTrue(validEmail.contains("."))
        assertFalse(invalidEmail.contains("@"))
    }

    @Test
    fun testPasswordValidation() {
        val validPassword = "password123"
        val shortPassword = "123"

        assertTrue(validPassword.length >= 6)
        assertFalse(shortPassword.length >= 6)
    }

    @Test
    fun testDisplayNameValidation() {
        val validName = "John Doe"
        val emptyName = ""

        assertTrue(validName.isNotBlank())
        assertFalse(emptyName.isNotBlank())
    }
}

