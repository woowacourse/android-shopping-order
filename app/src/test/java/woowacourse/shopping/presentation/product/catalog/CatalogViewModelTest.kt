package woowacourse.shopping.presentation.product.catalog

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.fixture.FakeCartItemRepository
import woowacourse.shopping.fixture.FakeCatalogItemRepository
import woowacourse.shopping.fixture.FakeViewedItemRepository
import woowacourse.shopping.util.InstantTaskExecutorExtension
import woowacourse.shopping.util.getOrAwaitValue

@ExtendWith(InstantTaskExecutorExtension::class)
class CatalogViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CatalogViewModel

    @Test
    fun `초기 상태 테스트`() {
        viewModel =
            CatalogViewModel(
                FakeCatalogItemRepository(20),
                FakeCartItemRepository(0),
                FakeViewedItemRepository(0),
            )

        viewModel.loadCatalogProducts(10)

        val hasNext = viewModel.pagingData.value?.hasNext
        val products = viewModel.pagingData.value?.products ?: emptyList()
        val page = viewModel.pagingData.value?.page ?: 0

        assertThat(page).isEqualTo(0)
        assertThat(hasNext).isTrue
        assertThat(products).hasSize(10)
    }

    @Test
    fun `더보기 버튼을 눌렀을 때 페이지가 증가되고 상품이 로드된다`() {
        // given
        viewModel =
            CatalogViewModel(
                FakeCatalogItemRepository(30),
                FakeCartItemRepository(0),
                FakeViewedItemRepository(0),
            )

        viewModel.loadCatalogProducts(20)
        val page = viewModel.pagingData.value?.page ?: 0
        assertThat(page).isEqualTo(0)

        viewModel.loadNextCatalogProducts()
        val products = viewModel.pagingData.value?.products ?: emptyList()
        val afterPage = viewModel.pagingData.value?.page ?: 0

        assertThat(afterPage).isEqualTo(1)
        assertThat(products.size).isEqualTo(30)
    }

    @Test
    fun `상품 목록이 20개 이상이면 더보기 버튼이 활성화된다`() {
        // given
        viewModel =
            CatalogViewModel(
                FakeCatalogItemRepository(30),
                FakeCartItemRepository(0),
                FakeViewedItemRepository(0),
            )

        viewModel.loadCatalogProducts()
        val hasNext = viewModel.pagingData.value?.hasNext

        assertThat(hasNext).isEqualTo(true)
    }

    @Test
    fun `최근 본 상품이 있는 경우에 10개 이하이면 모든 데이터를 불러온다`() {
        viewModel =
            CatalogViewModel(
                FakeCatalogItemRepository(30),
                FakeCartItemRepository(0),
                FakeViewedItemRepository(9),
            )

        viewModel.loadRecentViewedItems()

        val viewedSize = viewModel.recentViewedItems.value?.size
        val hasViewed = viewModel.hasRecentViewedItems.value

        assertThat(viewedSize).isEqualTo(9)
        assertThat(hasViewed).isEqualTo(true)
    }

    @Test
    fun `최근 본 상품이 있는 경우에 10개 이상이면 10개의 데이터만 불러온다`() {
        viewModel =
            CatalogViewModel(
                FakeCatalogItemRepository(30),
                FakeCartItemRepository(0),
                FakeViewedItemRepository(20),
            )
        viewModel.loadRecentViewedItems()

        val viewedSize = viewModel.recentViewedItems.value?.size
        val hasViewed = viewModel.hasRecentViewedItems.value

        assertThat(viewedSize).isEqualTo(10)
        assertThat(hasViewed).isEqualTo(true)
    }
}
