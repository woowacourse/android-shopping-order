package woowacourse.shopping.presentation.productdetail

import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.presentation.ProductFixture
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.presentation.model.RecentProductModel
import woowacourse.shopping.presentation.view.productdetail.ProductDetailContract
import woowacourse.shopping.presentation.view.productdetail.ProductDetailPresenter
import woowacouse.shopping.data.repository.cart.CartRepository
import woowacouse.shopping.data.repository.product.ProductRepository

class ProductDetailPresenterTest {
    private lateinit var presenter: ProductDetailContract.Presenter
    private lateinit var view: ProductDetailContract.View
    private lateinit var productRepository: ProductRepository
    private lateinit var cartRepository: CartRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)

        cartRepository = mockk(relaxed = true)
        productRepository = mockk(relaxed = true)
        justRun { productRepository.loadDataById(0L, onFailure = {}, onSuccess = {}) }

        presenter = ProductDetailPresenter(view, 0L, productRepository, cartRepository)
    }

    @Test
    fun `id를 통해 데이터를 받아와 상품 정보를 보여준다`() {
        // given
        val slot = slot<ProductModel>()
        justRun { view.setProductInfoView(capture(slot)) }

        // when
        presenter.loadProductInfo()

        // then
        val actual = slot.captured
        val expected = ProductFixture.getData()

        assertEquals(expected, actual)
        verify { productRepository.loadDataById(actual.id, onFailure = {}, onSuccess = {}) }
        verify { view.setProductInfoView(actual) }
    }

    @Test
    fun `장바구니 담기를 누르면 상품의 개수를 정하는 다이얼로그가 보여진다`() {
        // given
        val product = ProductFixture.getData()
        justRun { view.showCountView(product) }

        // when
        presenter.showCount()

        // then
        verify { view.showCountView(product) }
    }

    @Test
    fun `마지막으로 본 상품을 보여준다`() {
        // given
        val product = ProductFixture.getData()
        val lastRecentProduct = RecentProductModel(1L, product)
        val slot = slot<RecentProductModel>()
        justRun { view.setVisibleOfLastRecentProductInfoView(capture(slot)) }

        // when
        presenter.loadLastRecentProductInfo(lastRecentProduct)

        // then
        val actual = slot.captured
        assertEquals(lastRecentProduct, actual)
        verify { view.setVisibleOfLastRecentProductInfoView(actual) }
    }

    @Test
    fun `마지막으로 본 상품이 존재하지 않으면 보여지지 않는다`() {
        // given
        val product = ProductModel(-1L, "", 0, "")
        val lastRecentProduct = RecentProductModel(-1L, product)
        justRun { view.setGoneOfLastRecentProductInfoView() }

        // when
        presenter.loadLastRecentProductInfo(lastRecentProduct)

        // then
        verify { view.setGoneOfLastRecentProductInfoView() }
    }

    @Test
    fun `상품을 장바구니에 1개 저장한다`() {
        // given
        val slotId = slot<Long>()
        val slotCount = slot<Int>()
        justRun { cartRepository.loadAllCarts(onFailure = {}, onSuccess = {}) }
        justRun { cartRepository.addCartProduct(capture(slotId), onFailure = {}, onSuccess = {}) }
        justRun { view.addCartSuccessView() }
        justRun { view.exitProductDetailView() }

        // when
        presenter.addCart(1)

        // then
        val actualId = slotId.captured
        val actualCount = slotCount.captured
        val expectedId = 0L
        val expectedCount = 1
        assertEquals(expectedId, actualId)
        assertEquals(expectedCount, actualCount)
        justRun { cartRepository.loadAllCarts(onFailure = {}, onSuccess = {}) }
        justRun { cartRepository.addCartProduct(actualId, onFailure = {}, onSuccess = {}) }
        verify { view.addCartSuccessView() }
        verify { view.exitProductDetailView() }
    }
}
