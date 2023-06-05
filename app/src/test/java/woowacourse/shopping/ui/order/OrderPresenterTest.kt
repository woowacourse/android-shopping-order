package woowacourse.shopping.ui.order

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.Payment
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.discount.Point
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.PointRepository
import woowacourse.shopping.model.OrderModel
import woowacourse.shopping.model.PaymentModel
import woowacourse.shopping.model.PointModel
import woowacourse.shopping.model.PriceModel
import woowacourse.shopping.model.mapper.toDomain
import woowacourse.shopping.model.mapper.toUi

internal class OrderPresenterTest {
    private lateinit var orderRepository: OrderRepository
    private lateinit var pointRepository: PointRepository
    private lateinit var presenter: OrderContract.Presenter
    private lateinit var view: OrderContract.View

    @Before
    fun setUp() {
        orderRepository = mockk(relaxed = true)
        pointRepository = mockk(relaxed = true)
        view = mockk()
    }

    @Test
    internal fun 장바구니에_담은_상품을_주문할_수_있다() {
        // Given: 장바구니에서 주문할 상품을 선택한다.
        val order = mockk<Order>(relaxed = true)
        justRun { view.showOrderCompleted() }
        justRun {
            orderRepository.saveOrder(
                order = order,
                onSuccess = any(),
                onFailed = any()
            )
        }
        presenter = OrderPresenter(view, order.toUi(), orderRepository, pointRepository)

        // When: 상품을 주문한다.
        presenter.order()

        // Then: 주문이 완료되면 성공 메시지를 보여준다.
        verify(exactly = 1) {
            orderRepository.saveOrder(
                order = any(),
                onSuccess = any(),
                onFailed = any()
            )
        }
    }

    @Test
    internal fun 보유한_포인트가_적용할_포인트보다_많으면_포인트가_적용된다() {
        // Given: 주문 정보가 존재한다.
        val order = OrderModel(
            id = 0,
            orderProducts = listOf(),
            payment = PaymentModel(
                originalPayment = PriceModel(2000),
                finalPayment = PriceModel(0),
                usedPoint = PointModel(0)
            )
        )
        val discountedOrder =
            Order(0, listOf(), payment = Payment(Price(2000), Price(1500), Point(0)))

        // And: 포인트가 존재한다.
        val discountingPoint = mockk<PointModel>(relaxed = true)
        every { discountingPoint.value } answers { 500 }

        val availablePoint = mockk<Point>(relaxed = true)
        every { availablePoint.value } answers { 1000 }
        every { availablePoint.discountable(discountingPoint.toDomain()) } answers { true }

        presenter = OrderPresenter(
            view,
            order,
            orderRepository,
            pointRepository,
            availablePoint
        )
        every { availablePoint.discountable(any()) } answers { true }

        // And
        justRun { view.showFinalPayment(discountedOrder.finalPrice.toUi()) }

        // When: 상품을 주문한다.
        presenter.applyPoint(discountingPoint)

        // Then: 포인트를 적용하고, 할인된 가격을 화면에 보여준다.
        verify(exactly = 1) { view.showFinalPayment(discountedOrder.finalPrice.toUi()) }
    }
}
