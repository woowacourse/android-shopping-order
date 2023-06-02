package woowacourse.shopping.ui.productdetail

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.model.ProductModel
import woowacourse.shopping.model.RecentProductModel
import woowacourse.shopping.model.mapper.toDomain
import woowacourse.shopping.model.mapper.toUi

internal class ProductDetailPresenterTest {
    private lateinit var recentProductRepository: RecentProductRepository
    private lateinit var presenter: ProductDetailContract.Presenter
    private lateinit var view: ProductDetailContract.View
    private lateinit var detailProduct: ProductModel
    private lateinit var recentProduct: RecentProductModel

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        recentProductRepository = mockk(relaxed = true)
        detailProduct = mockk(relaxed = true)
        recentProduct = mockk(relaxed = true)
    }

    @Test
    internal fun 마지막으로_보여준_상품을_보여주지_않아도_되면_프레젠터가_초기화될_때_상품_정보만_보여준다() {
        // given
        val detailProduct = mockk<ProductModel>(relaxed = true)
        val savedRecentProduct = RecentProduct(product = detailProduct.toDomain())

        presenter = ProductDetailPresenter(
            view = view,
            product = detailProduct,
            showLastViewedProduct = false,
            recentProductRepository = recentProductRepository
        )

        justRun { recentProductRepository.saveRecentProduct(savedRecentProduct) }
        justRun { recentProductRepository.getRecentProducts(1) }

        // when
        /* init is called */

        // then
        verify { recentProductRepository.getRecentProducts(1) }
        verify(exactly = 1) { recentProductRepository.saveRecentProduct(savedRecentProduct) }
        verify(exactly = 1) { view.showProductDetail(detailProduct) }
    }

    @Test
    internal fun 마지막으로_보여준_상품을_보여줘야_되면_프레젠터가_초기화될_때_상품_정보와_마지막_조회한_상품을_모두_보여준다() {
        // given
        val detailProduct = mockk<ProductModel>(relaxed = true)
        val savedRecentProduct = RecentProduct(product = detailProduct.toDomain())

        presenter = ProductDetailPresenter(
            view = view,
            product = detailProduct,
            showLastViewedProduct = true,
            recentProductRepository = recentProductRepository
        )

        justRun { recentProductRepository.getRecentProducts(1) }
        justRun { recentProductRepository.saveRecentProduct(savedRecentProduct) }

        // when
        /* init is called */

        // then
        verify { recentProductRepository.getRecentProducts(1) }
        verify(exactly = 1) { recentProductRepository.saveRecentProduct(savedRecentProduct) }
        verify(exactly = 1) { view.showProductDetail(detailProduct) }
        verify(exactly = 1) { view.showLastViewedProductDetail(any()) }
    }

    @Test
    internal fun 장바구니에_상품을_추가한다() {
        // given
        presenter = ProductDetailPresenter(
            view = view,
            product = detailProduct,
            showLastViewedProduct = false,
            recentProductRepository = recentProductRepository
        )

        // when
        presenter.inquiryProductCounter()

        // then
        verify(exactly = 1) { view.showProductCounter(detailProduct) }
    }

    @Test
    internal fun 마지막으로_조회한_상품을_보여준다() {
        // given
        val lastViewedProduct = mockk<RecentProduct>(relaxed = true)
        presenter = ProductDetailPresenter(
            view = view,
            product = detailProduct,
            showLastViewedProduct = false,
            recentProductRepository = recentProductRepository
        )
        every { recentProductRepository.getRecentProducts(1).getLatest() } returns lastViewedProduct

        // when
        presenter.inquiryLastViewedProduct()

        // then
        view.navigateToProductDetail(lastViewedProduct.toUi())
    }

    @Test
    internal fun 홈_화면으로_이동한다() {
        // given
        val productAddedCount = 1
        presenter = ProductDetailPresenter(
            view = view,
            product = detailProduct,
            showLastViewedProduct = false,
            recentProductRepository = recentProductRepository
        )

        // when
        presenter.navigateToHome(productAddedCount)

        // then
        verify(exactly = 1) { view.navigateToHome(detailProduct, productAddedCount) }
    }
}
