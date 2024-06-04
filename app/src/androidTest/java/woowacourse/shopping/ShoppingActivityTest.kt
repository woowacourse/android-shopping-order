import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.R
import woowacourse.shopping.presentation.ui.shopping.ShoppingActivity
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
class ShoppingActivityTest {
    @get:Rule
    val activityRule: ActivityScenarioRule<ShoppingActivity> =
        ActivityScenarioRule<ShoppingActivity>(
            Intent(
                ApplicationProvider.getApplicationContext(),
                ShoppingActivity::class.java,
            ),
        )

    /*@Test
    fun `20개의_상품_목록이_있을_때_20개의_상품_목록으로_스크롤을_하면_더보기_버튼이_보인다`() {
        // Given
        sleep(3000)
        val recyclerView = onView(withId(R.id.rv_product_list))

        // When
        recyclerView.perform(scrollToPosition<ShoppingViewHolder>(5))

        // then
        onView(withId(R.id.btn_load_more)).check(
            matches(isDisplayed()),
        )
    }*/

    @Test
    fun `쇼핑_목록이_보인다`() {
        sleep(3000)
        onView(withId(R.id.rv_product_list))
            .check(matches(isDisplayed()))
    }
}
