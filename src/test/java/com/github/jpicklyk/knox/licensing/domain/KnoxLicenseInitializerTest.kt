package com.github.jpicklyk.knox.licensing.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class KnoxLicenseInitializerTest {

    private lateinit var initializer: KnoxLicenseInitializer

    @Before
    fun setup() {
        initializer = KnoxLicenseInitializer()
    }

    @After
    fun tearDown() {
        initializer.reset()
    }

    @Test
    fun `initial status is NotChecked`() = runTest {
        val status = initializer.licenseStatus.first()
        assertEquals(LicenseStartupResult.NotChecked, status)
    }

    @Test
    fun `isInitialized returns false initially`() {
        assertFalse(initializer.isInitialized)
    }

    @Test
    fun `isReady returns false initially`() {
        assertFalse(initializer.isReady)
    }

    @Test
    fun `getStatus returns NotChecked initially`() {
        assertEquals(LicenseStartupResult.NotChecked, initializer.getStatus())
    }

    @Test
    fun `reset returns status to NotChecked`() = runTest {
        // We can't fully test initialization without Knox SDK,
        // but we can verify reset behavior
        initializer.reset()

        assertEquals(LicenseStartupResult.NotChecked, initializer.getStatus())
        assertFalse(initializer.isInitialized)
        assertFalse(initializer.isReady)
    }
}
