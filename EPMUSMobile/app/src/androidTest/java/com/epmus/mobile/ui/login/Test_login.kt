package com.epmus.mobile.ui.login


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
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
class Test_login {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun test_deconnexion() {
        try {
            onView(withContentDescription("More options")).check(matches(isDisplayed()))
            onView(withContentDescription("More options")).perform(click())
            onView(withText("Déconnexion")).check(matches(isDisplayed()))
            onView(withText("Déconnexion")).perform(click())
            Thread.sleep(1000)
        }
        catch (e: Exception){

        }
        finally {
            // Connection
            onView(withId(R.id.loginDisabled)).check(matches(isDisplayed()))
            onView(withId(R.id.username)).perform(typeText("a@a.com"))
            onView(withId(R.id.password)).perform(typeText("aaaaaa"))
            onView(withId(R.id.login)).perform(click())
            Thread.sleep(1000)
            // Disconnection
            onView(withContentDescription("More options")).check(matches(isDisplayed()))
            onView(withContentDescription("More options")).perform(click())
            onView(withText("Déconnexion")).check(matches(isDisplayed()))
            onView(withText("Déconnexion")).perform(click())
        }
    }

    @Test
    fun loginActivityBadUserTest() {
        // Connection with unvavailable email
        onView(withId(R.id.username)).perform(typeText("fail@fail.com"))
        onView(withId(R.id.password)).perform(typeText("BadUser"))
        onView(withId(R.id.login)).perform(click())
        onView(withId(R.id.login)).check(matches(isDisplayed()))
    }

    @Test
    fun loginActivityBadFormatTest() {
        // Connection with non-authorized username (not an emaiil)
        onView(withId(R.id.username)).perform(typeText("BadUser"))
        onView(withId(R.id.password)).perform(typeText("BadUser"))
        onView(withId(R.id.loginDisabled)).check(matches(isDisplayed()))
    }
}

