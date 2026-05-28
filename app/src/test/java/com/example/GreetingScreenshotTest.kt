package com.example

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider
import com.example.ui.ChatFlowViewModel
import com.example.ui.LandingAuthScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = ChatFlowViewModel(application)
    composeTestRule.setContent {
      MyApplicationTheme {
        LandingAuthScreen(viewModel = viewModel)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }

  @Test
  fun dashboard_screenshot() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = ChatFlowViewModel(application)
    viewModel.login("test@chatflow.ai", "Test User")
    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.MainHubView(viewModel = viewModel)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/dashboard.png")
  }

  @Test
  fun connection_screen_test() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = ChatFlowViewModel(application)
    viewModel.login("test@chatflow.ai", "Test User")
    viewModel.currentScreen = com.example.ui.AppScreen.Connection
    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.MainHubView(viewModel = viewModel)
      }
    }
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/connection.png")
  }

  @Test
  fun live_chats_screen_test() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = ChatFlowViewModel(application)
    viewModel.login("test@chatflow.ai", "Test User")
    viewModel.currentScreen = com.example.ui.AppScreen.LiveChats
    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.MainHubView(viewModel = viewModel)
      }
    }
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/live_chats.png")
  }

  @Test
  fun automation_rules_screen_test() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = ChatFlowViewModel(application)
    viewModel.login("test@chatflow.ai", "Test User")
    viewModel.currentScreen = com.example.ui.AppScreen.AutomationRules
    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.MainHubView(viewModel = viewModel)
      }
    }
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/automation_rules.png")
  }

  @Test
  fun bulk_messaging_screen_test() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = ChatFlowViewModel(application)
    viewModel.login("test@chatflow.ai", "Test User")
    viewModel.currentScreen = com.example.ui.AppScreen.BulkMessaging
    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.MainHubView(viewModel = viewModel)
      }
    }
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/bulk_messaging.png")
  }
}
