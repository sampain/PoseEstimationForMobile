package com.epmus.mobile.ui.login


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
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
class Test_history {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun test_historique() {
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
            // Access to statistics
            onView(withId(R.id.activity_statistics)).check(matches(isDisplayed()))
            onView(withId(R.id.activity_statistics)).perform(click())
            onView(withId(R.id.cardViewGraph)).check(matches(isDisplayed()))
            // Access to history
            onView(withId(R.id.history_button)).check(matches(isDisplayed()))
            onView(withId(R.id.history_button)).perform(click())
            onView(withId(R.id.history_list)).perform(click())
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
