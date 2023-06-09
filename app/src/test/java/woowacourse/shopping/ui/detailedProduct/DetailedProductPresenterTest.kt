package woowacourse.shopping.ui.detailedProduct

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.RecentProduct
import woowacourse.shopping.utils.SharedPreferenceUtils

class DetailedProductPresenterTest {

    private lateinit var view: DetailedProductContract.View
    private lateinit var presenter: DetailedProductContract.Presenter
    private lateinit var sharedPreferenceUtils: SharedPreferenceUtils
    private lateinit var productRepository: ProductRepository
    private lateinit var cartRepository: CartRepository
    private lateinit var recentRepository: RecentRepository

    private val fakeProduct: Product = Product(
        1,
        "[사미헌] 갈비탕",
        12000,
        "https://search4.kakaocdn.net/argon/656x0_80_wr/KjhZ1Chrw9p"
    )

    private val fakeRecentProduct: RecentProduct = RecentProduct(
        1,
        "[사미헌] 갈비탕",
        12000,
        "https://search4.kakaocdn.net/argon/656x0_80_wr/KjhZ1Chrw9p"
    )

    @Before
    fun setUp() {
        view = mockk()
        sharedPreferenceUtils = mockk()
        productRepository = mockk()
        cartRepository = mockk()
        recentRepository = mockk()

        every { productRepository.findById(any()) } answers { Result.success(fakeProduct) }
        presenter =
            DetailedProductPresenter(
                view,
                sharedPreferenceUtils,
                productRepository,
                cartRepository,
                recentRepository,
                fakeProduct.toUIModel().id
            )
    }

    @Test
    fun `마지막으로 본 상품을 세팅한다`() {
        // given
        every { sharedPreferenceUtils.getLastProductId() } returns -2
        every { sharedPreferenceUtils.setLastProductId(any()) } answers { nothing }

        // when
        presenter.fetchLastProduct()

        // then
        verify(exactly = 1) { sharedPreferenceUtils.getLastProductId() }
        verify(exactly = 1) { sharedPreferenceUtils.setLastProductId(any()) }
    }

    @Test
    fun `상품을 불러와서 세팅한다`() {
        // given
        every { view.setProductDetail(any(), any()) } answers { nothing }

        // when
        presenter.fetchProductDetail()

        // then
        verify(exactly = 1) { view.setProductDetail(any(), any()) }
    }

    @Test
    fun `상품을 장바구니에 추가한다`() {
        // given
        every {
            cartRepository.updateCountWithProductId(any(), any())
        } answers { Result.success(0) }

        every { cartRepository.updateCountWithProductId(any(), any()) }
            .answers { Result.success(fakeProduct.id) }

        every { view.navigateToCart() } answers { nothing }

        // when
        presenter.addProductToCart(fakeProduct.id)

        // then
        verify(exactly = 1) { cartRepository.updateCountWithProductId(fakeProduct.id, any()) }
        verify(exactly = 1) { view.navigateToCart() }
    }

    @Test
    fun `상품을 최근 본 상품에 추가한다`() {
        // given
        every { recentRepository.insert(any()) } answers { nothing }
        every { recentRepository.delete(any()) } answers { nothing }
        every { recentRepository.findById(any()) } answers { fakeRecentProduct }

        // when
        presenter.addProductToRecent()

        // then
        verify(exactly = 1) { recentRepository.insert(fakeProduct) }
        verify(exactly = 1) { recentRepository.findById(fakeProduct.id) }
        verify(exactly = 1) { recentRepository.delete(fakeProduct.id) }
    }

    @Test
    fun `최근 본 상품으로 이동한다`() {
        // given
        every { productRepository.findById(any()) } answers { Result.success(fakeProduct) }
        every { view.navigateToDetailedProduct(any()) } answers { nothing }
        every { sharedPreferenceUtils.getLastProductId() } returns -2
        every { sharedPreferenceUtils.setLastProductId(any()) } answers { nothing }

        // when
        presenter.fetchLastProduct()
        presenter.processToDetailedProduct()

        // then
        verify(exactly = 1) { view.navigateToDetailedProduct(any()) }
        verify(exactly = 2) { sharedPreferenceUtils.setLastProductId(any()) }
    }

    @Test
    fun `장바구니에 상품을 추가하는 다이얼로그로 이동한다`() {
        // given
        every { view.showCartDialog(any()) } answers { nothing }

        // when
        presenter.processToCart()

        // then
        verify(exactly = 1) { view.showCartDialog(any()) }
    }
}
