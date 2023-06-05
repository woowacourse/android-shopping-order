package woowacourse.shopping.presentation.cart

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.presentation.CartFixture
import woowacourse.shopping.presentation.mapper.toModel
import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.presentation.view.cart.CartContract
import woowacourse.shopping.presentation.view.cart.CartPresenter
import woowacouse.shopping.data.repository.cart.CartRepository
import woowacouse.shopping.data.repository.product.ProductRepository
import woowacouse.shopping.model.cart.CartProduct

class CartPresenterTest {
    private lateinit var presenter: CartContract.Presenter
    private lateinit var view: CartContract.View
    private lateinit var cartRepository: CartRepository
    private lateinit var productRepository: ProductRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        cartRepository = mockk()
        productRepository = mockk()

        presenter = CartPresenter(view, cartRepository)
    }

    @Test
    fun `장바구니 데이터를 받아와 보여준다`() {
        // given
        val slotCartProducts = slot<(List<CartProduct>) -> Unit>()
        every {
            cartRepository.loadAllCarts(
                onFailure = any(),
                onSuccess = capture(slotCartProducts)
            )
        } answers {
            slotCartProducts.captured(CartFixture.getFixture().map { it.toModel() })
        }

        val slotItems = slot<List<CartProductModel>>()
        val slotTotalPrice = slot<Int>()

        every { cartRepository.getAllLocalCart() } returns CartFixture.getFixture()
            .map { it.toModel() }
        justRun { view.setEnableOrderButton(true) }
        justRun { view.showCartItemsView(capture(slotItems)) }
        justRun { view.showTotalPriceView(capture(slotTotalPrice)) }
        justRun { view.setEnableLeftButton(false) }
        justRun { view.setEnableRightButton(false) }
        justRun { view.setAllCartChecked(true) }
        justRun { view.setLayoutVisibility() }

        // when
        presenter.initCartItems()

        // then
        val actualItemsCount = slotItems.captured.size
        val actualTotalPrice = slotTotalPrice.captured
        val expectedItemsCount = CartFixture.getFixture().size
        val expectedTotalPrice = 39_900

        assertEquals(expectedItemsCount, actualItemsCount)
        assertEquals(expectedTotalPrice, actualTotalPrice)

        verify { cartRepository.getAllLocalCart() }
        verify { view.setEnableOrderButton(true) }
        verify { view.showCartItemsView(slotItems.captured) }
        verify { view.showTotalPriceView(slotTotalPrice.captured) }
        verify { view.setEnableLeftButton(false) }
        verify { view.setEnableRightButton(false) }
        verify { view.setAllCartChecked(true) }
        verify { view.setLayoutVisibility() }
    }

    @Test
    fun `카트 데이터가 하나 삭제된다`() {
        // given
        val targetCartId = 1L
        presenter.setPageNation(CartFixture.getFixture(), 1)

        justRun { cartRepository.deleteLocalCart(targetCartId) }
        justRun { cartRepository.deleteCart(targetCartId) }

        justRun { view.setEnableOrderButton(true) }
        justRun { view.setEnableLeftButton(false) }
        justRun { view.setEnableRightButton(false) }

        val slotItems = slot<List<CartProductModel>>()
        justRun { view.showChangedCartItemsView(capture(slotItems)) }

        // when
        presenter.deleteCartItem(targetCartId)

        // then
        val actual = slotItems.captured
        val expected = listOf(
            CartProductModel(
                2L,
                ProductModel(
                    id = 2L,
                    title = "치킨치킨",
                    price = 15_000,
                    imageUrl = "https://product-image.kurly.com/product/image/2c392328-104a-4fef-8222-c11be9c5c35f.jpg",
                ),
                1,
                true
            ),
            CartProductModel(
                3L,
                ProductModel(
                    id = 3L,
                    title = "피자피자",
                    price = 10_000,
                    imageUrl = "https://product-image.kurly.com/product/image/2c392328-104a-4fef-8222-c11be9c5c35f.jpg",
                ),
                0,
                true
            ),
        )
        assertEquals(expected, actual)
        verify { cartRepository.deleteLocalCart(targetCartId) }
        verify { cartRepository.deleteCart(targetCartId) }
        verify { view.setEnableOrderButton(true) }
        verify { view.setEnableLeftButton(false) }
        verify { view.setEnableRightButton(false) }
        verify { view.showChangedCartItemsView(actual) }
    }

    @Test
    fun `이전 페이지가 존재하는지 계산해 존재하면 뷰에 보여준다`() {
        // given
        presenter.setPageNation(CartFixture.getFixture(), 1)

        val slot = slot<Int>()
        justRun { view.showPageCountView(capture(slot)) }

        // when
        presenter.setPreviousPage()

        // then
        verify { view.showPageCountView(slot.captured) }
    }

    @Test
    fun `다음 페이지가 존재하는지 계산해 존재하면 뷰에 보여준다`() {
        // given
        presenter.setPageNation(CartFixture.getFixture(), 1)

        val slot = slot<Int>()
        justRun { view.showPageCountView(capture(slot)) }

        // when
        presenter.setNextPage()

        // then
        verify { view.showPageCountView(slot.captured) }
    }

    @Test
    fun `장바구니 아이디가 1인 상품의 개수 0이면 장바구니에서 제거한다`() {
        // given
        presenter.setPageNation(CartFixture.getFixture(), 1)

        val targetCart = CartProductModel(
            1L,
            ProductModel(
                id = 1L,
                title = "[선물세트][밀크바오밥] 퍼퓸 화이트 4종 선물세트 (샴푸+트리트먼트+바디워시+바디로션)",
                price = 24_900,
                imageUrl = "https://product-image.kurly.com/product/image/2c392328-104a-4fef-8222-c11be9c5c35f.jpg",
            ),
            0,
            true
        )

        val slotOnSuccess = slot<() -> Unit>()
        every {
            cartRepository.updateCartCount(
                targetCart.toModel(),
                onFailure = any(),
                onSuccess = capture(slotOnSuccess)
            )
        } answers {
            slotOnSuccess.captured.invoke()
        }

        justRun { cartRepository.deleteLocalCart(targetCart.id) }
        justRun { cartRepository.deleteCart(targetCart.id) }
        justRun { view.setEnableLeftButton(false) }
        justRun { view.setEnableRightButton(false) }

        // when
        presenter.updateProductCount(targetCart.id, 0)

        // then
        verify { cartRepository.deleteLocalCart(targetCart.id) }
        verify { cartRepository.deleteCart(targetCart.id) }
        verify { view.setEnableLeftButton(false) }
        verify { view.setEnableRightButton(false) }
    }

    @Test
    fun `상품의 체크 상태를 갱신할 수 있다`() {
        // given
        presenter.setPageNation(CartFixture.getFixture(), 1)
        justRun { cartRepository.updateLocalCartChecked(1L, false) }
        justRun { view.setAllCartChecked(false) }

        // when
        presenter.updateProductChecked(1L, false)

        // then
        justRun { cartRepository.updateLocalCartChecked(1L, false) }
        verify { view.setAllCartChecked(false) }
    }

    @Test
    fun `체크 되어있는 상품들의 총 가격을 보여준다`() {
        // given
        presenter.setPageNation(CartFixture.getFixture(), 1)

        val slot = slot<Int>()
        justRun { view.showTotalPriceView(capture(slot)) }

        // when
        presenter.calculateTotalPrice()

        // then
        val actual = slot.captured
        val expected = 24_900 + 15_000
        assertEquals(expected, actual)
        verify { view.showTotalPriceView(actual) }
    }

    @Test
    fun `현재 Checked 상태인 장바구니들을 주문한다`() {
        // given
        presenter.setPageNation(CartFixture.getFixture(), 1)

        val slot = slot<ArrayList<Long>>()
        justRun { view.showOrderView(capture(slot)) }

        // when
        presenter.showOrder()

        // then
        val actual = slot.captured
        val expected = arrayListOf(1L, 2L, 3L)
        assertEquals(expected, actual)
        verify { view.showOrderView(actual) }
    }
}
