package woowacourse.shopping.view.order

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isNotClickable
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

class CartFragmentTest {
    @BeforeEach
    fun setup() {
        val fakeProductRepository = FakeProductRepository()
        val fakeCartRepository =
            FakeCartRepository(
                initialCartProducts =
                    productsFixture
                        .take(15)
                        .map { CartProduct(it.id, it.toDomain(), 1) },
            )
        val fakeRecentProductRepository = FakeRecentProductRepository()

        RepositoryProvider.initProductRepository(fakeProductRepository)
        RepositoryProvider.initCartRepository(fakeCartRepository)
        RepositoryProvider.initRecentProductRepository(fakeRecentProductRepository)

        ActivityScenario.launch(OrderActivity::class.java)
    }

    @Test
    fun `장바구니에_담긴_상품을_확인할_수_있다`() {
        firstProductInRecyclerView(R.id.cart_item_name).check(matches(withText(productsFixture[0].name)))
    }

    @Test
    fun `다음_페이지_버튼을_누르면_페이지_수가_증가한다`() {
        // When
        onView(withId(R.id.btn_right)).perform(click())

        // Then
        onView(withId(R.id.text_view_page)).check(matches(withText("2")))
    }

    @Test
    fun `이전_페이지_버튼을_누르면_페이지_수가_감소한다`() {
        // Give
        onView(withId(R.id.btn_right)).perform(click())
        onView(withId(R.id.btn_right)).perform(click())

        // When
        onView(withId(R.id.btn_left)).perform(click())

        // Then
        onView(withId(R.id.text_view_page)).check(matches(withText("2")))
    }

    @Test
    fun `처음_페이지에서는_이전페이지_버튼을_클릭할_수_없다`() {
        // Give
        onView(withId(R.id.text_view_page)).check(matches(withText("1")))

        // Then
        onView(withId(R.id.btn_left)).check(matches(isNotClickable()))
    }

    @Test
    fun `다음_페이지에_조회_가능한_상품이_없다면_다음페이지_버튼을_클릭할_수_없다`() {
        // Give
        onView(withId(R.id.btn_right)).perform(click())
        onView(withId(R.id.btn_right)).perform(click())

        // When
        onView(withId(R.id.text_view_page)).check(matches(withText("3")))

        // Then
        onView(withId(R.id.btn_right)).check(matches(isNotClickable()))
    }

    @Test
    fun `장바구니에_담긴_상품을_제거할_수_있다`() {
        // Give
        firstProductInRecyclerView(R.id.cart_item_name).check(matches(withText(productsFixture[0].name)))

        // When
        clickChildInRecyclerView(R.id.recycler_view_cart, 0, R.id.btn_remove_cart)

        // Then
        firstProductInRecyclerView(R.id.cart_item_name).check(matches(withText(productsFixture[1].name)))
    }

    @Test
    fun `특정_상품의_구매_개수를_증가시킬_수_있다`() {
        // When
        clickChildInRecyclerView(R.id.recycler_view_cart, 0, R.id.btn_quantity_plus)

        // Then
        firstProductInRecyclerView(R.id.textview_quantity).check(matches(withText("2")))
    }

    @Test
    fun `특정_상품의_구매_개수를_감소시킬_수_있다`() {
        // Give
        clickChildInRecyclerView(R.id.recycler_view_cart, 0, R.id.btn_quantity_plus)
        clickChildInRecyclerView(R.id.recycler_view_cart, 0, R.id.btn_quantity_plus)

        // When
        clickChildInRecyclerView(R.id.recycler_view_cart, 0, R.id.btn_quantity_minus)

        // Then
        firstProductInRecyclerView(R.id.textview_quantity).check(matches(withText("2")))
    }

    @Test
    fun `구매_개수가_1개인_특정_상품의_구매_개수를_감소시키면_삭제된다`() {
        // When
        clickChildInRecyclerView(R.id.recycler_view_cart, 0, R.id.btn_quantity_minus)

        // Then
        firstProductInRecyclerView(R.id.cart_item_name).check(matches(withText(productsFixture[1].name)))
    }

    @Test
    fun `선택한_상품의_총_예상_주문_금액이_표시된다`() {
        // When
        clickChildInRecyclerView(R.id.recycler_view_cart, 0, R.id.checkbox_select)
        clickChildInRecyclerView(R.id.recycler_view_cart, 1, R.id.checkbox_select)

        // Then
        val expected = "%,d원".format(productsFixture[0].price + productsFixture[1].price)
        onView(withId(R.id.text_view_total_price)).check(matches(withText(expected)))
    }

    private fun firstProductInRecyclerView(targetViewId: Int) =
        nthProductInRecyclerView(
            R.id.recycler_view_cart,
            0,
            targetViewId,
        )
}
