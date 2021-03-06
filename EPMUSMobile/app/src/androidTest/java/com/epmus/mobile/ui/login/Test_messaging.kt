package com.epmus.mobile.ui.login


import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.epmus.mobile.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class Test_messaging {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun test_messagerie() {
        try {
            onView(withContentDescription("More options")).check(matches(isDisplayed()))
            onView(withContentDescription("More options")).perform(click())
            onView(withText("Déconnexion")).check(matches(isDisplayed()))
            onView(withText("Déconnexion")).perform(click())
            Thread.sleep(1000)
        } catch (e: Exception) {

        } finally {
            // Connection
            onView(withId(R.id.loginDisabled)).check(matches(isDisplayed()))
            onView(withId(R.id.username)).perform(typeText("a@a.com"))
            onView(withId(R.id.password)).perform(typeText("aaaaaa"))
            onView(withId(R.id.login)).perform(click())
            Thread.sleep(1000)
            // Access to messaging + send a message
            onView(withId(R.id.activity_messaging)).check(matches(isDisplayed()))
            onView(withId(R.id.activity_messaging)).perform(click())
            onView(withId(R.id.recyclerview_newmessage)).check(matches(isDisplayed()))
            onView(withId(R.id.recyclerview_newmessage)).perform(actionOnItemAtPosition<ViewHolder>(0, click()))
            onView(withId(R.id.send_button_chat_log)).check(matches(isDisplayed()))
            onView(withId(R.id.Text_chat_log)).perform(typeText("test"))
            onView(withId(R.id.send_button_chat_log)).perform(click())
            // Disconnection
            pressBack()
            pressBack()
            onView(withContentDescription("More options")).check(matches(isDisplayed()))
            onView(withContentDescription("More options")).perform(click())
            onView(withText("Déconnexion")).check(matches(isDisplayed()))
            onView(withText("Déconnexion")).perform(click())
        }
    }
}
