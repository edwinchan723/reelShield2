package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BlockEvent
import com.example.data.PreferencesManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ReelShield", appName)
  }

  @Test
  fun `default preferences allow reels in chat and enable protection`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = PreferencesManager(context)
    assertTrue("Protection should be enabled by default", prefs.isProtectionEnabled())
    assertTrue("Reels in chat should be allowed by default", prefs.allowReelsInChat())
  }

  @Test
  fun `create block event entity with default saved seconds`() {
    val event = BlockEvent(
      eventType = "REELS_TAB_BLOCKED",
      source = "Instagram App",
      details = "Tapped reels tab"
    )
    assertEquals(900, event.estimatedSecondsSaved)
    assertEquals("REELS_TAB_BLOCKED", event.eventType)
  }
}
