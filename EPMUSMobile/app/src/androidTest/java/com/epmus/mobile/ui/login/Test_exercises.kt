package com.epmus.mobile.ui.login


import androidx.recyclerview.widget.RecyclerView
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
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class Test_exercises {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun test_exercices() {
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
            // Access to exercise program
            onView(withId(R.id.activity_program)).check(matches(isDisplayed()))
            onView(withId(R.id.activity_program)).perform(click())
            onView(withId(R.id.fab_messaging)).check(matches(isDisplayed()))
            onView(withId(R.id.program_list)).check(matches(isDisplayed()))
            onView(allOf(isDisplayed(), withId(R.id.playButton)))
            // Access to messaging
            onView(withId(R.id.fab_messaging)).perform(click())
            onView(withId(R.id.recyclerview_newmessage)).check(matches(isDisplayed()))
            pressBack()
            // Access to one exercice
            onView(withText("Plier le bras gauche (Rep)")).check(matches(isDisplayed()))
            onView(withId(R.id.program_list)).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    click()
                )
            )
            onView(withId(R.id.program_detail)).check(matches(isDisplayed()))
            onView(withText("Assoyez-vous bien droit sur une chaise avec le bras allongé le long du corps.  Pliez le coude en gardant la paume de votre main vers le haut.  Redescendez lentement et répétez.")).check(
                matches(isDisplayed())
            )
            onView(withId(R.id.fab_play)).check(matches(isDisplayed()))
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

