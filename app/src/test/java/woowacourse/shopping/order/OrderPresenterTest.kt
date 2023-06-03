package woowacourse.shopping.order

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.Test
import woowacourse.shopping.createCartProduct
import woowacourse.shopping.createCartProductModel
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.ui.model.CartProductModel
import woowacourse.shopping.ui.order.OrderContract
import woowacourse.shopping.ui.order.OrderPresenter

class OrderPresenterTest {
    private val view: OrderContract.View = mockk()
    private val cartRepository: CartRepository = mockk()
    private val presenter: OrderContract.Presenter = OrderPresenter(view, cartRepository)

    @Test
    fun `주문 상품을 목록과 주문 금액을 노출한다`() {
        // given
        val products: List<CartProduct> = listOf(
            createCartProduct(1, 2),
            createCartProduct(2, 4),
            createCartProduct(3, 6)
        )
        val successSlot = slot<(List<CartProduct>) -> Unit>()
        every { cartRepository.getAll(capture(successSlot), any()) } answers {
            successSlot.captured(products)
        }
        every { view.showProducts(any()) } just runs
        every { view.showOriginalPrice(any()) } just runs

        // when
        val ids: List<Int> = listOf(1, 3)
        presenter.loadProducts(ids)

        // then
        val expectedProducts: List<CartProductModel> = listOf(createCartProductModel(1, 2), createCartProductModel(3, 6))
        verify {
            view.showProducts(expectedProducts)
        }

        // and
        val expectedOriginalPrice = 8000
        verify {
            view.showOriginalPrice(expectedOriginalPrice)
        }
    }
}