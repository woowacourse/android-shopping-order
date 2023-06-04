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
import woowacourse.shopping.domain.repository.MemberRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ui.model.CartProductModel
import woowacourse.shopping.ui.order.OrderContract
import woowacourse.shopping.ui.order.OrderPresenter

class OrderPresenterTest {
    private val view: OrderContract.View = mockk()
    private val cartRepository: CartRepository = mockk()
    private val memberRepository: MemberRepository = mockk()
    private val orderRepository: OrderRepository = mockk()
    private val presenter: OrderContract.Presenter =
        OrderPresenter(view, cartRepository, memberRepository, orderRepository)

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
        every { view.updateFinalPrice(any()) } just runs

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

    @Test
    fun `사용 가능한 포인트를 노출한다`() {
        // given
        val points = 5000
        val successSlot = slot<(Int) -> Unit>()
        every { memberRepository.getPoints(capture(successSlot), any()) } answers {
            successSlot.captured(points)
        }
        every { view.showPoints(any()) } just runs

        // when
        presenter.loadPoints()

        // then
        verify { view.showPoints(points) }
    }

    @Test
    fun `포인트 전액을 사용한다`() {
        // given
        val products: List<CartProduct> = listOf(
            createCartProduct(1, 2),
            createCartProduct(2, 4),
            createCartProduct(3, 6)
        )
        val cartSuccessSlot = slot<(List<CartProduct>) -> Unit>()
        every { cartRepository.getAll(capture(cartSuccessSlot), any()) } answers {
            cartSuccessSlot.captured(products)
        }
        every { view.showProducts(any()) } just runs
        every { view.showOriginalPrice(any()) } just runs
        every { view.updateFinalPrice(any()) } just runs
        presenter.loadProducts(listOf(3))

        val points = 2000
        val pointsSuccessSlot = slot<(Int) -> Unit>()
        every { memberRepository.getPoints(capture(pointsSuccessSlot), any()) } answers {
            pointsSuccessSlot.captured(points)
        }
        every { view.showPoints(any()) } just runs
        presenter.loadPoints()

        every { view.updatePointsUsed(any()) } just runs
        every { view.updateDiscountPrice(any()) } just runs

        // when
        presenter.useAllPoints()

        // then
        verify {
            view.updatePointsUsed(2000)
            view.updateDiscountPrice(2000)
            view.updateFinalPrice(4000)
        }
    }

    @Test
    fun `입력 포인트를 사용한다`() {
        // given
        val products: List<CartProduct> = listOf(
            createCartProduct(1, 2),
            createCartProduct(2, 4),
            createCartProduct(3, 6)
        )
        val cartSuccessSlot = slot<(List<CartProduct>) -> Unit>()
        every { cartRepository.getAll(capture(cartSuccessSlot), any()) } answers {
            cartSuccessSlot.captured(products)
        }
        every { view.showProducts(any()) } just runs
        every { view.showOriginalPrice(any()) } just runs
        every { view.updateFinalPrice(any()) } just runs
        presenter.loadProducts(listOf(3))

        val points = 2000
        val pointsSuccessSlot = slot<(Int) -> Unit>()
        every { memberRepository.getPoints(capture(pointsSuccessSlot), any()) } answers {
            pointsSuccessSlot.captured(points)
        }
        every { view.showPoints(any()) } just runs
        presenter.loadPoints()

        every { view.updatePointsUsed(any()) } just runs
        every { view.updateDiscountPrice(any()) } just runs

        // when
        val usePoints = 1000
        presenter.usePoints(usePoints)

        // then
        verify {
            view.updatePointsUsed(1000)
            view.updateDiscountPrice(1000)
            view.updateFinalPrice(5000)
        }
    }

    @Test
    fun `사용 포인트는 사용 가능 포인트를 초과할 수 없다`() {
        // given
        val products: List<CartProduct> = listOf(
            createCartProduct(1, 2),
            createCartProduct(2, 4),
            createCartProduct(3, 6)
        )
        val cartSuccessSlot = slot<(List<CartProduct>) -> Unit>()
        every { cartRepository.getAll(capture(cartSuccessSlot), any()) } answers {
            cartSuccessSlot.captured(products)
        }
        every { view.showProducts(any()) } just runs
        every { view.showOriginalPrice(any()) } just runs
        every { view.updateFinalPrice(any()) } just runs
        presenter.loadProducts(listOf(3))

        val points = 2000
        val pointsSuccessSlot = slot<(Int) -> Unit>()
        every { memberRepository.getPoints(capture(pointsSuccessSlot), any()) } answers {
            pointsSuccessSlot.captured(points)
        }
        every { view.showPoints(any()) } just runs
        presenter.loadPoints()

        every { view.notifyPointsExceeded() } just runs
        every { view.updatePointsUsed(any()) } just runs
        every { view.updateDiscountPrice(any()) } just runs

        // when
        val usePoints = 5000
        presenter.usePoints(usePoints)

        // then
        verify {
            view.notifyPointsExceeded()
            view.updatePointsUsed(2000)
            view.updateDiscountPrice(2000)
            view.updateFinalPrice(4000)
        }
    }

    @Test
    fun `주문을 하면 주문 상세를 보여준다`() {
        // given
        val products: List<CartProduct> = listOf(
            createCartProduct(1, 2),
            createCartProduct(2, 4),
            createCartProduct(3, 6)
        )
        val cartSuccessSlot = slot<(List<CartProduct>) -> Unit>()
        every { cartRepository.getAll(capture(cartSuccessSlot), any()) } answers {
            cartSuccessSlot.captured(products)
        }
        every { view.showProducts(any()) } just runs
        every { view.showOriginalPrice(any()) } just runs
        every { view.updateFinalPrice(any()) } just runs
        presenter.loadProducts(listOf(3))

        val orderId = 10
        val orderSuccessSlot = slot<(Int) -> Unit>()
        every { orderRepository.order(any(), any(), capture(orderSuccessSlot), any()) } answers {
            orderSuccessSlot.captured(orderId)
        }
        every { view.showOrderDetail(any()) } just runs

        // when
        presenter.order()

        // then
        verify { view.showOrderDetail(orderId) }
    }
}