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
import woowacourse.shopping.RecyclerViewMatcher
import woowacourse.shopping.TestFixture
import woowacourse.shopping.data.db.cartItem.CartItemDao
import woowacourse.shopping.data.db.cartItem.CartItemDatabase
import woowacourse.shopping.view.MainActivity
import woowacourse.shopping.view.cart.ShoppingCartFragment
import kotlin.concurrent.thread

@RunWith(AndroidJUnit4::class)
class ShoppingCartFragmentTest {
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
                .replace(R.id.fragment_container, ShoppingCartFragment())
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
    fun `장바구니_목록을_보여준다`() {
        onView(withId(R.id.rv_shopping_cart))
            .check(matches(isDisplayed()))
    }

    @Test
    fun `장바구니에_담은_상품_이름을_보여준다`() {
        onView(
            RecyclerViewMatcher(R.id.rv_shopping_cart)
                .atPositionOnView(0, R.id.tv_cart_item_name)
        ).check(matches(isDisplayed()))
    }

    @Test
    fun `장바구니에_담은_상품_가격을_보여준다`() {
        onView(
            RecyclerViewMatcher(R.id.rv_shopping_cart)
                .atPositionOnView(0, R.id.tv_cart_item_price)
        ).check(matches(isDisplayed()))
    }
}
