package woowacourse.shopping.ui.products

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.fixture.CoroutinesTestExtension
import woowacourse.shopping.fixture.InstantTaskExecutorExtension
import woowacourse.shopping.fixture.fake.FakeCartRepository
import woowacourse.shopping.fixture.fake.FakeProductRepository
import woowacourse.shopping.fixture.fake.FakeRecentRepository
import woowacourse.shopping.fixture.getOrAwaitValue
import woowacourse.shopping.ui.products.uimodel.ProductWithQuantityUiModel
import woowacourse.shopping.ui.products.viewmodel.ProductContentsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class, CoroutinesTestExtension::class)
class ProductContentsViewModelTest {
    private lateinit var viewModel: ProductContentsViewModel

    private lateinit var productRepository: FakeProductRepository
    private lateinit var recentProductRepository: FakeRecentRepository
    private lateinit var cartRepository: FakeCartRepository

    @BeforeEach
    fun setUp() {
        recentProductRepository = FakeRecentRepository()
        cartRepository = FakeCartRepository()
        productRepository = FakeProductRepository(cartRepository)

        viewModel =
            ProductContentsViewModel(productRepository, recentProductRepository, cartRepository)
        viewModel.loadCartItems()
    }

    @Test
    fun `viewModel을 생성하면, 초기에 20개의 상품이 load 된다`() =
        runTest {
            val actual = viewModel.productWithQuantity.getOrAwaitValue().productWithQuantities

            assertThat(actual).hasSize(LOADED_PRODUCT_SIZE)
        }

    @Test
    fun `상품을 가져올 때, 한번에 20개씩 가져온다`() =
        runTest {
            // when
            val before = viewModel.productWithQuantity.getOrAwaitValue().productWithQuantities
            assertThat(before).hasSize(LOADED_PRODUCT_SIZE)

            // given
            viewModel.loadProducts()

            // then
            advanceUntilIdle()
            val actual = viewModel.productWithQuantity.getOrAwaitValue().productWithQuantities
            assertThat(actual).hasSize(before.size + LOADED_PRODUCT_SIZE)
        }

    @Test
    fun `상품을 불러올 때, 상품의 불러오기 전에는 로딩 상태가 된다`() =
        runTest {
            // when
            val beforeState = viewModel.productWithQuantity.getOrAwaitValue()
            assertThat(beforeState.isLoading).isFalse

            // given
            viewModel.loadProducts()

            // then
            runCurrent()
            val beforeFinish = viewModel.productWithQuantity.value ?: throw Exception()
            assertThat(beforeFinish.isLoading).isTrue

            advanceUntilIdle()
            val after = viewModel.productWithQuantity.getOrAwaitValue()
            assertThat(after.isLoading).isFalse
        }

    @Test
    fun `장바구니에 해당 상품이 없을 때, 상품을 새로 추가하면 해당 상품의 quantity가 1이 된다`() =
        runTest {
            // when
            val isProductInCart = cartRepository.cartStubs.any { it.product.id == ADD_PRODUCT_ID }
            assertThat(isProductInCart).isFalse

            // given
            viewModel.addCart(ADD_PRODUCT_ID)

            // then
            advanceUntilIdle()
            val actual = viewModel.productWithQuantity.getOrAwaitValue()
            val actualProduct = (actual.productWithQuantities as List<ProductWithQuantityUiModel>)
            assertThat(actualProduct.first { it.product.id == ADD_PRODUCT_ID }.quantity).isEqualTo(1)
        }

    @Test
    fun `장바구니에 상품의 개수가 2개일 때, 개수를 추가하면 3개가 된다`() =
        runTest {
            // when
            val before = getCurrentProducts()
            val beforeQuantity = before.first { it.product.id == UPDATE_COUNT_PRODUCT_ID }.quantity
            assertThat(beforeQuantity).isEqualTo(2)

            // given
            viewModel.plusCount(UPDATE_COUNT_PRODUCT_ID)

            // then
            advanceUntilIdle()
            val actual = getCurrentProducts()
            val actualQuantity = actual.first { it.product.id == UPDATE_COUNT_PRODUCT_ID }.quantity
            assertThat(actualQuantity).isEqualTo(3)
        }

    private fun getCurrentProducts() =
        (viewModel.productWithQuantity.getOrAwaitValue().productWithQuantities as List<ProductWithQuantityUiModel>)

    companion object {
        const val LOADED_PRODUCT_SIZE = 20

        const val ADD_PRODUCT_ID = 8L

        const val UPDATE_COUNT_PRODUCT_ID = 2L
    }
}
