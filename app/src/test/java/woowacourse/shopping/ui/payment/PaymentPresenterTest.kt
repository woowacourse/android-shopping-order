package woowacourse.shopping.ui.payment

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.domain.User
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.UserRepository
import woowacourse.shopping.ui.BasketFixture
import woowacourse.shopping.ui.UserFixture
import woowacourse.shopping.ui.mapper.toUiModel
import woowacourse.shopping.ui.model.BasketProductUiModel

class PaymentPresenterTest {

    private lateinit var view: PaymentContract.View
    private lateinit var basketProducts: List<BasketProductUiModel>
    private lateinit var userRepository: UserRepository
    private lateinit var orderRepository: OrderRepository
    private lateinit var presenter: PaymentContract.Presenter

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        basketProducts = BasketFixture.createBasket()
            .products
            .filter { it.checked }
            .map { it.toUiModel() }

        userRepository = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        presenter = PaymentPresenter(
            view = view,
            totalPrice = basketProducts.sumOf { it.product.price.value },
            basketProducts = basketProducts,
            userRepository = userRepository,
            orderRepository = orderRepository
        )
    }

    @Test
    fun `저장소로부터 유저 정보를 받아온 후 뷰를 초기화한다`() {
        // given
        val user = UserFixture.createUser()
        val slotInitView = slot<(user: User) -> Unit>()
        val totalPrice = basketProducts.sumOf { it.product.price.value }

        every {
            userRepository.getUser(
                onReceived = capture(slotInitView),
                onFailure = any()
            )
        } answers {
            slotInitView.captured.invoke(user)
        }

        // when: 유저 정보를 저장소로부터 받아온다
        presenter.getUser()

        // then: 받아온 유저 정보를 가지고 뷰를 초기화한다
        verify {
            view.initView(
                user = user.toUiModel(),
                basketProducts = basketProducts,
                totalPrice = totalPrice
            )
        }
    }

    @Test
    fun `유저 정보를 받지 못한 경우 에러 메시지를 띄운다`() {
        // given
        val slotShowErrorMessage = slot<(errorMessage: String) -> Unit>()
        val errorMessage = "유저 정보를 불러올 수 없습니다."

        every {
            userRepository.getUser(
                onReceived = any(),
                onFailure = capture(slotShowErrorMessage)
            )
        } answers {
            slotShowErrorMessage.captured.invoke(errorMessage)
        }

        // when: 유저 정보를 저장소로부터 받아온다
        presenter.getUser()

        // then: 받아오지 못한 경우 에러 메시지를 띄운다
        verify {
            view.showErrorMessage(errorMessage)
        }
    }

    @Test
    fun `주문을 추가하면 저장소에 저장된 후 주문 상세 화면을 보여준다`() {
        // given
        val basketsId = basketProducts.map { it.id }
        val totalPrice = basketProducts.sumOf { it.product.price.value }
        val slotShowOrderDetail = slot<(orderId: Int) -> Unit>()
        val usingPoint = 1000
        val orderId = 1

        every {
            orderRepository.addOrder(
                basketIds = basketsId,
                usingPoint = usingPoint,
                totalPrice = totalPrice,
                onAdded = capture(slotShowOrderDetail),
                onFailed = any()
            )
        } answers {
            slotShowOrderDetail.captured.invoke(orderId)
        }

        // when: 주문을 추가한다
        presenter.addOrder(usingPoint)

        // then: 받아온 주문 식별번호를 가지고 주문 상세 화면을 보여준다
        verify { view.showOrderDetail(orderId = 1) }
    }

    @Test
    fun `주문을 추가에 실패한 경우 에러 메시지를 띄운다`() {
        // given
        val slotShowErrorMessage = slot<(errorMessage: String) -> Unit>()
        val errorMessage = "주문 추가에 실패했습니다"

        every {
            orderRepository.addOrder(
                basketIds = any(),
                usingPoint = any(),
                totalPrice = any(),
                onAdded = any(),
                onFailed = capture(slotShowErrorMessage)
            )
        } answers {
            slotShowErrorMessage.captured.invoke(errorMessage)
        }

        // when: 주문을 추가한다
        presenter.addOrder(0)

        // then: 실패한 경우 에러메시지를 띄운다
        verify { view.showErrorMessage(errorMessage) }
    }
}
