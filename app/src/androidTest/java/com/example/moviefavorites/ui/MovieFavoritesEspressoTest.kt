/**
 * Robust Espresso UI tests for the MovieFavorites app.
 */
package com.example.moviefavorites.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moviefavorites.MainActivity
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieFavoritesEspressoTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun waitForAppToBeReady() {
        composeTestRule.waitUntil(timeoutMillis = 20_000) {
            composeTestRule
                .onAllNodesWithText("Loading movies...")
                .fetchSemanticsNodes()
                .isEmpty()
        }
    }

    private fun performSmallSwipeUp() {
        composeTestRule.onRoot().performTouchInput {
            val c = center
            val start = Offset(c.x, c.y + 180f)
            val end = Offset(c.x, c.y - 80f)
            swipe(start, end, 120)
        }
    }

    /**
     * Scrolls until node is found OR we've scrolled to the absolute bottom
     */
    private fun scrollToBottom(
        targetMatcher: SemanticsMatcher,
        maxAttempts: Int = 100
    ): Boolean {
        // Check if already visible
        if (composeTestRule.onAllNodes(targetMatcher, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()) {
            return true
        }

        var previousNodeCount = -1
        var sameCountStreak = 0

        repeat(maxAttempts) { attempt ->
            performSmallSwipeUp()
            composeTestRule.waitForIdle()
            Thread.sleep(300)

            // Check if target found
            val targetNodes = composeTestRule.onAllNodes(targetMatcher, useUnmergedTree = true).fetchSemanticsNodes()
            if (targetNodes.isNotEmpty()) {
                Thread.sleep(300) // Extra wait for stability
                return true
            }

            // Check if we've hit the bottom (no new nodes appearing)
            val allNodes = composeTestRule.onRoot(useUnmergedTree = true).fetchSemanticsNode()
            val currentNodeCount = allNodes.children.size

            if (currentNodeCount == previousNodeCount) {
                sameCountStreak++
                if (sameCountStreak >= 3) {
                    // We've scrolled 3 times with no change - we're at the bottom
                    return false
                }
            } else {
                sameCountStreak = 0
            }
            previousNodeCount = currentNodeCount

            if ((attempt + 1) % 10 == 0) {
                Thread.sleep(200)
            }
        }
        return false
    }

    @Test
    fun testAddMovieAndNavigation_scrollUntilFound() {
        val movieTitle = "The Ultimate Test Movie"

        composeTestRule.onNodeWithContentDescription("Add movie").performClick()
        composeTestRule.onNodeWithText("Title").performTextInput(movieTitle)
        composeTestRule.onNodeWithText("Year").performTextInput("2024")
        composeTestRule.onNodeWithText("Genre").performTextInput("Testing")
        composeTestRule.onNodeWithText("Rating (0-10)").performTextInput("10")
        composeTestRule.onNodeWithText("Official URL").performTextInput("https://www.imdb.com/title/tt0068646/")
        composeTestRule.onNodeWithText("Description").performTextInput("A movie about passing tests.")
        composeTestRule.onNodeWithText("Add").performClick()
        composeTestRule.waitForIdle()

        // Scroll all the way down until we find the movie - with more attempts
        val found = scrollToBottom(hasText(movieTitle), maxAttempts = 150)
        assertTrue("Movie '$movieTitle' not found after scrolling to bottom", found)

        // Extra scrolls to make absolutely sure we're at the bottom and movie is visible
        repeat(5) {
            performSmallSwipeUp()
            Thread.sleep(200)
        }
        composeTestRule.waitForIdle()
        Thread.sleep(800)

        // Verify the movie is still visible after extra scrolls
        val movieVisible = composeTestRule.onAllNodes(hasText(movieTitle), useUnmergedTree = true)
            .fetchSemanticsNodes().isNotEmpty()
        assertTrue("Movie disappeared after scrolling", movieVisible)

        // Click the movie - try clickable first, then fallback to text
        val clickableNodes = composeTestRule.onAllNodes(
            hasText(movieTitle) and hasClickAction(),
            useUnmergedTree = true
        ).fetchSemanticsNodes()

        if (clickableNodes.isNotEmpty()) {
            composeTestRule.onAllNodes(hasText(movieTitle) and hasClickAction(), useUnmergedTree = true)[0]
                .performClick()
        } else {
            composeTestRule.onAllNodes(hasText(movieTitle), useUnmergedTree = true)[0]
                .performClick()
        }

        composeTestRule.waitUntil(timeoutMillis = 7_000) {
            composeTestRule.onAllNodesWithText("Synopsis").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun testFavoriteAndSearchFunctionality_scrollUntilFoundAndFavorite() {
        val movieToAdd = "Movie To Be Favorited"

        composeTestRule.onNodeWithContentDescription("Add movie").performClick()
        composeTestRule.onNodeWithText("Title").performTextInput(movieToAdd)
        composeTestRule.onNodeWithText("Year").performTextInput("2023")
        composeTestRule.onNodeWithText("Genre").performTextInput("Favorite")
        composeTestRule.onNodeWithText("Rating (0-10)").performTextInput("8")
        composeTestRule.onNodeWithText("Official URL").performTextInput("https://www.imdb.com/title/tt0111161/")
        composeTestRule.onNodeWithText("Description").performTextInput("A favorite movie.")
        composeTestRule.onNodeWithText("Add").performClick()
        composeTestRule.waitForIdle()

        // Scroll all the way down until we find the movie
        val found = scrollToBottom(hasText(movieToAdd))
        assertTrue("Movie '$movieToAdd' not found after scrolling to bottom", found)

        Thread.sleep(500)

        // Find and click the favorite button - use simple ancestor matching
        val favButtonMatcher = hasContentDescription("Add to favorites") and
                hasAnyAncestor(hasAnyDescendant(hasText(movieToAdd)))

        val favNodes = composeTestRule.onAllNodes(favButtonMatcher, useUnmergedTree = true).fetchSemanticsNodes()

        if (favNodes.isNotEmpty()) {
            // Found button in the row, click it
            composeTestRule.onAllNodes(favButtonMatcher, useUnmergedTree = true)[0].performClick()
        } else {
            // Fallback: try sibling matching
            val siblingMatcher = hasContentDescription("Add to favorites") and hasAnySibling(hasText(movieToAdd))
            val siblingNodes = composeTestRule.onAllNodes(siblingMatcher, useUnmergedTree = true).fetchSemanticsNodes()

            if (siblingNodes.isNotEmpty()) {
                composeTestRule.onAllNodes(siblingMatcher, useUnmergedTree = true)[0].performClick()
            } else {
                throw AssertionError("Could not find favorite button for movie: $movieToAdd")
            }
        }

        // Wait for favorite state to change
        composeTestRule.waitUntil(timeoutMillis = 7_000) {
            composeTestRule.onAllNodes(
                hasContentDescription("Remove from favorites"),
                useUnmergedTree = true
            ).fetchSemanticsNodes().isNotEmpty()
        }

        assertTrue(
            "Movie was not favorited",
            composeTestRule.onAllNodes(hasContentDescription("Remove from favorites"), useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        )
    }
}