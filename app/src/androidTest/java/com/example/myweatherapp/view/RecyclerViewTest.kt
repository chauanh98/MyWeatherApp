package com.example.myweatherapp.view

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.PerformException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.myweatherapp.R
import com.example.myweatherapp.ui.main.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class RecyclerViewTest {
    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    companion object {
        private const val ITEM_POSITION = 0
    }

    @Test(expected = PerformException::class)
    fun itemWithText_doesNotExist() {
        // Attempt to scroll to an item that contains the special text.
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView)) // scrollTo will fail the test if no item matches.
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    ViewMatchers.hasDescendant(ViewMatchers.withText("not item in the list"))
                )
            )
    }

    @Test
    fun scrollToItemBelowFold_checkItsText() {
        // First scroll to the position that needs to be matched and click on it.
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    ITEM_POSITION,
                    ViewActions.click()
                )
            )

        // Match the text in an item below the fold and check that it's displayed.
        val itemElementText = "Moscow"
        Espresso.onView(ViewMatchers.withText(itemElementText))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}