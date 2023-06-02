package woowacourse.shopping.ui.cart

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.CartItem
import woowacourse.shopping.Product
import woowacourse.shopping.User
import woowacourse.shopping.async
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.order.DiscountPolicy
import woowacourse.shopping.domain.order.Payment
import woowacourse.shopping.repository.CartItemRepository
import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.repository.UserRepository
import woowacourse.shopping.ui.cart.uistate.CartItemUIState.Companion.toUIState
import woowacourse.shopping.ui.order.uistate.PaymentUIState.Companion.toUIState

class CartPresenterTest {
    private lateinit var view: CartContract.View
    private lateinit var presenter: CartPresenter
    private lateinit var cartItemRepository: CartItemRepository
    private lateinit var orderRepository: OrderRepository
    private lateinit var userRepository: UserRepository
    private val cartItems = listOf(
        CartItem(
            id = 0,
            quantity = 2,
            product = Product(
                price = 1000
            )
        ), CartItem(
            id = 1,
            quantity = 3,
            product = Product(
                price = 2000
            )
        )
    )

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        cartItemRepository = mockk()
        orderRepository = mockk()
        userRepository = mockk()

        every { userRepository.findCurrent() } returns async(User())
    }

    @Test
    fun 장바구니의_다음_페이지를_불러오고_표시한다() {
        // given
        presenter = CartPresenter(
            view,
            cartItemRepository,
            orderRepository,
            userRepository,
            pageSize = 1,
            selectedCart = Cart(cartItems.toSet()),
            currentPage = 1
        )

        every {
            cartItemRepository.findAllOrderByAddedTime(any(), any(), any())
        } returns async(cartItems)

        every { cartItemRepository.countAll(any()) } returns async(2)

        // when
        presenter.loadCartItemsOfNextPage()

        // then
        verify { view.setOrderPrice(8000) }
        verify { view.setOrderCount(2) }

        verify { view.setCartItems(cartItems.map { it.toUIState(true) }, true) }

        verify { view.setStateThatCanRequestPreviousPage(true) }
        verify { view.setStateThatCanRequestNextPage(false) }
        verify { view.setStateThatCanRequestPage(true) }
        verify { view.setPage(2) }

        verify { view.setStateOfAllSelection(true) }
    }

    @Test
    fun 장바구니의_이전_페이지를_불러오고_표시한다() {
        // given
        presenter = CartPresenter(
            view,
            cartItemRepository,
            orderRepository,
            userRepository,
            pageSize = 1,
            selectedCart = Cart(cartItems.toSet()),
            currentPage = 2
        )

        every {
            cartItemRepository.findAllOrderByAddedTime(any(), any(), any())
        } returns async(cartItems)

        every { cartItemRepository.countAll(any()) } returns async(2)

        // when
        presenter.loadCartItemsOfPreviousPage()

        // then
        verify { view.setCartItems(cartItems.map { it.toUIState(true) }, true) }

        verify { view.setStateThatCanRequestPreviousPage(false) }
        verify { view.setStateThatCanRequestNextPage(true) }
        verify { view.setStateThatCanRequestPage(true) }
        verify { view.setPage(1) }

        verify { view.setStateOfAllSelection(true) }
    }

    @Test
    fun 장바구니의_마지막_페이지를_불러오고_표시한다() {
        // given
        presenter = CartPresenter(
            view,
            cartItemRepository,
            orderRepository,
            userRepository,
            pageSize = 1,
            selectedCart = Cart(cartItems.toSet()),
            currentPage = 2
        )

        every { cartItemRepository.countAll(any()) } returns async(2)

        every {
            cartItemRepository.findAllOrderByAddedTime(any(), any(), any())
        } returns async(cartItems)

        // when
        presenter.loadCartItemsOfLastPage()

        // then
        verify { view.setCartItems(cartItems.map { it.toUIState(true) }, true) }

        verify { view.setStateThatCanRequestPreviousPage(true) }
        verify { view.setStateThatCanRequestNextPage(false) }
        verify { view.setStateThatCanRequestPage(true) }
        verify { view.setPage(2) }

        verify { view.setStateOfAllSelection(true) }
    }

    @Test
    fun 장바구니_상품을_제거한다() {
        // given
        presenter = CartPresenter(
            view,
            cartItemRepository,
            orderRepository,
            userRepository,
            pageSize = 1,
            selectedCart = Cart(cartItems.toSet()),
            currentPage = 1
        )

        every { cartItemRepository.deleteById(any(), any()) } returns async(Unit)

        every { cartItemRepository.countAll(any()) } returns async(1)

        // when
        presenter.deleteCartItem(0)

        // then
        verify { view.setStateThatCanRequestPreviousPage(false) }
        verify { view.setStateThatCanRequestNextPage(false) }
        verify { view.setStateThatCanRequestPage(false) }
        verify { view.setPage(1) }
    }

    @Test
    fun 장바구니_상품을_선택한다() {
        // given
        presenter = CartPresenter(
            view,
            cartItemRepository,
            orderRepository,
            userRepository,
            pageSize = 1,
            selectedCart = Cart(cartItems.toSet()),
            currentPage = 1
        )

        every {
            cartItemRepository.findById(any(), any())
        } returns async(
            CartItem(
                id = 1,
                quantity = 2,
                Product(price = 1000)
            )
        )

        every {
            cartItemRepository.findAllOrderByAddedTime(any(), any(), any())
        } returns async(cartItems)

        // when
        presenter.updateSelectionCartItem(2, true)

        // then
        verify { view.setStateOfAllSelection(true) }

        verify { view.setOrderPrice(10000) }
        verify { view.setOrderCount(3) }
    }

    @Test
    fun 장바구니_현재_페이지의_상품을_모두_선택한다() {
        // given
        presenter = CartPresenter(
            view,
            cartItemRepository,
            orderRepository,
            userRepository,
            pageSize = 1,
            selectedCart = Cart(cartItems.toSet()),
            currentPage = 1
        )

        every {
            cartItemRepository.findAllOrderByAddedTime(any(), any(), any())
        } returns async(cartItems)

        // when
        presenter.updateSelectionTotalCartItems(true)

        // then
        verify { view.setOrderPrice(8000) }
        verify { view.setOrderCount(2) }
    }

    @Test
    fun 장바구니_상품의_수량을_증가한다() {
        // given
        presenter = CartPresenter(
            view,
            cartItemRepository,
            orderRepository,
            userRepository,
            pageSize = 1,
            selectedCart = Cart(cartItems.toSet()),
            currentPage = 1
        )

        val plusCartItems = listOf(
            CartItem(
                id = 0,
                quantity = 3,
                product = Product(
                    price = 1000
                )
            ), CartItem(
                id = 1,
                quantity = 3,
                product = Product(
                    price = 2000
                )
            )
        )

        every {
            cartItemRepository.findById(any(), any())
        } returns async(cartItems[0])

        every {
            cartItemRepository.updateCountById(any(), any(), any())
        } returns async(Unit)

        every {
            cartItemRepository.findAllOrderByAddedTime(any(), any(), any())
        } returns async(plusCartItems)

        // when
        presenter.plusQuantity(0)

        // then
        verify { view.setCartItems(plusCartItems.map { it.toUIState(true) }, false) }
    }

    @Test
    fun 장바구니_상품의_수량을_감소한다() {
        // given
        presenter = CartPresenter(
            view,
            cartItemRepository,
            orderRepository,
            userRepository,
            pageSize = 1,
            selectedCart = Cart(cartItems.toSet()),
            currentPage = 1
        )

        val minusCartItems = listOf(
            CartItem(
                id = 0,
                quantity = 1,
                product = Product(
                    price = 1000
                )
            ), CartItem(
                id = 1,
                quantity = 3,
                product = Product(
                    price = 2000
                )
            )
        )

        every {
            cartItemRepository.findById(any(), any())
        } returns async(cartItems[0])

        every {
            cartItemRepository.updateCountById(any(), any(), any())
        } returns async(Unit)

        every {
            cartItemRepository.findAllOrderByAddedTime(any(), any(), any())
        } returns async(minusCartItems)

        // when
        presenter.minusQuantity(0)

        // then
        verify { view.setCartItems(minusCartItems.map { it.toUIState(true) }, false) }
    }

    @Test
    fun 현재_상품의_할인_정보를_가져오고_표시한다() {
        // given
        presenter = CartPresenter(
            view,
            cartItemRepository,
            orderRepository,
            userRepository,
            pageSize = 1,
            selectedCart = Cart(cartItems.toSet()),
            currentPage = 1
        )

        val payment = Payment(
            listOf(
                DiscountPolicy(
                    "", 0.1, 1000
                )
            )
        )

        every {
            orderRepository.findDiscountPolicy(any(), any())
        } returns async(payment)

        // when
        presenter.checkPayment()

        // then
        verify { view.showPayment(payment.toUIState(), 7000) }
    }

    @Test
    fun 현재_선택한_상품을_주문한다() {
        // given
        presenter = CartPresenter(
            view,
            cartItemRepository,
            orderRepository,
            userRepository,
            pageSize = 1,
            selectedCart = Cart(cartItems.toSet()),
            currentPage = 1
        )

        every {
            orderRepository.save(any(), any())
        } returns async(0)

        // when
        presenter.placeOrder()

        // then
        verify { view.showOrderDetail(0) }
    }
}