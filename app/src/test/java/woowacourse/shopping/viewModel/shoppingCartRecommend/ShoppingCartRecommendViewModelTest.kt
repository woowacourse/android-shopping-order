package woowacourse.shopping.viewModel.shoppingCartRecommend

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.fixture.RECENT_PRODUCTS
import woowacourse.shopping.fixture.RECENT_PRODUCTS_FULL
import woowacourse.shopping.fixture.RECOMMENDED_PRODUCTS
import woowacourse.shopping.fixture.SHOPPING_CARTS1
import woowacourse.shopping.view.shoppingCartRecommend.ShoppingCartRecommendViewModel
import woowacourse.shopping.viewModel.common.CoroutinesTestExtension
import woowacourse.shopping.viewModel.common.InstantTaskExecutorExtension
import woowacourse.shopping.viewModel.common.getOrAwaitValue

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
@Suppress("FunctionName")
class ShoppingCartRecommendViewModelTest {
    private val shoppingCartRepository: ShoppingCartRepository = mockk()
    private val productRepository: ProductsRepository = mockk()
    private lateinit var viewModel: ShoppingCartRecommendViewModel

    @BeforeEach
    fun setUp() {
        coEvery { productRepository.getRecentRecommendWatchingProducts(any()) } returns
            Result.success(
                RECENT_PRODUCTS,
            )

        coEvery { shoppingCartRepository.load(any(), any()) } returns
            Result.success(
                SHOPPING_CARTS1,
            )

        viewModel =
            ShoppingCartRecommendViewModel(
                SHOPPING_CARTS1.shoppingCartItems,
                shoppingCartRepository,
                productRepository,
            )
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `처음 생성됬을 때 추천 상품을 로드한다`() {
        // given
        coEvery { shoppingCartRepository.load(any(), any()) } returns
            Result.success(
                SHOPPING_CARTS1,
            )

        // when
        viewModel =
            ShoppingCartRecommendViewModel(
                SHOPPING_CARTS1.shoppingCartItems,
                shoppingCartRepository,
                productRepository,
            )
        // then
        assertThat(viewModel.recommendProducts.getOrAwaitValue()).isEqualTo(RECOMMENDED_PRODUCTS)
    }

    @Test
    fun `추천 상품의 개수는 최대 10개이다`() {
        // given
        coEvery { productRepository.getRecentRecommendWatchingProducts(any()) } returns
            Result.success(
                RECENT_PRODUCTS_FULL,
            )
        // when
        viewModel =
            ShoppingCartRecommendViewModel(
                SHOPPING_CARTS1.shoppingCartItems,
                shoppingCartRepository,
                productRepository,
            )
        // then
        assertThat(viewModel.recommendProducts.getOrAwaitValue()).hasSize(10)
    }

    @Test
    fun `추천 상품을 장바구니에 추가할 수 있다`() {
        // given
        val product = RECOMMENDED_PRODUCTS.first()
        coEvery { shoppingCartRepository.add(any(), any()) } returns
            Result.success(
                ShoppingCartProduct(
                    id = 1,
                    product = product.product,
                    quantity = 1,
                ),
            )

        // when
        viewModel.addProductToShoppingCart(product, 1)

        // then
        assertThat(viewModel.shoppingCartProductsToOrder.getOrAwaitValue()).contains(
            ShoppingCartProduct(
                id = 1,
                product = product.product,
                quantity = 1,
            ),
        )
    }
}
