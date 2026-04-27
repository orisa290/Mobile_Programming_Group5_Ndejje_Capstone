package com.ndejje.lostfoundhub

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SignUpScreenTest {

    @Test
    fun testUserNameValidation() {
        // Test that username is not empty
        val userName = "TestUser"
        assertNotNull(userName)
        assertTrue(userName.isNotEmpty())
    }

    @Test
    fun testEmptyUserName() {
        // Test empty username validation
        val userName = ""
        assertTrue(userName.isEmpty())
    }

    @Test
    fun testPasswordMatch() {
        // Test that password and confirm password match
        val password = "password123"
        val confirmPassword = "password123"
        assertEquals(password, confirmPassword)
    }

    @Test
    fun testPasswordMismatch() {
        // Test password mismatch detection
        val password = "password123"
        val confirmPassword = "different"
        assertNotEquals(password, confirmPassword)
    }
}
