package woowacourse.shopping.ui.detail

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.fake.FakeCartRepository
import woowacourse.shopping.fake.FakeProductRepository
import woowacourse.shopping.fake.FakeRecentProductRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.product
import woowacourse.shopping.recentProduct

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class ProductDetailViewModelTest {
    private lateinit var viewModel: ProductDetailViewModel

    private lateinit var productRepository: ProductRepository
    private lateinit var recentProductRepository: RecentProductRepository
    private lateinit var cartRepository: CartRepository

    @BeforeEach
    fun setUp() {
        productRepository = FakeProductRepository()
        recentProductRepository = FakeRecentProductRepository()
        cartRepository = FakeCartRepository()
    }

    private fun setUpViewModel(
        productId: Int,
        recentProductId: Int? = null,
        lastSeenProductVisible: Boolean = false,
    ): Unit =
        runBlocking {
            val products = savedProducts(productId, recentProductId)
            val recentProducts = savedRecentProducts(recentProductId)
            productRepository = FakeProductRepository(products)
            recentProductRepository = FakeRecentProductRepository(recentProducts)
            viewModel =
                ProductDetailViewModel(
                    productId,
                    productRepository,
                    recentProductRepository,
                    cartRepository,
                    lastSeenProductVisible,
                )
        }

    @Test
    fun `상품 id에 맞는 상품을 불러온다`(): Unit =
        runBlocking {
            // given
            setUpViewModel(productId = 0)

            // when
            viewModel.loadProduct()

            // then
            val actual = viewModel.productUiModel.getOrAwaitValue()
            assertThat(actual.productId).isEqualTo(0)
        }

    @Test
    fun `상품 id에 해당하는 상품이 없는 경우 에러가 발생한다`(): Unit =
        runBlocking {
            // given
            viewModel =
                ProductDetailViewModel(
                    -1,
                    productRepository,
                    recentProductRepository,
                    cartRepository,
                    false,
                )

            // when
            viewModel.loadProduct()

            // then
            assertThat(viewModel.productLoadError.getValue()).isNotNull
        }

    @Test
    fun `상품을 장바구니에 담는다`(): Unit =
        runBlocking {
            // given
            setUpViewModel(productId = 0)
            viewModel.loadProduct()

            // when
            viewModel.addCartProduct()

            // then
            cartRepository.findByProductId(0)
                .onSuccess { cartItem ->
                    assertThat(cartItem).isNotNull
                    assertThat(cartItem?.product?.id).isEqualTo(0)
                }.onFailure {
                    assertThat(true).isFalse
                }
            assertThat(viewModel.isSuccessAddCart.getValue()).isTrue
        }

    @Test
    fun `마지막으로 본 상품을 불러온다`(): Unit =
        runBlocking {
            // given
            setUpViewModel(productId = 0, recentProductId = 3)

            // when
            viewModel.loadProduct()

            // then
            val actual = viewModel.lastRecentProduct.getOrAwaitValue()
            assertThat(actual.productId).isEqualTo(3)
        }

    @Test
    fun `마지막으로 본 상품이 없는 경우 불러올 수 없다`(): Unit =
        runBlocking {
            // given
            setUpViewModel(productId = 0)

            // when
            viewModel.loadProduct()

            // then
            val actual = viewModel.lastRecentProduct.isInitialized
            assertThat(actual).isFalse
        }

    @Test
    fun `마지막으로 본 상품이 현재 상품과 동일힌 경우 마지막으로 본 상품이 보이지 않는다`(): Unit =
        runBlocking {
            // given
            setUpViewModel(productId = 3, recentProductId = 3)

            // when
            viewModel.loadProduct()

            // then
            val actual = viewModel.isVisibleLastRecentProduct.getOrAwaitValue()
            assertThat(actual).isFalse
        }

    @Test
    fun `마지막으로 본 상품을 타고 들어온 경우 마지막으로 본 상품이 보이지 않는다`(): Unit =
        runBlocking {
            // given
            setUpViewModel(productId = 0, recentProductId = 3, lastSeenProductVisible = true)

            // when
            viewModel.loadProduct()

            // then
            val actual = viewModel.isVisibleLastRecentProduct.getOrAwaitValue()
            assertThat(actual).isFalse
        }

    private fun savedProducts(
        productId: Int,
        recentProductId: Int? = null,
    ): List<Product> {
        return if (recentProductId != null) {
            listOf(product(productId), product(recentProductId))
        } else {
            listOf(product(productId))
        }
    }

    private fun savedRecentProducts(recentProductId: Int? = null): List<RecentProduct> {
        return if (recentProductId != null) listOf(recentProduct(recentProductId)) else emptyList()
    }
}
