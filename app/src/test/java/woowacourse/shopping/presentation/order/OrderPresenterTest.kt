package woowacourse.shopping.presentation.order

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.presentation.CardFixture
import woowacourse.shopping.presentation.CartFixture
import woowacourse.shopping.presentation.mapper.toModel
import woowacourse.shopping.presentation.mapper.toUIModel
import woowacourse.shopping.presentation.model.PointModel
import woowacourse.shopping.presentation.view.order.OrderContract
import woowacourse.shopping.presentation.view.order.OrderPresenter
import woowacouse.shopping.data.repository.card.CardRepository
import woowacouse.shopping.data.repository.cart.CartRepository
import woowacouse.shopping.data.repository.order.OrderRepository
import woowacouse.shopping.data.repository.point.PointRepository
import woowacouse.shopping.model.cart.CartProduct
import woowacouse.shopping.model.cart.CartProducts
import woowacouse.shopping.model.order.Order
import woowacouse.shopping.model.point.Point

class OrderPresenterTest {
    private lateinit var presenter: OrderContract.Presenter
    private lateinit var view: OrderContract.View
    private lateinit var cardRepository: CardRepository
    private lateinit var cartRepository: CartRepository
    private lateinit var pointRepository: PointRepository
    private lateinit var orderRepository: OrderRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)

        cardRepository = mockk()
        cartRepository = mockk()
        pointRepository = mockk()
        orderRepository = mockk()

        presenter = OrderPresenter(
            view,
            cardRepository,
            cartRepository,
            pointRepository,
            orderRepository
        )
    }

    @Test
    fun `사용할 포인트를 설정할 수 있다`() {
        // given
        val usePoint = slot<PointModel>()
        justRun { view.showUsePointView(capture(usePoint)) }

        // when
        presenter.setUsePoint(100)

        // then
        val actual = usePoint.captured
        val expected = 100

        assertEquals(expected, actual.value)
        verify { view.showUsePointView(actual) }
    }

    @Test
    fun `장바구니 아이디들을 갖고 전체 주문 정보를 불러온다`() {
        // given
        val carts =
            CartFixture.getFixture().filter { it.checked && it.count > 0 }.map { it.toModel() }
        val cartIds =
            ArrayList(CartFixture.getFixture().filter { it.checked && it.count > 0 }.map { it.id })
        val userPoint = Point(1_000)
        val orderPrice = carts.sumOf { it.product.price * it.count }
        val predictionPoint = Point((orderPrice * 0.01).toInt())

        // 장바구니 아이디들로 조회한다
        val cartOnSuccess = slot<(List<CartProduct>) -> Unit>()
        every {
            cartRepository.loadCartsByCartIds(
                cartIds = cartIds,
                onFailure = any(),
                onSuccess = capture(cartOnSuccess),
            )
        } answers {
            cartOnSuccess.captured(carts)
        }

        // 카드 정보를 조회한다.
        every { cardRepository.loadCards() } returns CardFixture.getFixture().map { it.toModel() }

        // 유저 가용 포인트를 조회한다.
        val pointOnSuccess = slot<(Point) -> Unit>()
        every {
            pointRepository.loadPoint(
                onFailure = any(),
                onSuccess = capture(pointOnSuccess)
            )
        } answers {
            pointOnSuccess.captured(userPoint)
        }

        // 예상 적립 포인트를 조회한다.
        val predictionPointOnSuccess = slot<(Point) -> Unit>()
        every {
            pointRepository.loadPredictionSavePoint(
                orderPrice,
                onFailure = any(),
                onSuccess = capture(predictionPointOnSuccess),
            )
        } answers {
            predictionPointOnSuccess.captured(predictionPoint)
        }

        justRun { view.showCardItemsView(CardFixture.getFixture()) }
        justRun { view.showUserPointView(userPoint.toUIModel()) }
        justRun { view.setPointTextChangeListener(orderPrice, userPoint.toUIModel()) }
        justRun { view.showSavePredictionPointView(predictionPoint.toUIModel()) }
        justRun { view.showOrderPriceView(orderPrice) }
        justRun { view.showProductItemsView(carts.map { it.toUIModel() }) }
        justRun { view.setLayoutVisibility() }

        // when
        presenter.loadOrderProducts(cartIds)

        // then
        verify { view.showCardItemsView(CardFixture.getFixture()) }
        verify { view.showUserPointView(userPoint.toUIModel()) }
        verify { view.setPointTextChangeListener(orderPrice, userPoint.toUIModel()) }
        verify { view.showSavePredictionPointView(predictionPoint.toUIModel()) }
        verify { view.showOrderPriceView(orderPrice) }
        verify { view.showProductItemsView(carts.map { it.toUIModel() }) }
        verify { view.setLayoutVisibility() }
    }

    @Test
    fun `주문을 할 수 있다`() {
        // given
        val carts =
            CartFixture.getFixture().filter { it.checked && it.count > 0 }.map { it.toModel() }
        val cartIds =
            ArrayList(CartFixture.getFixture().filter { it.checked && it.count > 0 }.map { it.id })
        val userPoint = Point(1_000)

        val orderPrice = carts.sumOf { it.product.price * it.count }
        val predictionPoint = Point((orderPrice * 0.01).toInt())

        val order = Order(
            999999999L,
            CartProducts(carts),
            Point(0),
            CardFixture.getFixture()[0].toModel()
        )

        val orderId = 1L

        // 장바구니 아이디들로 조회한다
        val cartOnSuccess = slot<(List<CartProduct>) -> Unit>()
        every {
            cartRepository.loadCartsByCartIds(
                cartIds = cartIds,
                onFailure = any(),
                onSuccess = capture(cartOnSuccess),
            )
        } answers {
            cartOnSuccess.captured(carts)
        }

        // 카드 정보를 조회한다.
        every { cardRepository.loadCards() } returns CardFixture.getFixture().map { it.toModel() }

        // 유저 가용 포인트를 조회한다.
        val pointOnSuccess = slot<(Point) -> Unit>()
        every {
            pointRepository.loadPoint(
                onFailure = any(),
                onSuccess = capture(pointOnSuccess)
            )
        } answers {
            pointOnSuccess.captured(userPoint)
        }

        // 예상 적립 포인트를 조회한다.
        val predictionPointOnSuccess = slot<(Point) -> Unit>()
        every {
            pointRepository.loadPredictionSavePoint(
                orderPrice,
                onFailure = any(),
                onSuccess = capture(predictionPointOnSuccess),
            )
        } answers {
            predictionPointOnSuccess.captured(predictionPoint)
        }

        justRun { view.showCardItemsView(CardFixture.getFixture()) }
        justRun { view.showUserPointView(userPoint.toUIModel()) }
        justRun { view.setPointTextChangeListener(orderPrice, userPoint.toUIModel()) }
        justRun { view.showSavePredictionPointView(predictionPoint.toUIModel()) }
        justRun { view.showOrderPriceView(orderPrice) }
        justRun { view.showProductItemsView(carts.map { it.toUIModel() }) }
        justRun { view.setLayoutVisibility() }

        presenter.loadOrderProducts(cartIds)

        // 주문 성공 시 OrderId를 반환한다.
        val addOrderOnSuccess = slot<(Long) -> Unit>()
        every {
            orderRepository.addOrder(
                order,
                onFailure = any(),
                onSuccess = capture(addOrderOnSuccess),
            )
        } answers {
            addOrderOnSuccess.captured(orderId)
        }

        justRun { cartRepository.deleteLocalCarts(order.cartIds) }
        justRun { view.showOrderDetailView(orderId) }

        // when
        presenter.postOrder()

        // then
        verify { view.showCardItemsView(CardFixture.getFixture()) }
        verify { view.showUserPointView(userPoint.toUIModel()) }
        verify { view.setPointTextChangeListener(orderPrice, userPoint.toUIModel()) }
        verify { view.showSavePredictionPointView(predictionPoint.toUIModel()) }
        verify { view.showOrderPriceView(orderPrice) }
        verify { view.showProductItemsView(carts.map { it.toUIModel() }) }
        verify { view.setLayoutVisibility() }
        verify { cartRepository.deleteLocalCarts(order.cartIds) }
        verify { view.showOrderDetailView(orderId) }
    }
}
