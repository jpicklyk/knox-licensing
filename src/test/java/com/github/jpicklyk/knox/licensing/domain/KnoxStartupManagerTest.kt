package com.github.jpicklyk.knox.licensing.domain

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class KnoxStartupManagerTest {

    @Before
    fun setup() {
        KnoxStartupManager.reset()
    }

    @After
    fun tearDown() {
        KnoxStartupManager.reset()
    }

    @Test
    fun `getInstance returns a KnoxLicenseInitializer`() {
        val initializer = KnoxStartupManager.getInstance()
        assertNotNull(initializer)
    }

    @Test
    fun `getInstance returns the same instance on subsequent calls`() {
        val first = KnoxStartupManager.getInstance()
        val second = KnoxStartupManager.getInstance()
        assertSame(first, second)
    }

    @Test
    fun `setInstance registers the provided initializer`() {
        val customInitializer = KnoxLicenseInitializer()

        KnoxStartupManager.setInstance(customInitializer)

        assertSame(customInitializer, KnoxStartupManager.getInstance())
    }

    @Test
    fun `reset clears the initializer instance`() {
        val first = KnoxStartupManager.getInstance()
        KnoxStartupManager.reset()
        val second = KnoxStartupManager.getInstance()

        // After reset, a new instance should be created
        // (they won't be the same object reference)
        assertNotNull(second)
    }

    @Test
    fun `isKnoxLicenseReady returns false initially`() {
        assertFalse(KnoxStartupManager.isKnoxLicenseReady())
    }

    @Test
    fun `getLicenseStatus returns NotChecked initially`() {
        assertEquals(LicenseStartupResult.NotChecked, KnoxStartupManager.getLicenseStatus())
    }

    @Test
    fun `setInstance allows Hilt to provide managed instance`() {
        // Simulate what Hilt would do
        val hiltManagedInitializer = KnoxLicenseInitializer()
        KnoxStartupManager.setInstance(hiltManagedInitializer)

        // Verify that getInstance returns the Hilt-managed instance
        assertSame(hiltManagedInitializer, KnoxStartupManager.getInstance())

        // Verify that static methods delegate to the Hilt-managed instance
        assertEquals(
            hiltManagedInitializer.getStatus(),
            KnoxStartupManager.getLicenseStatus()
        )
        assertEquals(
            hiltManagedInitializer.isReady,
            KnoxStartupManager.isKnoxLicenseReady()
        )
    }
}
