package woowacourse.shopping.presentation.recommend.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.usecase.AddToCartUseCase
import woowacourse.shopping.domain.usecase.GetRecommendProductsUseCase
import woowacourse.shopping.fake.fakeProduct
import woowacourse.shopping.fake.repository.FakeCartRepository
import woowacourse.shopping.fake.repository.FakeProductRepository
import woowacourse.shopping.fake.repository.FakeRecentProductRepository

class RecommendViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    private val toy1 = fakeProduct(id = 1L, category = "장난감")
    private val toy2 = fakeProduct(id = 2L, category = "장난감")
    private val toy3 = fakeProduct(id = 3L, category = "장난감")

    private val food1 = fakeProduct(id = 4L, category = "음식")

    private lateinit var cartRepository: FakeCartRepository
    private lateinit var productRepository: FakeProductRepository
    private lateinit var recentProductRepository: FakeRecentProductRepository
    private lateinit var viewModel: RecommendViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        val allProducts = listOf(toy1, toy2, toy3, food1)
        val productMap = allProducts.associateBy { it.id }

        cartRepository = FakeCartRepository(productMap)
        productRepository = FakeProductRepository(allProducts)
        recentProductRepository = FakeRecentProductRepository(allProducts)

        viewModel =
            RecommendViewModel(
                cartRepository = cartRepository,
                addToCartUseCase = AddToCartUseCase(cartRepository),
                getRecommendProductsUseCase = GetRecommendProductsUseCase(recentProductRepository, cartRepository, productRepository),
            )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `최근 본 상품이 없으면 추천 상품도 비어있다`() =
        runTest {
            viewModel.loadRecommendProducts()

            assertThat(viewModel.uiState.value.recommendProducts).isEmpty()
        }

    @Test
    fun `최근 본 상품과 같은 카테고리의 상품을 추천한다`() =
        runTest {
            recentProductRepository.upsertRecentProduct(toy1.id)

            viewModel.loadRecommendProducts()

            val recommended =
                viewModel.uiState.value.recommendProducts
                    .map { it.product.id }
            assertThat(recommended).contains(toy1.id, toy2.id, toy3.id)
            assertThat(recommended).doesNotContain(food1.id)
        }

    @Test
    fun `카트에 이미 있는 상품은 추천에서 제외된다`() =
        runTest {
            recentProductRepository.upsertRecentProduct(toy1.id)
            cartRepository.addItem(toy2.id, 1)

            viewModel.loadRecommendProducts()

            val recommended =
                viewModel.uiState.value.recommendProducts
                    .map { it.product.id }
            assertThat(recommended).contains(toy1.id, toy3.id)
            assertThat(recommended).doesNotContain(toy2.id)
        }

    @Test
    fun `loadPaymentId는 주어진 productIds에 해당하는 카트 항목만 PaymentItems에 포함시킨다`() =
        runTest {
            cartRepository.addItem(toy1.id, 2)
            cartRepository.addItem(toy2.id, 3)
            cartRepository.addItem(food1.id, 1)

            viewModel.loadPaymentId(longArrayOf(toy1.id, food1.id))

            val paymentItems = viewModel.uiState.value.paymentItems
            assertThat(paymentItems.isContain(toy1.id)).isTrue
            assertThat(paymentItems.isContain(food1.id)).isTrue
            assertThat(paymentItems.isContain(toy2.id)).isFalse
        }

    @Test
    fun `increase는 카트에 새 항목을 추가하고 PaymentItems와 추천 수량을 갱신한다`() =
        runTest {
            recentProductRepository.upsertRecentProduct(toy1.id)
            viewModel.loadRecommendProducts()

            viewModel.increase(toy2.id)

            val state = viewModel.uiState.value
            assertThat(state.paymentItems.quantityOf(toy2.id)).isEqualTo(1)
            val recommendQuantity = state.recommendProducts.find { it.product.id == toy2.id }?.quantity
            assertThat(recommendQuantity).isEqualTo(1)
        }

    @Test
    fun `increase를 같은 상품에 두 번 호출하면 수량이 누적된다`() =
        runTest {
            recentProductRepository.upsertRecentProduct(toy1.id)
            viewModel.loadRecommendProducts()

            viewModel.increase(toy2.id)
            viewModel.increase(toy2.id)

            val state = viewModel.uiState.value
            assertThat(state.paymentItems.quantityOf(toy2.id)).isEqualTo(2)
        }

    @Test
    fun `decrease는 수량이 1보다 크면 1 감소시킨다`() =
        runTest {
            recentProductRepository.upsertRecentProduct(toy1.id)
            viewModel.loadRecommendProducts()
            viewModel.increase(toy2.id)
            viewModel.increase(toy2.id)

            viewModel.decrease(toy2.id)

            val state = viewModel.uiState.value
            assertThat(state.paymentItems.quantityOf(toy2.id)).isEqualTo(1)
        }

    @Test
    fun `decrease는 수량이 1일 때 PaymentItems에서 항목을 제거한다`() =
        runTest {
            recentProductRepository.upsertRecentProduct(toy1.id)
            viewModel.loadRecommendProducts()
            viewModel.increase(toy2.id)

            viewModel.decrease(toy2.id)

            val state = viewModel.uiState.value
            assertThat(state.paymentItems.isContain(toy2.id)).isFalse
        }
}
