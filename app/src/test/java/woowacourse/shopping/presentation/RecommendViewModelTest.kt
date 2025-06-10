package woowacourse.shopping.presentation

import androidx.lifecycle.SavedStateHandle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.usecase.RecommendProductsUseCase
import woowacourse.shopping.fixture.FakeCartRepository
import woowacourse.shopping.fixture.FakeProductRepository
import woowacourse.shopping.fixture.FakeRecentProductRepository
import woowacourse.shopping.fixture.ProductsFixture
import woowacourse.shopping.presentation.Extra.KEY_SELECT_ITEMS
import woowacourse.shopping.presentation.recommend.RecommendViewModel

@ExtendWith(InstantTaskExecutorExtension::class)
class RecommendViewModelTest {
    private lateinit var cartRepository: CartRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var recentProductRepository: RecentProductRepository
    private lateinit var recommendProductsUseCase: RecommendProductsUseCase
    private lateinit var viewModel: RecommendViewModel

    @BeforeEach
    fun setUp() {
        val savedStateHandle =
            SavedStateHandle(mapOf(KEY_SELECT_ITEMS to ProductsFixture.dummyCartUiModels))
        cartRepository = FakeCartRepository()
        productRepository = FakeProductRepository()
        recentProductRepository = FakeRecentProductRepository()
        recommendProductsUseCase = RecommendProductsUseCase(cartRepository, productRepository)

        viewModel =
            RecommendViewModel(
                savedStateHandle,
                cartRepository,
                recentProductRepository,
                recommendProductsUseCase,
            )
    }

    @Test
    fun `장바구니_페이지에서_선택한_가격과_수량을_불러온다`() {
        val count = viewModel.selectedTotalCount.getOrAwaitValue()
        val price = viewModel.selectedTotalPrice.getOrAwaitValue()
        assertThat(count).isEqualTo(15)
        assertThat(price).isEqualTo(15_000)
    }
}
