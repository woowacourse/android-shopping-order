package woowacourse.shopping.feature.order.confirm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.domain.model.CartProduct
import com.example.domain.repository.CartRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import woowacourse.shopping.feature.CartFixture
import woowacourse.shopping.feature.getOrAwaitValue
import woowacourse.shopping.mapper.toPresentation

internal class OrderConfirmPresenterTest {
    private lateinit var view: OrderConfirmContract.View
    private lateinit var cartRepository: CartRepository
    private lateinit var presenter: OrderConfirmContract.Presenter

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setInit() {
        view = mockk()
        cartRepository = mockk()
    }

    @Test
    fun `주문 목록 내용을 보여준다`() {
        // given
        val cartIds = listOf(1L, 2L, 4L)
        presenter = OrderConfirmPresenter(view, cartRepository, cartIds)

        val mockCartProducts = CartFixture.getMockCarts()
        val successSlot = slot<(List<CartProduct>) -> Unit>()
        every { cartRepository.getAll(capture(successSlot), any()) } answers {
            successSlot.captured(mockCartProducts)
        }

        // when
        presenter.loadSelectedCarts()

        // then
        val actual = presenter.cartProducts.getOrAwaitValue()
        val expected = CartFixture.getMockCartItems(cartIds).map { it.toPresentation() }
        assert(actual == expected)
    }
}
