package woowacourse.shopping.ui.cart

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import woowacourse.shopping.data.cart.CartItem
import woowacourse.shopping.data.cart.CartItemRepository
import woowacourse.shopping.data.discount.DiscountInfoRepository
import woowacourse.shopping.data.order.OrderRepository
import woowacourse.shopping.data.product.Product
import woowacourse.shopping.ui.cart.uistate.CartItemUIState
import woowacourse.shopping.ui.cart.uistate.OrderPriceInfoUIState
import java.util.concurrent.CompletableFuture

internal class CartPresenterTest {

    private lateinit var view: FakeView
    private lateinit var cartItemRepository: CartItemRepository
    private lateinit var orderRepository: OrderRepository
    private lateinit var discountInfoRepository: DiscountInfoRepository
    private lateinit var sut: CartPresenter

    @BeforeEach
    fun setUp() {
        view = FakeView()
        cartItemRepository = mockk()
        orderRepository = mockk()
        discountInfoRepository = mockk()
        sut = CartPresenter(view, cartItemRepository, orderRepository, discountInfoRepository)
    }

    @DisplayName("한 페이지에 보여줄 장바구니 개수가 5개이고 장바구니 아이템 저장소에서 한 페이지에 보여줄 장바구니 아이템의 개수보다 더 많은 장바구니 아이템을 불러오는 게 성공했고 선택한 장바구니 아이템이 하나도 없을 때 첫 번째 페이지의 장바구니 아이템을 불러오면 그 장바구니 아이템들을 뷰에 보여주고 이전 페이지를 요청할 수 없는 상태가 되고 1 페이지라고 보여지고 다음 페이지를 요청할 수 있는 상태가 되고 뷰가 모두 선택이 되어 있지 않은 상태가 된다")
    @Test
    fun test1() {
        val dummyCartItems = listOf(
            CartItem(1L, Product(1L, "", "", 10), 1),
            CartItem(2L, Product(2L, "", "", 10), 1),
            CartItem(3L, Product(3L, "", "", 10), 1),
            CartItem(4L, Product(4L, "", "", 10), 1),
            CartItem(5L, Product(5L, "", "", 10), 1),
            CartItem(6L, Product(6L, "", "", 10), 1),
        )
        every { cartItemRepository.getCartItems() } returns CompletableFuture.supplyAsync {
            Result.success(dummyCartItems)
        }

        sut.onLoadCartItemsOfFirstPage()

        val cartItemUiStates = dummyCartItems.map { CartItemUIState.create(it, false) }.take(5)
        assertAll(
            { assertThat(view.mCartItems).isEqualTo(cartItemUiStates) },
            { assertThat(view.mStateThatCanRequestPreviousPage).isFalse },
            { assertThat(view.mPage).isEqualTo(1) },
            { assertThat(view.mStateThatCanRequestNextPage).isTrue },
            { assertThat(view.mStateOfAllSelection).isFalse }
        )
    }

    class FakeView : CartContract.View {
        var mStateThatCanRequestPreviousPage: Boolean? = null
        var mStateThatCanRequestNextPage: Boolean? = null
        var mPageUIVisibility: Boolean? = null
        var mPage: Int? = null
        var mCartItems: List<CartItemUIState>? = null
        var mInitScroll: Boolean? = null
        var mStateOfAllSelection: Boolean? = null
        var mOrderPrice: Int? = null
        var mOrderCount: Int? = null
        var mCanOrder: Boolean? = null
        var mOrderResult: Long? = null
        var mCanSeeOrderPriceInfo: Boolean? = null
        var mOrderPriceInfo: OrderPriceInfoUIState? = null
        var mMessage: String? = null
        var isRefreshed: Boolean = false

        override fun setStateThatCanRequestPreviousPage(canRequest: Boolean) {
            mStateThatCanRequestPreviousPage = canRequest
        }

        override fun setStateThatCanRequestNextPage(canRequest: Boolean) {
            mStateThatCanRequestNextPage = canRequest
        }

        override fun setPageUIVisibility(isVisible: Boolean) {
            mPageUIVisibility = isVisible
        }

        override fun setPage(page: Int) {
            mPage = page
        }

        override fun setCartItems(cartItems: List<CartItemUIState>, initScroll: Boolean) {
            mCartItems = cartItems
            mInitScroll = initScroll
        }

        override fun setStateOfAllSelection(isAllSelected: Boolean) {
            mStateOfAllSelection = isAllSelected
        }

        override fun setOrderPrice(price: Int) {
            mOrderPrice = price
        }

        override fun setOrderCount(count: Int) {
            mOrderCount = count
        }

        override fun setCanOrder(canOrder: Boolean) {
            mCanOrder = canOrder
        }

        override fun showOrderResult(orderId: Long) {
            mOrderResult = orderId
        }

        override fun setCanSeeOrderPriceInfo(canSeeOrderPriceInfo: Boolean) {
            mCanSeeOrderPriceInfo = canSeeOrderPriceInfo
        }

        override fun showOrderPriceInfo(orderPriceInfo: OrderPriceInfoUIState) {
            mOrderPriceInfo = orderPriceInfo
        }

        override fun showMessage(message: String) {
            mMessage = message
        }

        override fun refresh() {
            isRefreshed = true
        }
    }
}
