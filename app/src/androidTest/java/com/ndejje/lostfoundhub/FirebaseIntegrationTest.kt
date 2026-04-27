package com.ndejje.lostfoundhub

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class FirebaseIntegrationTest {

    @Before
    fun setup() {
        // Optional: Configure Firebase Emulator if needed
        // FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
        // FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000)
    }

    @Test
    fun testFirebaseAuthInitialized() {
        val auth = FirebaseAuth.getInstance()
        assertNotNull(auth)
    }

    @Test
    fun testFirebaseDatabaseInitialized() {
        val database = FirebaseDatabase.getInstance()
        assertNotNull(database)
    }
}
