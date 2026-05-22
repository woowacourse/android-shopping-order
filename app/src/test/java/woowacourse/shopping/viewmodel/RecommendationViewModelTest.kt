package woowacourse.shopping.viewmodel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.PurchaseProduct
import woowacourse.shopping.ui.recommendation.RecommendationViewModel
import woowacourse.shopping.viewmodel.fakes.FakeCartRepository
import woowacourse.shopping.viewmodel.fakes.FakeProductRepository
import woowacourse.shopping.viewmodel.fakes.FakeRecentlyViewedProductRepository

@OptIn(ExperimentalCoroutinesApi::class)
class RecommendationViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private lateinit var viewModel: RecommendationViewModel
    private lateinit var fakeProductRepository: FakeProductRepository
    private lateinit var fakeRecentlyViewedProductRepository: FakeRecentlyViewedProductRepository
    private lateinit var fakeCartRepository: FakeCartRepository

    private val products = listOf(
        Product(id = 1L, name = "커비", price = 1000, imageUri = "uri1", category = "크루"),
        Product(id = 2L, name = "하로", price = 2000, imageUri = "uri2", category = "크루"),
        Product(id = 3L, name = "투핸더", price = 10000, imageUri = "uri3", category = "크루"),
        Product(id = 4L, name = "커브볼", price = 3000, imageUri = "uri4", category = "생활용품")
    )

    @BeforeEach
    fun init() {
        fakeCartRepository = FakeCartRepository()
        fakeRecentlyViewedProductRepository = FakeRecentlyViewedProductRepository()
        fakeProductRepository = FakeProductRepository()
        fakeProductRepository.setProducts(products)
    }

    @Test
    fun `장바구니에 있는 모든 상품은 추천 목록에서 제외되어야 한다`() = runTest {
        // Given
        val purchaseProduct1 = PurchaseProduct(id = 101L, product = products[0], count = 1)
        val purchaseProduct2 = PurchaseProduct(id = 102L, product = products[1], count = 1)
        fakeCartRepository.insert(purchaseProduct1)
        fakeCartRepository.insert(purchaseProduct2)

        viewModel = RecommendationViewModel(
            cartRepository = fakeCartRepository,
            productRepository = fakeProductRepository,
            recentlyViewedProductRepository = fakeRecentlyViewedProductRepository,
            initialSelectedIds = listOf(101L)
        )

        val collectJob = backgroundScope.launch(context = UnconfinedTestDispatcher(testScheduler)) {
            viewModel.recommendedProducts.collect()
        }

        // When&Then
        val recommended = viewModel.recommendedProducts.value.products
        
        assertEquals(2, recommended.size)
        assertEquals(false, recommended.any { it.id == 1L || it.id == 2L })

        collectJob.cancel()
    }
}
