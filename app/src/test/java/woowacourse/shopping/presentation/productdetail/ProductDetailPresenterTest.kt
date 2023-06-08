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
        justRun { view.showVisibleOfLastRecentProductInfoView(capture(slot)) }

        // when
        presenter.loadLastRecentProductInfo(recentProduct)

        // then
        verify { view.showVisibleOfLastRecentProductInfoView(slot.captured) }
    }

    @Test
    fun `마지막으로 본 상품이 존재하지 않다면 마지막으로 본 상품 정보를 안 보여준다`() {
        // given
        val recentProduct = null
        justRun { view.hideLastRecentProductInfoView() }

        // when
        presenter.loadLastRecentProductInfo(recentProduct)

        // then
        verify { view.hideLastRecentProductInfoView() }
    }

    @Test
    fun `id를 통해 데이터를 받아와 상품 정보를 보여준다`() {
        // given
        val slot = slot<ProductModel>()
        justRun { view.showProductInfoView(capture(slot)) }
        every {
            productRepository.loadDataById(
                1L,
                onFailure = any(),
                onSuccess = captureLambda()
            )
        } answers {
            lambda<(Product) -> Unit>().captured.invoke(ProductFixture.getData().toModel())
        }

        // when
        presenter.setProduct(1L)

        // then
        verify { view.showProductInfoView(ProductFixture.getData()) }
    }

    @Test
    fun `장바구니 담기를 누르면 상품의 개수를 정하는 다이얼로그가 보여진다`() {
        // given
        val product = ProductFixture.getData()
        every {
            productRepository.loadDataById(
                1L,
                onFailure = any(),
                onSuccess = captureLambda()
            )
        } answers {
            lambda<(Product) -> Unit>().captured.invoke(ProductFixture.getData().toModel())
        }
        presenter.setProduct(1L)

        justRun { view.showCountView(product) }

        // when
        presenter.showCount()

        // then
        verify { view.showProductInfoView(product) }
        verify { view.showCountView(product) }
    }

    @Test
    fun `마지막으로 본 상품을 보여준다`() {
        // given
        val product = ProductFixture.getData()
        val lastRecentProduct = RecentProductModel(1L, product)
        val slot = slot<RecentProductModel>()
        justRun { view.showVisibleOfLastRecentProductInfoView(capture(slot)) }

        // when
        presenter.loadLastRecentProductInfo(lastRecentProduct)

        // then
        val actual = slot.captured
        assertEquals(lastRecentProduct, actual)
        verify { view.showVisibleOfLastRecentProductInfoView(actual) }
    }

    @Test
    fun `마지막으로 본 상품이 존재하지 않으면 보여지지 않는다`() {
        // given
        val product = ProductModel(-1L, "", 0, "")
        val lastRecentProduct = RecentProductModel(-1L, product)
        justRun { view.hideLastRecentProductInfoView() }

        // when
        presenter.loadLastRecentProductInfo(lastRecentProduct)

        // then
        verify { view.hideLastRecentProductInfoView() }
    }

    @Test
    fun `아이디가 1L인 상품을 장바구니에 1개 저장한다`() {
        // given
        every {
            cartRepository.addCartProduct(
                1L,
                onFailure = any(),
                onSuccess = captureLambda()
            )
        } answers {
            lambda<(Long) -> Unit>().captured.invoke(1L)
        }

        // 장바구니는 현재 비어있다
        every {
            cartRepository.loadAllCarts(
                onFailure = any(),
                onSuccess = captureLambda()
            )
        } answers {
            lambda<(List<CartProduct>) -> Unit>().captured.invoke(emptyList())
        }

        justRun { view.addCartSuccessView() }
        justRun { view.exitProductDetailView() }

        // when
        presenter.addCart(1L, 1)

        // then
        verify { view.addCartSuccessView() }
        verify { view.exitProductDetailView() }
    }
}
