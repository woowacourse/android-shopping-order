package woowacourse.shopping.viewModel.productDetail

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.fixture.PRODUCT1
import woowacourse.shopping.fixture.PRODUCT2
import woowacourse.shopping.fixture.SHOPPING_CART_PRODUCT1
import woowacourse.shopping.view.productDetail.ProductDetailViewModel
import woowacourse.shopping.viewModel.common.CoroutinesTestExtension
import woowacourse.shopping.viewModel.common.InstantTaskExecutorExtension

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
@Suppress("FunctionName")
class ProductDetailViewModelTest {
    private lateinit var viewModel: ProductDetailViewModel
    private lateinit var shoppingCartRepository: ShoppingCartRepository
    private lateinit var productRepository: ProductsRepository

    @BeforeEach
    fun setUp() {
        shoppingCartRepository = mockk()
        productRepository = mockk()

        coEvery { productRepository.getProduct(SHOPPING_CART_PRODUCT1.product.id) } returns
            Result.success(PRODUCT1)

        viewModel =
            ProductDetailViewModel(
                SHOPPING_CART_PRODUCT1.product.id,
                SHOPPING_CART_PRODUCT1.id,
                SHOPPING_CART_PRODUCT1.quantity,
                shoppingCartRepository,
                productRepository,
            )
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `처음 로드됬을 때 현재 현재 상품의 최신 정보를 가져온다`() {
        // given
        coEvery { productRepository.getProduct(SHOPPING_CART_PRODUCT1.product.id) } returns
            Result.success(PRODUCT1)

        // when
        viewModel =
            ProductDetailViewModel(
                SHOPPING_CART_PRODUCT1.product.id,
                SHOPPING_CART_PRODUCT1.id,
                SHOPPING_CART_PRODUCT1.quantity,
                shoppingCartRepository,
                productRepository,
            )

        // then
        assert(viewModel.product.value?.product == PRODUCT1)
    }

    @Test
    fun `처음 로드됬을 때 최근 본 상품을 업데이트할 수 있다`() {
        // given
        coEvery { productRepository.getLatestRecentWatchingProduct() } returns
            Result.success(PRODUCT2)
        coEvery { productRepository.updateRecentWatchingProduct(PRODUCT1) } returns
            Result.success(Unit)
        coEvery { productRepository.getProduct(SHOPPING_CART_PRODUCT1.product.id) } returns
            Result.success(PRODUCT1)
        coEvery { shoppingCartRepository.add(PRODUCT1, 1) } returns
            Result.success(SHOPPING_CART_PRODUCT1)

        // when
        viewModel =
            ProductDetailViewModel(
                SHOPPING_CART_PRODUCT1.product.id,
                SHOPPING_CART_PRODUCT1.id,
                SHOPPING_CART_PRODUCT1.quantity,
                shoppingCartRepository,
                productRepository,
            )

        // then
        coVerify { productRepository.updateRecentWatchingProduct(PRODUCT1) }
    }

    @Test
    fun `최근 본 상품이 있으면 최근 본 상품을 표시한다`() {
        // given
        coEvery { productRepository.getLatestRecentWatchingProduct() } returns
            Result.success(PRODUCT2)
        coEvery { productRepository.updateRecentWatchingProduct(PRODUCT1) } returns
            Result.success(Unit)
        coEvery { productRepository.getProduct(SHOPPING_CART_PRODUCT1.product.id) } returns
            Result.success(PRODUCT1)
        coEvery { shoppingCartRepository.add(PRODUCT1, 1) } returns
            Result.success(SHOPPING_CART_PRODUCT1)

        // when
        viewModel =
            ProductDetailViewModel(
                SHOPPING_CART_PRODUCT1.product.id,
                SHOPPING_CART_PRODUCT1.id,
                SHOPPING_CART_PRODUCT1.quantity,
                shoppingCartRepository,
                productRepository,
            )

        // then
        assertThat(viewModel.recentWatchingProduct.value).isEqualTo(PRODUCT2)
    }

    @Test
    fun `현재 상품이 최근 본 상품에 속해있으면 최근 본 상품을 표시하지 않는다`() {
        // given
        coEvery { productRepository.getLatestRecentWatchingProduct() } returns
            Result.success(PRODUCT1)
        coEvery { productRepository.updateRecentWatchingProduct(PRODUCT1) } returns
            Result.success(Unit)
        coEvery { productRepository.getProduct(SHOPPING_CART_PRODUCT1.product.id) } returns
            Result.success(PRODUCT1)

        // when
        viewModel =
            ProductDetailViewModel(
                SHOPPING_CART_PRODUCT1.product.id,
                SHOPPING_CART_PRODUCT1.id,
                SHOPPING_CART_PRODUCT1.quantity,
                shoppingCartRepository,
                productRepository,
            )

        // then
        assertThat(viewModel.recentWatchingProduct.value).isNull()
    }

    @Test
    fun `현재 상품을 장바구니에 담을 수 있다`() {
        // given
        coEvery { shoppingCartRepository.updateQuantity(any(), any()) } returns
            Result.success(SHOPPING_CART_PRODUCT1)

        // when
        viewModel.addToShoppingCart()

        // then
        coVerify { shoppingCartRepository.updateQuantity(1, 3) }
    }

    @Test
    fun `현재 상품의 수량을 늘릴 수 있다`() {
        // given
        coEvery { shoppingCartRepository.updateQuantity(any(), any()) } returns
            Result.success(SHOPPING_CART_PRODUCT1)
        coEvery { productRepository.getProduct(SHOPPING_CART_PRODUCT1.product.id) } returns
            Result.success(PRODUCT1)
        coEvery { productRepository.updateRecentWatchingProduct(PRODUCT1) } returns
            Result.success(Unit)
        coEvery { productRepository.getLatestRecentWatchingProduct() } returns
            Result.success(PRODUCT2)

        // when
        viewModel.plusQuantity()

        // then
        assertThat(viewModel.quantity.value).isEqualTo(2)
        assertThat(viewModel.price.value).isEqualTo(200000)
    }

    @Test
    fun `현재 상품의 수량을 줄일 수 있다`() {
        // given
        coEvery { shoppingCartRepository.updateQuantity(any(), any()) } returns
            Result.success(SHOPPING_CART_PRODUCT1)
        coEvery { productRepository.getProduct(SHOPPING_CART_PRODUCT1.product.id) } returns
            Result.success(PRODUCT1)
        coEvery { productRepository.updateRecentWatchingProduct(PRODUCT1) } returns
            Result.success(Unit)
        // when
        viewModel.minusQuantity()

        // then
        assertThat(viewModel.quantity.value).isEqualTo(1)
        assertThat(viewModel.price.value).isEqualTo(100000)
    }
}
