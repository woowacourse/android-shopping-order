package woowacourse.shopping.feature.order

import com.example.domain.model.CartProduct
import com.example.domain.repository.CartRepository
import com.example.domain.repository.OrderRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.feature.ProductFixture
import woowacourse.shopping.mapper.toPresentation

class OrderPresenterTest {
    lateinit var presenter: OrderContract.Presenter
    lateinit var view: OrderContract.View
    lateinit var cartRepository: CartRepository
    lateinit var orderRepository: OrderRepository
    private val cartIds = listOf<Long>(1, 2, 3)

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        presenter = OrderPresenter(cartIds, view, cartRepository, orderRepository)
    }

    @Test
    fun `화면에 주문할 상품 목록이 나타난다`() {
        // Given: cartId 를 이용해 상품을 er4받아올 수 있는 상태이다.
        val fakeCartProducts = ProductFixture.makeCartProducts(cartIds, 1000, 5)

        every { cartRepository.getAll(onSuccess = any(), onFailure = any()) } answers {
            firstArg<(List<CartProduct>) -> Unit>().invoke(
                fakeCartProducts,
            )
        }

        // When: cartId 를 통해 상품을 요청한다.
        presenter.requestProducts()

        // Then: 주문할 상품 목록이 노출된다.
        verify { view.showProducts(fakeCartProducts.map { it.toPresentation() }) }
    }

    @Test
    fun `주문 금액이 할인 정책에 해당하지 않는다면 할인 정보를 띄우지 않는다`() {
        // Given: cartId 를 이용해 상품을 받아올 수 있는 상태이다. 주문 가격이 할인 정책에 해당하지 않는다.
        val fakeCartProducts = ProductFixture.makeCartProducts(cartIds, 1000, 5)

        every { cartRepository.getAll(onSuccess = any(), onFailure = any()) } answers {
            firstArg<(List<CartProduct>) -> Unit>().invoke(fakeCartProducts)
        }

        // When: 주문할 상품을 불러오고 주문 가격을 계산한 후 그에 따른 할인 금액을 계산한다.
        presenter.requestProducts()

        // Then: 할인 가능 여부를 알 수 있다.
        verify { view.showNonDiscount() }
    }

    @Test
    fun `주문 금액이 할인 정책에 해당한다면 할인 정보를 띄운다`() {
        // Given: cartId 를 이용해 상품을 받아올 수 있는 상태이다. 주문 가격이 할인 정책에 해당한다.
        val fakeCartProducts = ProductFixture.makeCartProducts(cartIds, 5000, 5)

        every { cartRepository.getAll(onSuccess = any(), onFailure = any()) } answers {
            firstArg<(List<CartProduct>) -> Unit>().invoke(fakeCartProducts)
        }

        // When: 주문할 상품을 불러오고 주문 가격을 계산한 후 그에 따른 할인 금액을 계산한다.
        presenter.requestProducts()

        // Then: 할인 가능 여부를 알 수 있다.
        verify { view.showDiscount(any(), any()) }
    }

    @Test
    fun `상품의 총 개수가 99개를 넘는다면 주문이 불가하다`() {
        // Given: 주문할 상품 불러오기가 완료되었고, 상품의 총 개수가 99개를 넘는다.
        val fakeCartProducts = ProductFixture.makeCartProducts(cartIds, 5000, 50)

        every { cartRepository.getAll(onSuccess = any(), onFailure = any()) } answers {
            firstArg<(List<CartProduct>) -> Unit>().invoke(fakeCartProducts)
        }

        presenter.requestProducts()

        // When: 주문 요청을 보낸다.
        presenter.order()

        // Then: 주문이 불가함을 알려준다.
        verify { view.failToOrder() }
    }

    @Test
    fun `상품의 총 개수가 99개를 넘지 않는다면 주문이 완료된다`() {
        // Given: 주문할 상품 불러오기가 완료되었고, 상품의 총 개수가 99개를 넘지 않는다.
        val fakeCartProducts = ProductFixture.makeCartProducts(cartIds, 5000, 10)

        every { cartRepository.getAll(onSuccess = any(), onFailure = any()) } answers {
            firstArg<(List<CartProduct>) -> Unit>().invoke(fakeCartProducts)
        }

        every {
            orderRepository.addOrder(
                cartIds = any(),
                totalPrice = any(),
                onSuccess = any(),
                onFailure = any(),
            )
        } answers {
            thirdArg<(Long) -> Unit>().invoke(1)
        }

        presenter.requestProducts()

        // When: 주문 요청을 보낸다.
        presenter.order()

        // Then: 주문 완료 화면을 띄운다.
        verify { view.succeedInOrder(1) }
    }
}
