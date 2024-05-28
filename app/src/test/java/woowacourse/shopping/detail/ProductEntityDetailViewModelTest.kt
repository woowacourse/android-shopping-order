package woowacourse.shopping.detail

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.FakeCartRepository
import woowacourse.shopping.FakeRecentProductRepository
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.data.product.remote.mock.MockWebProductServer
import woowacourse.shopping.data.product.remote.mock.MockWebProductServerDispatcher
import woowacourse.shopping.data.product.remote.mock.MockWebServerProductRepository
import woowacourse.shopping.data.recent.local.entity.RecentProductEntity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.products
import woowacourse.shopping.ui.detail.ProductDetailViewModel
import java.time.LocalDateTime

@ExtendWith(InstantTaskExecutorExtension::class)
class ProductEntityDetailViewModelTest {
    private lateinit var viewModel: ProductDetailViewModel

    private lateinit var productRepository: ProductRepository
    private lateinit var recentProductRepository: RecentProductRepository
    private val cartRepository: CartRepository = FakeCartRepository()

    private lateinit var productServer: MockWebProductServer

    @BeforeEach
    fun setUp() {
        productServer = MockWebProductServer(MockWebProductServerDispatcher(products(2)))
        productServer.start()
        productRepository = MockWebServerProductRepository(productServer)
        recentProductRepository = FakeRecentProductRepository()
    }

    @AfterEach
    fun tearDown() {
        productServer.shutDown()
    }

    @Test
    fun `상품 id에 맞는 상품을 불러온다`() {
        viewModel =
            ProductDetailViewModel(
                0,
                productRepository,
                recentProductRepository,
                cartRepository,
                false,
            )

        val actual = viewModel.productUiModel.getOrAwaitValue()
        assertThat(actual.productId).isEqualTo(0)
    }

    @Test
    fun `상품 id에 해당하는 상품이 없는 경우 에러가 발생한다`() {
        viewModel =
            ProductDetailViewModel(
                -1,
                productRepository,
                recentProductRepository,
                cartRepository,
                false,
            )
        assertThat(viewModel.productLoadError.getOrAwaitValue().peekContent()).isNotNull
    }

    @Test
    fun `상품을 장바구니에 담는다`() {
        // given
        viewModel =
            ProductDetailViewModel(
                0,
                productRepository,
                recentProductRepository,
                cartRepository,
                false,
            )

        // when
        viewModel.addCartProduct()

        // then
        val actual = cartRepository.find(0)
        assertThat(actual.productId).isEqualTo(0)
        assertThat(viewModel.isSuccessAddCart.getOrAwaitValue().peekContent()).isTrue
    }

    @Test
    fun `마지막으로 본 상품을 불러온다`() {
        setUpRecentProductRepository(productId = 1)
        viewModel =
            ProductDetailViewModel(
                0,
                productRepository,
                recentProductRepository,
                cartRepository,
                false,
            )

        val actual = viewModel.lastRecentProduct.getOrAwaitValue()
        assertThat(actual.productId).isEqualTo(1)
    }

    @Test
    fun `마지막으로 본 상품이 없는 경우 불러올 수 없다`() {
        viewModel =
            ProductDetailViewModel(
                0,
                productRepository,
                recentProductRepository,
                cartRepository,
                false,
            )

        val actual = viewModel.lastRecentProduct.isInitialized
        assertThat(actual).isFalse
    }

    @Test
    fun `마지막으로 본 상품이 현재 상품과 동일힌 경우 마지막으로 본 상품이 보이지 않는다`() {
        setUpRecentProductRepository(productId = 0)
        viewModel =
            ProductDetailViewModel(
                0,
                productRepository,
                recentProductRepository,
                cartRepository,
                false,
            )

        val actual = viewModel.isVisibleLastRecentProduct.getOrAwaitValue()
        assertThat(actual).isFalse
    }

    @Test
    fun `마지막으로 본 상품을 타고 들어온 경우 마지막으로 본 상품이 보이지 않는다`() {
        setUpRecentProductRepository(productId = 1)
        viewModel =
            ProductDetailViewModel(
                0,
                productRepository,
                recentProductRepository,
                cartRepository,
                true,
            )

        val actual = viewModel.isVisibleLastRecentProduct.getOrAwaitValue()
        assertThat(actual).isFalse
    }

    private fun setUpRecentProductRepository(productId: Int) {
        recentProductRepository =
            FakeRecentProductRepository(listOf(RecentProductEntity(0, productId, LocalDateTime.now())))
    }
}
