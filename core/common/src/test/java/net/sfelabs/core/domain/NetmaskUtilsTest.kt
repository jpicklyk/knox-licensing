package net.sfelabs.core.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class NetmaskUtilsTest {

    @Test
    fun testPrefixToNetmask() {
        assertEquals(
            "255.255.255.255",
            NetmaskUtils.prefixToNetmask(32)
        )
        assertEquals(
            "255.255.255.0",
            NetmaskUtils.prefixToNetmask(24)
        )
        assertEquals(
            "255.255.0.0",
            NetmaskUtils.prefixToNetmask(16)
        )
        assertEquals(
            "255.0.0.0",
            NetmaskUtils.prefixToNetmask(8)
        )
        assertEquals(
            "0.0.0.0",
            NetmaskUtils.prefixToNetmask(0)
        )

        // Less common prefixes
        assertEquals(
            "255.255.255.248",
            NetmaskUtils.prefixToNetmask(29)
        )
        assertEquals(
            "255.255.255.252",
            NetmaskUtils.prefixToNetmask(30)
        )
        assertEquals(
            "255.255.255.254",
            NetmaskUtils.prefixToNetmask(31)
        )
        assertEquals(
            "255.255.128.0",
            NetmaskUtils.prefixToNetmask(17)
        )
        assertEquals(
            "255.255.224.0",
            NetmaskUtils.prefixToNetmask(19)
        )
        assertEquals(
            "255.255.240.0",
            NetmaskUtils.prefixToNetmask(20)
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun testPrefixToNetmaskInvalidInput() {
        NetmaskUtils.prefixToNetmask(33)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testPrefixToNetmaskNegativeInput() {
        NetmaskUtils.prefixToNetmask(-1)
    }

    @Test
    fun testNetmaskToPrefix() {
        assertEquals(32, NetmaskUtils.netmaskToPrefix("255.255.255.255"))
        assertEquals(24, NetmaskUtils.netmaskToPrefix("255.255.255.0"))
        assertEquals(16, NetmaskUtils.netmaskToPrefix("255.255.0.0"))
        assertEquals(8, NetmaskUtils.netmaskToPrefix("255.0.0.0"))
        assertEquals(0, NetmaskUtils.netmaskToPrefix("0.0.0.0"))

        // Less common netmasks
        assertEquals(29, NetmaskUtils.netmaskToPrefix("255.255.255.248"))
        assertEquals(30, NetmaskUtils.netmaskToPrefix("255.255.255.252"))
        assertEquals(31, NetmaskUtils.netmaskToPrefix("255.255.255.254"))
        assertEquals(17, NetmaskUtils.netmaskToPrefix("255.255.128.0"))
        assertEquals(19, NetmaskUtils.netmaskToPrefix("255.255.224.0"))
        assertEquals(20, NetmaskUtils.netmaskToPrefix("255.255.240.0"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testNetmaskToPrefixInvalidInput() {
        NetmaskUtils.netmaskToPrefix("255.255.255")
    }

    @Test(expected = IllegalArgumentException::class)
    fun testNetmaskToPrefixInvalidFormat() {
        NetmaskUtils.netmaskToPrefix("255.256.255.0")
    }

    @Test(expected = IllegalArgumentException::class)
    fun testNetmaskToPrefixNonContiguousOnes() {
        NetmaskUtils.netmaskToPrefix("255.255.0.255")
    }

    @Test
    fun testBidirectionalConversion() {
        for (i in 0..32) {
            val netmask = NetmaskUtils.prefixToNetmask(i)
            val prefix = NetmaskUtils.netmaskToPrefix(netmask)
            assertEquals(i, prefix)
        }
    }

    @Test
    fun testEdgeCases() {
        // Test boundary values
        assertEquals("128.0.0.0", NetmaskUtils.prefixToNetmask(1))
        assertEquals("192.0.0.0", NetmaskUtils.prefixToNetmask(2))
        assertEquals("255.255.255.254", NetmaskUtils.prefixToNetmask(31))

        // Test corresponding netmask to prefix conversions
        assertEquals(1, NetmaskUtils.netmaskToPrefix("128.0.0.0"))
        assertEquals(2, NetmaskUtils.netmaskToPrefix("192.0.0.0"))
        assertEquals(31, NetmaskUtils.netmaskToPrefix("255.255.255.254"))
    }
}