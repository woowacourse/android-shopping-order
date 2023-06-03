package woowacourse.shopping.presentation.productlist

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.CartProductInfo
import woowacourse.shopping.Price
import woowacourse.shopping.Product
import woowacourse.shopping.presentation.mapper.toPresentation
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentProductRepository

class ProductListPresenterTest {
    private lateinit var presenter: ProductListContract.Presenter
    private lateinit var view: ProductListContract.View
    private val productRepository: ProductRepository = mockk(relaxed = true)
    private val recentProductRepository: RecentProductRepository = mockk(relaxed = true)
    private val cartRepository: CartRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        presenter =
            ProductListPresenter(view, productRepository, recentProductRepository, cartRepository)
    }

    private fun makeProduct(id: Int): Product = Product(id, "", "", Price(1000))
    private fun makeCartProduct(id: Int): CartProductInfo = CartProductInfo(id, makeProduct(id), 1)
    private fun makeCartProductCountZero(id: Int): CartProductInfo =
        CartProductInfo(1000 + id, makeProduct(id), 0)

    @Test
    fun 상품_목록과_장바구니_목록을_불러와_상품정보와_장바구니에_담긴_개수를_보여준다() {
        // given
        val slotProduct = slot<(List<Product>) -> Unit>()
        val slotCartProduct = slot<(List<CartProductInfo>) -> Unit>()
        every {
            productRepository.getProductsWithRange(
                0,
                20,
                onSuccess = capture(slotProduct)
            )
        } answers {
            slotProduct.captured.invoke(List(20) { makeProduct(it) })
        }
        every { cartRepository.getAllCartItems(onSuccess = capture(slotCartProduct)) } answers {
            slotCartProduct.captured.invoke(List(10) { makeCartProduct(it) })
        }
        // when
        presenter.refreshProductItems()
        // then
        val matchedProduct = List(10) { makeCartProduct(it).toPresentation() }
        val notMatchedProduct = List(10) { makeCartProductCountZero(it + 10).toPresentation() }
        val expected = matchedProduct + notMatchedProduct
        verify { view.loadProductItems(expected) }
        verify { view.setLoadingViewVisible(false) }
    }

    @Test
    fun 최근_상품_목록을_업데이트한다() {
        // given
        val slot = slot<(List<Product>) -> Unit>()
        every { recentProductRepository.getRecentProducts(10, onSuccess = capture(slot)) } answers {
            slot.captured.invoke(List(10) { makeProduct(it) })
        }
        // when
        presenter.loadRecentProductItems()
        // then
        val productModels = List(10) { makeProduct(it).toPresentation() }
        verify { view.loadRecentProductItems(productModels) }
    }

    @Test
    fun 장바구니에_있는_전체_상품_개수를_표시한다() {
        // given
        val slot = slot<(List<CartProductInfo>) -> Unit>()
        every { cartRepository.getAllCartItems(onSuccess = capture(slot)) } answers {
            slot.captured.invoke(List(10) { makeCartProduct(it) })
        }
        // when
        presenter.updateCartCount()
        // then
        verify { view.showCartCount(10) }
    }

    @Test
    fun 업데이트할_카트_상품의_개수가_0이라면_상품을_삭제한후에_장바구니_상품_개수를_최신화하고_상품들을_다시_로드한다() {
        // given
        val slotDeleteProduct = slot<() -> Unit>()
        val slotCartProduct = slot<(List<CartProductInfo>) -> Unit>()
        val slotProduct = slot<(List<Product>) -> Unit>()
        every { cartRepository.deleteCartItem(0, onSuccess = capture(slotDeleteProduct)) } answers {
            slotDeleteProduct.captured.invoke()
        }
        every { cartRepository.getAllCartItems(onSuccess = capture(slotCartProduct)) } answers {
            slotCartProduct.captured.invoke(List(10) { makeCartProduct(it) })
        }
        every { productRepository.getProductsWithRange(0, 20, capture(slotProduct)) } answers {
            slotProduct.captured.invoke(List(10) { makeProduct(it) })
        }
        val deleteItem = makeCartProduct(0).toPresentation()
        // when
        presenter.updateCartItemQuantity(deleteItem, 0)
        // then
        verify { cartRepository.deleteCartItem(0, slotDeleteProduct.captured) }
        verify { view.loadProductItems(any()) }
        verify { view.showCartCount(any()) }
    }

    @Test
    fun 업데이트할_카트_상품의_개수가_0이_아니라면_상품개수를_업데이트한후에_장바구니_상품_개수를_최신화하고_상품들을_다시_로드한다() {
        // given
        val slotCartProduct = slot<(List<CartProductInfo>) -> Unit>()
        val slotUpdateQuantity = slot<() -> Unit>()
        val slotProduct = slot<(List<Product>) -> Unit>()
        every { cartRepository.getAllCartItems(onSuccess = capture(slotCartProduct)) } answers {
            slotCartProduct.captured.invoke(List(10) { makeCartProduct(it) })
        }
        every { cartRepository.updateCartItemQuantity(0, 3, capture(slotUpdateQuantity)) } answers {
            slotUpdateQuantity.captured.invoke()
        }
        every { productRepository.getProductsWithRange(0, 20, capture(slotProduct)) } answers {
            slotProduct.captured.invoke(List(10) { makeProduct(it) })
        }
        val deleteItem = makeCartProduct(0).toPresentation()
        // when
        presenter.updateCartItemQuantity(deleteItem, 3)
        // then
        verify { cartRepository.updateCartItemQuantity(0, 3, slotUpdateQuantity.captured) }
        verify { view.loadProductItems(any()) }
        verify { view.showCartCount(any()) }
    }
}
