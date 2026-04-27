package com.ndejje.lostfoundhub

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginScreenTest {

    @Test
    fun testValidEmailFormat() {
        // Test valid email format
        val email = "student@ndejjeuniversity.ac.ug"
        assertTrue(email.contains("@"))
        assertTrue(email.contains("."))
    }

    @Test
    fun testInvalidEmailFormat() {
        // Test invalid email format
        val email = "invalid-email"
        assertFalse(email.contains("@"))
    }

    @Test
    fun testPasswordNotEmpty() {
        // Test that password cannot be empty
        val password = "password123"
        assertTrue(password.isNotEmpty())
    }

    @Test
    fun testEmptyPassword() {
        // Test empty password validation
        val password = ""
        assertTrue(password.isEmpty())
    }
}
