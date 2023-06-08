package woowacourse.shopping.ui.cart

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.Test
import woowacourse.shopping.createCartProduct
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.repository.CartRepository

class CartPresenterTest {
    private lateinit var presenter: CartPresenter
    private val view: CartContract.View = mockk()
    private val cartRepository: CartRepository = mockk()

    @Test
    fun `주문을 하면 선택된 카트 상품 아이디를 받아 주문을 보여준다`() {
        // given
        val orderProduct = createCartProduct(1, 2, true)
        val products: List<CartProduct> = listOf(orderProduct, createCartProduct(2, 1, false))
        val successSlot = slot<(List<CartProduct>) -> Unit>()
        every { cartRepository.getAll(capture(successSlot), any()) } answers {
            successSlot.captured(products)
        }
        every { view.updateCart(any(), any(), any()) } just runs
        every { view.updateAllChecked(any()) } just runs
        every { view.updateCartTotalPrice(any()) } just runs
        every { view.updateCartTotalQuantity(any()) } just runs
        every { view.updateNavigationVisibility(any()) } just runs
        every { view.showOrder(any()) } just runs
        presenter = CartPresenter(
            view, cartRepository, 0, 0
        )

        // when
        presenter.order()

        // then
        verify {
            view.showOrder(listOf(1))
        }
    }
}
