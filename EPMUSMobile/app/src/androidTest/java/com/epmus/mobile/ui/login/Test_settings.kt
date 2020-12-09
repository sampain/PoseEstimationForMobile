package com.epmus.mobile.ui.login


import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
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
class Test_settings {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun test_parametres() {
        try {
            onView(withContentDescription("More options")).check(ViewAssertions.matches(isDisplayed()))
            onView(withContentDescription("More options")).perform(click())
            onView(withText("Déconnexion")).check(ViewAssertions.matches(isDisplayed()))
            onView(withText("Déconnexion")).perform(click())
            Thread.sleep(1000)
        } catch (e: Exception) {

        } finally {
            // Connection
            onView(withId(R.id.loginDisabled)).check(ViewAssertions.matches(isDisplayed()))
            onView(withId(R.id.username)).perform(typeText("a@a.com"))
            onView(withId(R.id.password)).perform(typeText("aaaaaa"))
            onView(withId(R.id.login)).perform(click())
            Thread.sleep(1000)
            // Access to settings
            onView(withContentDescription("Paramètres")).check(ViewAssertions.matches(isDisplayed()))
            onView(withContentDescription("Paramètres")).perform(click())
            // Activation/deactivation of the audio
            onView(withText("Désactiver l'audio")).check(ViewAssertions.matches(isDisplayed()))
            onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(3, click()))
            Thread.sleep(1000)
            onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(3, click()))
            // Disconnection
            onView(withContentDescription("Navigate up")).perform(click())
            onView(withContentDescription("More options")).check(ViewAssertions.matches(isDisplayed()))
            onView(withContentDescription("More options")).perform(click())
            onView(withText("Déconnexion")).check(ViewAssertions.matches(isDisplayed()))
            onView(withText("Déconnexion")).perform(click())
        }
    }
}
