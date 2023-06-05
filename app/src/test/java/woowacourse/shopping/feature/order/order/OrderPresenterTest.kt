package woowacourse.shopping.feature.order.order

import com.example.domain.model.CartProduct
import com.example.domain.repository.CartRepository
import com.example.domain.repository.OrderRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.feature.CartFixture
import woowacourse.shopping.feature.Product
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
        val fakeCartProducts = CartFixture.getMockCarts(
            Triple(1L, Product(1L, 3000), 3),
            Triple(2L, Product(2L, 3000), 3),
            Triple(3L, Product(3L, 3000), 3),
        )

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
        val fakeCartProducts = CartFixture.getMockCarts(
            Triple(1L, Product(1L, 3000), 3),
            Triple(2L, Product(2L, 3000), 3),
            Triple(3L, Product(3L, 3000), 3),
        )

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
        val fakeCartProducts = CartFixture.getMockCarts(
            Triple(1L, Product(1L, 10000), 3),
            Triple(2L, Product(2L, 10000), 3),
            Triple(3L, Product(3L, 10000), 3),
        )

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
        val fakeCartProducts = CartFixture.getMockCarts(
            Triple(1L, Product(1L, 10000), 50),
            Triple(2L, Product(2L, 10000), 50),
            Triple(3L, Product(3L, 10000), 50),
        )

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
        val fakeCartProducts = CartFixture.getMockCarts(
            Triple(1L, Product(1L, 10000), 10),
            Triple(2L, Product(2L, 10000), 10),
            Triple(3L, Product(3L, 10000), 10),
        )

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
//    private val mockCartProducts = CartFixture.getMockCarts(List(10){Triple(it.toLong(), Product(it.toLong(), 3000),3)})
//        CartFixture.getMockCarts(
//
//            Triple(4L, Product(4L, 3000), 3),
//            Triple(5L, Product(5L, 3000), 3),
//            Triple(6L, Product(6L, 3000), 3),
//            Triple(7L, Product(7L, 3000), 3),
//            Triple(8L, Product(8L, 3000), 3),
//            Triple(9L, Product(9L, 3000), 3),
//            Triple(10L, Product(10L, 3000), 3),
//            Triple(11L, Product(11L, 3000), 3),
//            Triple(12L, Product(12L, 3000), 3),
//            Triple(13L, Product(13L, 3000), 3),
//            Triple(14L, Product(14L, 3000), 3),
//        )
}
