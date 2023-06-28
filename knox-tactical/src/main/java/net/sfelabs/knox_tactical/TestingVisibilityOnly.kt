package net.sfelabs.knox_tactical

import java.lang.annotation.RetentionPolicy

@RequiresOptIn(message = "This part of the API is visible only for testing.", )
@Retention(AnnotationRetention.BINARY)
annotation class TestingVisibilityOnly
