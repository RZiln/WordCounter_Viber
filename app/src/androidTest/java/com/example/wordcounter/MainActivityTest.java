package com.example.wordcounter;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Espresso UI tests for {@link MainActivity}.
 *
 * <p>These tests run on an emulator or physical device. Espresso automatically
 * synchronises with the UI thread, but the ViewModel dispatches calculations on a
 * background thread and posts results back via LiveData. A small polling IdlingResource
 * is registered so Espresso waits until the Calculate button is re-enabled (i.e. the
 * background work is finished) before it checks the result views.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    /** Simple IdlingResource that considers the app idle when btnCalculate is enabled. */
    private IdlingResource calculationIdlingResource;

    @Before
    public void registerIdlingResource() {
        activityRule.getScenario().onActivity(activity -> {
            calculationIdlingResource = new IdlingResource() {
                private ResourceCallback callback;

                @Override
                public String getName() {
                    return "CalculationIdlingResource";
                }

                @Override
                public boolean isIdleNow() {
                    boolean idle = activity.findViewById(R.id.btnCalculate).isEnabled();
                    if (idle && callback != null) {
                        callback.onTransitionToIdle();
                    }
                    return idle;
                }

                @Override
                public void registerIdleTransitionCallback(ResourceCallback cb) {
                    this.callback = cb;
                }
            };
            IdlingRegistry.getInstance().register(calculationIdlingResource);
        });
    }

    @After
    public void unregisterIdlingResource() {
        if (calculationIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(calculationIdlingResource);
        }
    }

    // -----------------------------------------------------------------------
    // Launch state
    // -----------------------------------------------------------------------

    @Test
    public void inputFieldIsVisibleOnLaunch() {
        onView(withId(R.id.etInputText)).check(matches(isDisplayed()));
    }

    @Test
    public void calculateButtonIsVisibleOnLaunch() {
        onView(withId(R.id.btnCalculate)).check(matches(isDisplayed()));
    }

    @Test
    public void generateSampleButtonIsVisibleOnLaunch() {
        onView(withId(R.id.btnGenerateSample)).check(matches(isDisplayed()));
    }

    @Test
    public void clearButtonIsVisibleOnLaunch() {
        onView(withId(R.id.btnClear)).check(matches(isDisplayed()));
    }

    @Test
    public void metricsInitiallyShowZero() {
        onView(withId(R.id.tvSentencesValue)).check(matches(withText("0")));
        onView(withId(R.id.tvWordsValue)).check(matches(withText("0")));
        onView(withId(R.id.tvCharactersValue)).check(matches(withText("0")));
        onView(withId(R.id.tvNumbersValue)).check(matches(withText("0")));
    }

    @Test
    public void historyRecyclerViewIsGoneOnLaunch() {
        onView(withId(R.id.rvHistory)).check(matches(not(isDisplayed())));
    }

    @Test
    public void emptyHistoryMessageIsVisibleOnLaunch() {
        onView(withId(R.id.tvEmptyHistory)).check(matches(isDisplayed()));
    }

    // -----------------------------------------------------------------------
    // Validation: empty input shows error
    // -----------------------------------------------------------------------

    @Test
    public void emptyInputShowsValidationError() {
        // Ensure the field is empty first
        onView(withId(R.id.etInputText))
                .perform(replaceText(""), closeSoftKeyboard());

        onView(withId(R.id.btnCalculate)).perform(click());

        // The TextInputLayout shows an error — we verify it by checking the
        // error TextView that Material's TextInputLayout creates automatically
        onView(allOf(withId(com.google.android.material.R.id.textinput_error),
                isDisplayed()))
                .check(matches(isDisplayed()));
    }

    @Test
    public void whitespaceOnlyInputShowsValidationError() {
        onView(withId(R.id.etInputText))
                .perform(replaceText("   "), closeSoftKeyboard());
        onView(withId(R.id.btnCalculate)).perform(click());
        onView(allOf(withId(com.google.android.material.R.id.textinput_error),
                isDisplayed()))
                .check(matches(isDisplayed()));
    }

    // -----------------------------------------------------------------------
    // Calculate
    // -----------------------------------------------------------------------

    @Test
    public void validInputUpdatesMetricsToNonZero() {
        onView(withId(R.id.etInputText))
                .perform(replaceText("Hello world"), closeSoftKeyboard());
        onView(withId(R.id.btnCalculate)).perform(click());

        // After calculation, sentence and word counts must be non-zero
        onView(withId(R.id.tvSentencesValue)).check(matches(not(withText("0"))));
        onView(withId(R.id.tvWordsValue)).check(matches(not(withText("0"))));
    }

    @Test
    public void validInputUpdatesCharacterCount() {
        // "Hello" has 5 chars
        onView(withId(R.id.etInputText))
                .perform(replaceText("Hello"), closeSoftKeyboard());
        onView(withId(R.id.btnCalculate)).perform(click());

        onView(withId(R.id.tvCharactersValue)).check(matches(withText("5")));
    }

    @Test
    public void summaryFormulaDisplayedAfterCalculation() {
        onView(withId(R.id.etInputText))
                .perform(replaceText("Test sentence here."), closeSoftKeyboard());
        onView(withId(R.id.btnCalculate)).perform(click());

        onView(withId(R.id.tvSummaryFormula))
                .check(matches(withText(containsString("=>"))));
    }

    @Test
    public void summaryFormulaContainsInputText() {
        String input = "Short text";
        onView(withId(R.id.etInputText))
                .perform(replaceText(input), closeSoftKeyboard());
        onView(withId(R.id.btnCalculate)).perform(click());

        onView(withId(R.id.tvSummaryFormula))
                .check(matches(withText(containsString(input))));
    }

    // -----------------------------------------------------------------------
    // History
    // -----------------------------------------------------------------------

    @Test
    public void calculateAddsEntryToHistory() {
        onView(withId(R.id.etInputText))
                .perform(replaceText("First calculation"), closeSoftKeyboard());
        onView(withId(R.id.btnCalculate)).perform(click());

        // RecyclerView must be visible and have at least 1 item
        onView(withId(R.id.rvHistory)).check(matches(isDisplayed()));
        onView(withId(R.id.rvHistory)).check(matches(hasMinimumChildCount(1)));
        // Empty message must be hidden
        onView(withId(R.id.tvEmptyHistory)).check(matches(not(isDisplayed())));
    }

    @Test
    public void multipleCalculationsGrowHistory() {
        for (int i = 1; i <= 3; i++) {
            onView(withId(R.id.etInputText))
                    .perform(replaceText("Calculation number " + i), closeSoftKeyboard());
            onView(withId(R.id.btnCalculate)).perform(click());
        }
        onView(withId(R.id.rvHistory)).check(matches(hasMinimumChildCount(3)));
    }

    @Test
    public void clearHistoryHidesRecyclerView() {
        // Add one entry first
        onView(withId(R.id.etInputText))
                .perform(replaceText("Some text"), closeSoftKeyboard());
        onView(withId(R.id.btnCalculate)).perform(click());

        // Now clear history
        onView(withId(R.id.btnClearHistory)).perform(click());

        onView(withId(R.id.rvHistory)).check(matches(not(isDisplayed())));
        onView(withId(R.id.tvEmptyHistory)).check(matches(isDisplayed()));
    }

    // -----------------------------------------------------------------------
    // Clear button
    // -----------------------------------------------------------------------

    @Test
    public void clearButtonClearsInputField() {
        onView(withId(R.id.etInputText))
                .perform(replaceText("Some text to clear"), closeSoftKeyboard());
        onView(withId(R.id.btnClear)).perform(click());

        onView(withId(R.id.etInputText)).check(matches(withText("")));
    }

    @Test
    public void clearButtonResetsMetricsToZero() {
        // Calculate first so metrics are non-zero
        onView(withId(R.id.etInputText))
                .perform(replaceText("Hello world"), closeSoftKeyboard());
        onView(withId(R.id.btnCalculate)).perform(click());

        // Now clear
        onView(withId(R.id.btnClear)).perform(click());

        onView(withId(R.id.tvSentencesValue)).check(matches(withText("0")));
        onView(withId(R.id.tvWordsValue)).check(matches(withText("0")));
        onView(withId(R.id.tvCharactersValue)).check(matches(withText("0")));
        onView(withId(R.id.tvNumbersValue)).check(matches(withText("0")));
    }

    // -----------------------------------------------------------------------
    // Generate sample
    // -----------------------------------------------------------------------

    @Test
    public void generateSamplePopulatesInputField() {
        onView(withId(R.id.btnGenerateSample)).perform(click());
        // After sample is generated the input field must not be empty
        onView(withId(R.id.etInputText)).check(matches(not(withText(""))));
    }

    @Test
    public void generateSampleAllowsImmediateCalculation() {
        onView(withId(R.id.btnGenerateSample)).perform(click());
        onView(withId(R.id.btnCalculate)).perform(click());
        // Calculation should succeed and show non-zero results
        onView(withId(R.id.tvWordsValue)).check(matches(not(withText("0"))));
    }

    @Test
    public void generateSampleButtonIsEnabledAfterCalculation() {
        onView(withId(R.id.etInputText))
                .perform(replaceText("Test"), closeSoftKeyboard());
        onView(withId(R.id.btnCalculate)).perform(click());
        onView(withId(R.id.btnGenerateSample)).check(matches(isEnabled()));
    }
}
