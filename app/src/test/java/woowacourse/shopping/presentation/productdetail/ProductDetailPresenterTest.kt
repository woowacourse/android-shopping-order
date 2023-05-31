package woowacourse.shopping.presentation.productdetail

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.presentation.ProductFixture
import woowacourse.shopping.presentation.RecentProductFixture
import woowacourse.shopping.presentation.mapper.toModel
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.presentation.model.RecentProductModel
import woowacourse.shopping.presentation.view.productdetail.ProductDetailContract
import woowacourse.shopping.presentation.view.productdetail.ProductDetailPresenter
import woowacouse.shopping.data.repository.cart.CartRepository
import woowacouse.shopping.data.repository.product.ProductRepository
import woowacouse.shopping.model.cart.CartProduct
import woowacouse.shopping.model.product.Product

class ProductDetailPresenterTest {
    private lateinit var presenter: ProductDetailContract.Presenter
    private lateinit var view: ProductDetailContract.View
    private lateinit var productRepository: ProductRepository
    private lateinit var cartRepository: CartRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)

        cartRepository = mockk()
        productRepository = mockk()

        presenter = ProductDetailPresenter(view, productRepository, cartRepository)
    }

    @Test
    fun `마지막으로 본 상품이 존재한다면 상품 정보를 보여준다`() {
        // given
        val recentProduct = RecentProductFixture.getData()
        val slot = slot<RecentProductModel>()
        justRun { view.setVisibleOfLastRecentProductInfoView(capture(slot)) }

        // when
        presenter.loadLastRecentProductInfo(recentProduct)

        // then
        verify { view.setVisibleOfLastRecentProductInfoView(slot.captured) }
    }

    @Test
    fun `마지막으로 본 상품이 존재하지 않다면 마지막으로 본 상품 정보를 안 보여준다`() {
        // given
        val recentProduct = null
        justRun { view.setGoneOfLastRecentProductInfoView() }

        // when
        presenter.loadLastRecentProductInfo(recentProduct)

        // then
        verify { view.setGoneOfLastRecentProductInfoView() }
    }

    @Test
    fun `id를 통해 데이터를 받아와 상품 정보를 보여준다`() {
        // given
        val slot = slot<ProductModel>()
        justRun { view.setProductInfoView(capture(slot)) }
        val slotOnSuccess = slot<(Product) -> Unit>()
        every {
            productRepository.loadDataById(
                1L, onFailure = any(), onSuccess = capture(slotOnSuccess)
            )
        } answers {
            slotOnSuccess.captured(ProductFixture.getData().toModel())
        }

        // when
        presenter.setProduct(1L)

        // then
        verify { view.setProductInfoView(ProductFixture.getData()) }
    }

    @Test
    fun `장바구니 담기를 누르면 상품의 개수를 정하는 다이얼로그가 보여진다`() {
        // given
        val product = ProductFixture.getData()
        val slotOnSuccess = slot<(Product) -> Unit>()
        every {
            productRepository.loadDataById(
                1L, onFailure = any(), onSuccess = capture(slotOnSuccess)
            )
        } answers {
            slotOnSuccess.captured(product.toModel())
        }
        presenter.setProduct(1L)

        justRun { view.showCountView(product) }

        // when
        presenter.showCount()

        // then
        verify { view.setProductInfoView(product) }
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
    fun `아이디가 1L인 상품을 장바구니에 1개 저장한다`() {
        // given
        val slotAddCartOnSuccess = slot<(Long) -> Unit>()
        every {
            cartRepository.addCartProduct(
                1L,
                onFailure = any(),
                onSuccess = capture(slotAddCartOnSuccess)
            )
        } answers {
            slotAddCartOnSuccess.captured(1L)
        }

        // 장바구니는 현재 비어있다
        val slotLoadAllCartsOnSuccess = slot<(List<CartProduct>) -> Unit>()
        every {
            cartRepository.loadAllCarts(
                onFailure = any(),
                onSuccess = capture(slotLoadAllCartsOnSuccess)
            )
        } answers {
            slotLoadAllCartsOnSuccess.captured(emptyList())
        }

        justRun { cartRepository.addLocalCart(1L) }
        justRun { view.addCartSuccessView() }
        justRun { view.exitProductDetailView() }

        // when
        presenter.addCart(1L, 1)

        // then
        verify { cartRepository.addLocalCart(1L) }
        verify { view.addCartSuccessView() }
        verify { view.exitProductDetailView() }
    }
}
