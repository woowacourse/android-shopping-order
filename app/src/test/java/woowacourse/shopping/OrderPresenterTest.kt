package woowacourse.shopping

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.presentation.mapper.toDomain
import woowacourse.shopping.presentation.model.OrderProductModel
import woowacourse.shopping.presentation.model.OrderProductsModel
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.presentation.order.OrderContract
import woowacourse.shopping.presentation.order.OrderPresenter
import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.repository.UserRepository

class OrderPresenterTest {
    private lateinit var view: OrderContract.View
    private lateinit var orderRepository: OrderRepository
    private lateinit var userRepository: UserRepository
    private lateinit var presenter: OrderPresenter
    private val orderProductsModel = OrderProductsModel(
        listOf(
            makeOrderProductModel(1),
            makeOrderProductModel(2),
            makeOrderProductModel(3),
            makeOrderProductModel(4),
        )
    )

    private fun makeOrderProductModel(id: Int): OrderProductModel =
        OrderProductModel(ProductModel(id, "", "", 1000), 1)

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        presenter = OrderPresenter(view, orderProductsModel, orderRepository, userRepository)
    }

    @Test
    fun `주문 상품들을 보여준다`() {
        // when
        presenter.loadOrderItems()
        // then
        verify { view.setOrderItems(orderProductsModel.list) }
    }

    @Test
    fun `사용자의 남은 포인트를 보여준다`() {
        // given
        val slotUser = slot<(User?) -> Unit>()
        every { userRepository.getUserPoint(capture(slotUser)) } answers {
            slotUser.captured.invoke(User(1, Point(1000)))
        }
        // when
        presenter.showUserTotalPoint()
        // then
        verify { view.setUserTotalPoint(1000) }
    }

    @Test
    fun `남은 포인트보다 많은 포인트를 입력하면, 남은 포인트를 보여준다`() {
        // given
        presenter = OrderPresenter(
            view,
            orderProductsModel,
            orderRepository,
            userRepository,
            userTotalPoint = 1000
        )
        // when
        presenter.checkPointAble("1200")
        // then
        verify { view.setUsagePoint("1000") }
    }

    @Test
    fun `0보다 적은 포인트를 입력하면, 빈 값을 보여준다`() {
        // given
        presenter = OrderPresenter(
            view,
            orderProductsModel,
            orderRepository,
            userRepository,
            userTotalPoint = 1000
        )
        // when
        presenter.checkPointAble("-1")
        // then
        verify { view.setUsagePoint("") }
    }

    @Test
    fun `개별 주문 상품의 금액 총 합을 보여준다`() {
        // given
        val orderProductModel = OrderProductModel(ProductModel(1, "", "", 1000), 3)
        // when
        presenter.updateOrderProductTotalPrice(orderProductModel)
        // then
        verify { view.setOrderProductTotalPrice(3000) }
    }

    @Test
    fun `모든 주문 상품들의 총 합을 보여준다`() {
        // when
        presenter.showOrderPrice()
        // then
        verify { view.setOrderPrice(4000) }
    }

    @Test
    fun `결제 금액을 보여준다`() {
        // given
        presenter = OrderPresenter(
            view,
            orderProductsModel,
            orderRepository,
            userRepository,
            userTotalPoint = 1000,
            usagePoint = 1000
        )
        // when
        presenter.showPaymentPrice()
        // then
        verify { view.setPaymentPrice(3000) }
    }

    @Test
    fun `결제하기를 누르면, 결제한 후에 결제 완료 메세지를 띄운다`() {
        // given
        presenter = OrderPresenter(
            view,
            orderProductsModel,
            orderRepository,
            userRepository,
            userTotalPoint = 1000,
            usagePoint = 1000
        )
        val slot = slot<(String?) -> Unit>()
        every {
            orderRepository.addOrder(
                1000,
                orderProductsModel.toDomain(),
                capture(slot)
            )
        } answers {
            slot.captured.invoke("good")
        }
        // when
        presenter.order()
        // then
        verify { view.showAddOrderComplete("good") }
    }
}
