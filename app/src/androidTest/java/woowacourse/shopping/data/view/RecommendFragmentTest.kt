package woowacourse.shopping.data.view

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.R
import woowacourse.shopping.TestFixture
import woowacourse.shopping.data.db.cartItem.CartItemDao
import woowacourse.shopping.data.db.cartItem.CartItemDatabase
import woowacourse.shopping.view.MainActivity
import woowacourse.shopping.view.recommend.RecommendFragment
import kotlin.concurrent.thread

@RunWith(AndroidJUnit4::class)
class RecommendFragmentTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    private lateinit var database: CartItemDatabase
    private lateinit var dao: CartItemDao
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = CartItemDatabase.getInstance(context)
        dao = database.cartItemDao()
        thread {
            dao.saveCartItem(TestFixture.makeCartItemEntity())
        }.join()

        activityRule.scenario.onActivity { activity ->
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RecommendFragment())
                .commitNow()
        }
    }

    @After
    fun clearDB() {
        thread {
            database.clearAllTables()
        }.join()
    }

    @Test
    fun `총_가격을_보여준다`() {
        onView(withId(R.id.tv_total_price))
            .check(matches(isDisplayed()))
    }

    @Test
    fun `구매_버튼을_보여준다`() {
        onView(withId(R.id.btn_order))
            .check(matches(isDisplayed()))
    }
}
