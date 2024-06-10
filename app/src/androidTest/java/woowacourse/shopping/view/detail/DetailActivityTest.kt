package woowacourse.shopping.view.detail

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.R
import woowacourse.shopping.data.model.product.toProduct
import woowacourse.shopping.util.FakeShoppingApplication
import woowacourse.shopping.util.getFixtureProduct

@RunWith(AndroidJUnit4::class)
class DetailActivityTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val application = context.applicationContext as FakeShoppingApplication

    @Before
    fun setUp() =
        runTest {
            application.recentProductRepository.deleteAll()
        }

    private val intent =
        DetailActivity.createIntent(ApplicationProvider.getApplicationContext(), 1, false)

    @get:Rule
    val activityRule = ActivityScenarioRule<DetailActivity>(intent)

    @Test
    fun `선택한_상품의_사진이_표시된다`() {
        onView(withId(R.id.iv_detail_product_image))
            .check(matches(isDisplayed()))
    }

    @Test
    fun `선택한_상품의_제목이_표시된다`() {
        onView(withId(R.id.tv_detail_product_name))
            .check(matches(withText("apple1")))
    }

    @Test
    fun `선택한_상품의_가격이_표시된다`() {
        onView(withId(R.id.tv_detail_price))
            .check(matches(withText("1,000원")))
    }

    @Test
    fun `마지막으로_본_상품이_나타난다`() =
        runTest {
            application.recentProductRepository.save(getFixtureProduct(1, 1000).toProduct())
            application.recentProductRepository.save(getFixtureProduct(2, 2000).toProduct())

            ActivityScenario.launch<DetailActivity>(intent)

            onView(withId(R.id.cl_recent_viewed_products))
                .check(matches(isDisplayed()))
        }
}
