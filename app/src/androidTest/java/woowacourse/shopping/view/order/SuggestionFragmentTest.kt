package woowacourse.shopping.view.order

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.R
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.di.provider.RepositoryProvider
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.fixture.FakeCartRepository
import woowacourse.shopping.fixture.FakeProductRepository
import woowacourse.shopping.fixture.FakeRecentProductRepository
import woowacourse.shopping.fixture.productsFixture
import woowacourse.shopping.presentation.view.order.OrderActivity
import woowacourse.shopping.util.clickChildInRecyclerView
import woowacourse.shopping.util.nthProductInRecyclerView

class SuggestionFragmentTest {
    @BeforeEach
    fun setup() {
        val fakeProductRepository = FakeProductRepository()
        val fakeCartRepository =
            FakeCartRepository(
                initialCartProducts = productsFixture.takeLast(1).map { CartProduct(it.id, it.toDomain(), 1) },
            )
        val fakeRecentProductRepository =
            FakeRecentProductRepository(
                initialRecentProductIds = productsFixture.take(10).map { it.id },
            )

        RepositoryProvider.initProductRepository(fakeProductRepository)
        RepositoryProvider.initCartRepository(fakeCartRepository)
        RepositoryProvider.initRecentProductRepository(fakeRecentProductRepository)

        ActivityScenario.launch(OrderActivity::class.java)
        clickChildInRecyclerView(R.id.recycler_view_cart, 0, R.id.checkbox_select)
        onView(withId(R.id.button_order)).perform(click())
    }

    @Test
    fun `추천_상품을_확인할_수_있다`() {
        firstProductInRecyclerView(R.id.text_view_product_name).check(matches(withText(productsFixture[0].name)))
    }

    @Test
    fun `추천_상품_목록에서_장바구니에_담긴_수량_조절이_가능하다`() {
        // Give
        clickChildInRecyclerView(R.id.recycler_view_suggestion, 0, R.id.btn_quantity_plus)
        clickChildInRecyclerView(R.id.recycler_view_suggestion, 0, R.id.btn_quantity_plus)
        clickChildInRecyclerView(R.id.recycler_view_suggestion, 0, R.id.btn_quantity_plus)

        // When
        clickChildInRecyclerView(R.id.recycler_view_suggestion, 0, R.id.btn_quantity_minus)

        // Then
        firstProductInRecyclerView(R.id.textview_quantity).check(matches(withText("2")))
    }

    private fun firstProductInRecyclerView(targetViewId: Int) =
        nthProductInRecyclerView(
            R.id.recycler_view_suggestion,
            0,
            targetViewId,
        )
}
