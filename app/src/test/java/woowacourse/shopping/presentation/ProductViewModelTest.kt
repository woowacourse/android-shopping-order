package woowacourse.shopping.presentation

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.fixture.FakeCartRepository
import woowacourse.shopping.fixture.FakeProductRepository
import woowacourse.shopping.fixture.FakeRecentProductRepository
import woowacourse.shopping.presentation.product.ProductViewModel

@ExtendWith(InstantTaskExecutorExtension::class)
class ProductViewModelTest {
    private lateinit var cartRepository: CartRepository
    private lateinit var productRepository: FakeProductRepository
    private lateinit var recentProductRepository: RecentProductRepository
    private lateinit var viewModel: ProductViewModel

    @BeforeEach
    fun setUp() {
        cartRepository = FakeCartRepository()
        productRepository = FakeProductRepository()
        recentProductRepository = FakeRecentProductRepository()

        viewModel = ProductViewModel(cartRepository, productRepository, recentProductRepository)
    }

    @Test
    fun `장바구니 전체 상품 개수를 조회할 수 있다`() =
        runTest {
            viewModel.fetchCartItemCount()

            val products = viewModel.cartItemCount.getOrAwaitValue()
            assertThat(products).isEqualTo(10)
        }

    @Test
    fun `fetchData 초기 호출 시 최근 본 상품 목록을 조회할 수 있다`() =
        runTest {
            viewModel.fetchData(0)

            val recentProducts = viewModel.recentProducts.getOrAwaitValue()
            assertThat(recentProducts).hasSize(20)
        }

    @Test
    fun `fetchData 초기 호출 시 상품 12개를 반환한다`() =
        runTest {
            viewModel.fetchData(0)

            val products = viewModel.products.getOrAwaitValue()
            assertThat(products).hasSize(12)
        }

    @Test
    fun `loadMore 호출 시 상품 12개가 추가된다`() =
        runTest {
            viewModel.fetchData(0)
            viewModel.loadMore()

            val products = viewModel.products.getOrAwaitValue()
            assertThat(products).hasSize(24)
        }

    @Test
    fun `모든 데이터를 불러오지 않으면 더보기 버튼은 true가 된다`() =
        runTest {
            viewModel.fetchData(0)
            repeat(1) { viewModel.loadMore() }

            val showLoadMore = viewModel.showLoadMore.getOrAwaitValue()
            assertThat(showLoadMore).isTrue()
        }

    @Test
    fun `모든 데이터를 불러오면 더보기 버튼은 false가 된다`() =
        runTest {
            viewModel.fetchData(0)
            repeat(8) { viewModel.loadMore() }

            val showLoadMore = viewModel.showLoadMore.getOrAwaitValue()
            assertThat(showLoadMore).isFalse()
        }
}
