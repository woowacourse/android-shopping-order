package woowacourse.shopping.data.view

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.R
import woowacourse.shopping.view.MainActivity
import woowacourse.shopping.view.products.ProductsListFragment

@RunWith(AndroidJUnit4::class)
class ProductListFragmentTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        activityRule.scenario.onActivity { activity ->
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProductsListFragment())
                .commitNow()
        }
    }

    @Test
    fun `상품_목록을_보여준다`() {
        onView(withId(R.id.rv_products))
            .check(matches(isDisplayed()))
    }

    @Test
    fun `데이터가_모두_로드되지_않은_경우_더보기_버튼은_보여지지_않아야_한다`() {
        onView(withId(R.id.btn_more_product))
            .check(matches(not(isDisplayed())))
    }

    //TODO 스크롤 관련 테스트코드 학습 필
//    @Test
//    fun `더보기_버튼을_클릭하면_새로운_상품이_로드_되어야_한다`() {
//        Thread.sleep(2000)
//        onView(withId(R.id.rv_products))
//            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(18))
//
//        onView(withI요(R.id.btn_more_product)).perform(ViewActions.click())
//
//        onView(withId(R.id.rv_products)).check { view, _ ->
//            val recyclerView = view as RecyclerView
//            val itemCount = recyclerView.adapter?.itemCount ?: 0
//            assertTrue(itemCount > 20)
//        }
//    }
}