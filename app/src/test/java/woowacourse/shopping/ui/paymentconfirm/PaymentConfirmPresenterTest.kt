package woowacourse.shopping.ui.paymentconfirm

import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Count
import woowacourse.shopping.domain.EarnRate
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.Point
import woowacourse.shopping.domain.Price
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.UserPointInfo
import woowacourse.shopping.domain.exception.AddOrderException
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.PointRepository

class PaymentConfirmPresenterTest {
    private lateinit var view: PaymentConfirmContract.View
    private lateinit var pointRepository: PointRepository
    private lateinit var orderRepository: OrderRepository
    private lateinit var presenter: PaymentConfirmPresenter

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        pointRepository = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        presenter = PaymentConfirmPresenter(
            view,
            pointRepository,
            orderRepository,
            listOf(
                BasketProduct(1, Count(2), Product(1, "더미", Price(1000), "url")),
                BasketProduct(2, Count(2), Product(2, "더미", Price(1000), "url")),
                BasketProduct(3, Count(2), Product(3, "더미", Price(1000), "url"))
            )
        )
    }

    @Test
    fun `유저의 포인트 정보를 불러오고 화면에 표시한다`() {
        // given
        clearMocks(view)
        clearMocks(pointRepository)
        every { view.updateUserPointInfo(any()) } just runs
        every { pointRepository.getUserPointInfo(any()) } answers {
            val callback: (UserPointInfo) -> Unit = arg(0)
            callback(UserPointInfo(Point(100), EarnRate(1)))
        }

        // when
        presenter.fetchUserPointInfo()

        // then
        verify(exactly = 1) { view.updateUserPointInfo(any()) }
        verify(exactly = 1) { pointRepository.getUserPointInfo(any()) }
    }

    @Test
    fun `PreOrderInfo를 계산하면 계산된 데이터를 화면에 표시한다`() {
        // given
        clearMocks(view)
        every { view.updatePreOrderInfo(any()) } just runs
        // when
        presenter.fetchPreOrderInfo()
        // then
        verify(exactly = 1) { view.updatePreOrderInfo(any()) }
    }

    @Test
    fun `포인트를 적용할때 보유포인트보다 많다면 포인트가 적용되지않고 메시지만 화면에 표시한다`() {
        // given
        clearMocks(pointRepository)
        every { pointRepository.getUserPointInfo(any()) } answers {
            val callback: (UserPointInfo) -> Unit = arg(0)
            callback(UserPointInfo(Point(100), EarnRate(1)))
        }
        presenter = PaymentConfirmPresenter(
            view,
            pointRepository,
            orderRepository,
            listOf(
                BasketProduct(1, Count(2), Product(1, "더미", Price(1000), "url")),
                BasketProduct(2, Count(2), Product(2, "더미", Price(1000), "url")),
                BasketProduct(3, Count(2), Product(3, "더미", Price(1000), "url"))
            )
        )
        clearMocks(view)
        every { view.updatePointMessageCode(any()) } just runs

        // when
        presenter.applyPoint(300)

        // then
        verify(exactly = 1) { view.updatePointMessageCode(any()) }
    }

    @Test
    fun `보유 포인트 이내에서 포인트를 적용하면 포인트가 적용되고 사용포인트,실결제액,메시지가 업데이트된다`() {
        // given
        clearMocks(pointRepository)
        every { pointRepository.getUserPointInfo(any()) } answers {
            val callback: (UserPointInfo) -> Unit = arg(0)
            callback(UserPointInfo(Point(500), EarnRate(1)))
        }
        presenter = PaymentConfirmPresenter(
            view,
            pointRepository,
            orderRepository,
            listOf(
                BasketProduct(1, Count(2), Product(1, "더미", Price(1000), "url")),
                BasketProduct(2, Count(2), Product(2, "더미", Price(1000), "url")),
                BasketProduct(3, Count(2), Product(3, "더미", Price(1000), "url"))
            )
        )
        clearMocks(view)
        every { view.updatePointMessageCode(any()) } just runs
        every { view.updateUsingPoint(any()) } just runs
        every { view.updateActualPayment(any()) } just runs

        // when
        presenter.applyPoint(300)

        // then
        verify(exactly = 1) { view.updatePointMessageCode(any()) }
        verify(exactly = 1) { view.updateUsingPoint(any()) }
        verify(exactly = 1) { view.updateActualPayment(any()) }
    }

    @Test
    fun `주문 할때 성공 상황이라면 성공 알림을 표시한다`() {
        // given
        every { orderRepository.addOrder(any(), any(), any(), any()) } answers {
            val callback: (Result<Int>) -> Unit = arg(3)
            callback(Result.success(1))
        }
        val order: Order = mockk(relaxed = true)
        every { orderRepository.getIndividualOrderInfo(any(), any()) } answers {
            val callback: (Order) -> Unit = arg(1)
            callback(order)
        }
        clearMocks(view)
        every { view.showOrderSuccessNotification(any()) } just runs

        // when
        presenter.addOrder()

        // then
        verify(exactly = 1) { view.showOrderSuccessNotification(any()) }
    }

    @Test
    fun `주문시 재고오류로 실패할경우 재고오류 알림을 보여준다`() {
        // given
        every { orderRepository.addOrder(any(), any(), any(), any()) } answers {
            val callback: (Result<Int>) -> Unit = arg(3)
            callback(Result.failure(AddOrderException.ShortageStockException("테스트입니다")))
        }
        clearMocks(view)
        every { view.showOrderShortageStockFailureNotification(any()) } just runs

        // when
        presenter.addOrder()

        // then
        verify(exactly = 1) { view.showOrderShortageStockFailureNotification(any()) }
    }

    @Test
    fun `주문시 포인트 부족으로 실패할경우 포인트 부족 오류 알림을 보여준다`() {
        // given
        every { orderRepository.addOrder(any(), any(), any(), any()) } answers {
            val callback: (Result<Int>) -> Unit = arg(3)
            callback(Result.failure(AddOrderException.LackOfPointException("테스트입니다")))
        }
        clearMocks(view)
        every { view.showOrderLackOfPointFailureNotification(any()) } just runs

        // when
        presenter.addOrder()

        // then
        verify(exactly = 1) { view.showOrderLackOfPointFailureNotification(any()) }
    }
}
