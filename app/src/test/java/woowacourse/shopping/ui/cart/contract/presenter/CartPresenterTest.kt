package woowacourse.shopping.ui.cart.contract.presenter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.domain.model.CartItems
import com.example.domain.model.CartProduct
import com.example.domain.model.Product
import com.example.domain.repository.CartRepository
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.CartUIModel
import woowacourse.shopping.model.ProductUIModel
import woowacourse.shopping.ui.cart.contract.CartContract

internal class CartPresenterTest {
    private lateinit var view: CartContract.View
    private lateinit var presenter: CartPresenter
    private lateinit var cartRepository: CartRepository
    private lateinit var cartItems: CartItems

    private val fakeProduct: Product = Product(
        1,
        "[사미헌] 갈비탕",
        12000,
        "https://img-cf.kurly.com/cdn-cgi/image/quality=85,width=676/shop/data/goods/1648206780555l0.jpeg",
    )

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
        presenter = CartPresenter(cartRepository, view)
        cartItems = mockk(relaxed = true)
    }

    @Test
    fun `장바구니에 담긴 상품을 보여준다`() {
        // given
        val slot = slot<CartUIModel>()
        every { cartRepository.getAllProductInCart().getOrNull() } returns emptyList()
        every { cartRepository.getSubList(any(), any()).getOrNull() } returns emptyList()
        every { view.setCarts(any(), capture(slot)) } answers { nothing }

        // When
        presenter.setUpCarts()

        // Then
        val expectedCartUIModel = CartUIModel(false, false, 1)
        Assert.assertEquals(expectedCartUIModel, slot.captured)
        verify(exactly = 1) { view.setCarts(any(), expectedCartUIModel) }
    }

    @Test
    fun `장바구니에 담긴 상품을 삭제한다`() {
        val idToRemove = 1L
        every { cartRepository.getAllProductInCart().getOrNull() } returns emptyList()
        every { cartRepository.getSubList(any(), any()).getOrNull() } returns emptyList()
        every { cartRepository.remove(idToRemove).getOrNull() } returns null

        // when
        presenter.removeItem(idToRemove)

        // then
        verify(exactly = 1) { cartRepository.remove(idToRemove).getOrNull() }
        verify(exactly = 1) { view.setCarts(any(), any()) }
    }

    @Test
    fun `다음 페이지 상품을 불러온다`() {
        // given
        val slot = slot<CartUIModel>()
        every { cartRepository.getSubList(any(), any()).getOrNull() } returns emptyList()
        every { cartRepository.getAllProductInCart().getOrNull() } returns listOf(
            CartProduct(
                1,
                1,
                fakeProduct,
            ),
        )
        every { view.setCarts(any(), capture(slot)) } answers { nothing }
        // when
        presenter.pageUp()
        // then
        Assert.assertEquals(slot.captured, CartUIModel(false, true, 2))
        verify(exactly = 1) { view.setCarts(any(), CartUIModel(false, true, 2)) }
    }

    @Test
    fun `이전 페이지 상품을 불러온다`() {
        // given
        val slot = slot<CartUIModel>()
        every { cartRepository.getSubList(any(), any()).getOrNull() } returns emptyList()
        every { cartRepository.getAllProductInCart().getOrNull() } returns listOf(
            CartProduct(
                1,
                1,
                fakeProduct,
            ),
        )
        every { view.setCarts(any(), capture(slot)) } answers { nothing }
        // when
        presenter.pageDown()
        // then
        Assert.assertEquals(slot.captured, CartUIModel(true, false, 0))
        verify { view.setCarts(any(), CartUIModel(true, false, 0)) }
    }

    @Test
    fun `상세 페이지로 이동한다`() {
        // given
        val slot = slot<ProductUIModel>()
        every { cartRepository.findById(any()).getOrNull() } returns CartProduct(1, 1, fakeProduct)
        every { view.navigateToItemDetail(capture(slot)) } answers { nothing }
        // when
        presenter.navigateToItemDetail(fakeProduct.id)
        // then
        assertEquals(slot.captured, fakeProduct.toUIModel())
        verify(exactly = 1) { view.navigateToItemDetail(fakeProduct.toUIModel()) }
    }

    @Test
    fun `장바구니에 선택된 아이템들의 가격을 알 수 있다`() {
        // given
        val slot = slot<Int>()
        every { cartItems.getPrice() } returns 0
        justRun { view.setCartItemsPrice(capture(slot)) }

        // when
        presenter.setCartItemsPrice()

        // then
        assertEquals(cartItems.getPrice(), slot.captured)
        verify { view.setCartItemsPrice(slot.captured) }
    }

    @Test
    fun `전체 체크박스의 상태를 지정할 수 있다`() {
        // given
        val slot = slot<Boolean>()
        every { cartRepository.getSubList(any(), any()).getOrNull() } returns listOf(
            CartProduct(
                1,
                1,
                fakeProduct,
            ),
        )
        every { view.setAllCheckbox(capture(slot)) } answers { nothing }
        // when
        presenter.setAllCheckbox()
        // then
        Assert.assertEquals(slot.captured, false)
        verify { view.setAllCheckbox(slot.captured) }
    }

    @Test
    fun `총 주문한 상품의 수를 알 수 있다`() {
        // given
        val slot = slot<Int>()
        every { cartItems.getSize() } returns 0
        every { view.setAllOrderCount(capture(slot)) } answers { nothing }
        // when
        presenter.setAllOrderCount()
        // then
        Assert.assertEquals(slot.captured, cartItems.getSize())
        verify { view.setAllOrderCount(slot.captured) }
    }

    /*@Test
    fun `상품 수량이 증가한다`() {
        // given
        every { cartRepository.getSubList(0, 5).getOrNull() } returns listOf(
            CartProduct(
                1,
                1,
                fakeProduct,
            ),
        )
        // when
        presenter.setUpCarts()
        presenter.increaseCount(fakeProduct.id)
        // then
        assertEquals(presenter)
    }*/

    /*
@Test
fun `상품 수량이 감소한다`() {
  // given
  every { cartRepository.getSubList(0, 5) } returns listOf(
      CartProduct(
          fakeProduct,
          1,
          true,
      ),
  )
  // when
  presenter.setUpCarts()
  presenter.increaseCount(fakeProduct.id)
  presenter.decreaseCount(fakeProduct.id)
  // then
  assertEquals(presenter.countLiveDatas[fakeProduct.id]?.value, 0)
}*/
}
