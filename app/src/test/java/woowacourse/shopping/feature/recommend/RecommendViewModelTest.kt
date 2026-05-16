package woowacourse.shopping.feature.recommend

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.feature.MainDispatcherExtension
import woowacourse.shopping.feature.fake.FakeCartRepository
import woowacourse.shopping.feature.fake.FakeOrderRepository
import woowacourse.shopping.feature.fake.FakeProductRepository
import woowacourse.shopping.feature.fake.FakeRecentProductRepository
import woowacourse.shopping.fixture.TestCartContentFixture
import woowacourse.shopping.fixture.TestProductFixture

@ExtendWith(MainDispatcherExtension::class)
class RecommendViewModelTest {

    private lateinit var viewModel: RecommendViewModel

    private val comnProducts = TestProductFixture.products(35)
    private val comnCartContents = TestCartContentFixture.cartContentsOf(comnProducts)

    private val beverageProducts = TestProductFixture.products(15, category = "음료")
    private val clothesProducts = TestProductFixture.products(15, category = "의류", startIndex = 16)

    @Test
    fun `최근에 본 상품이 없으면 상품 목록을 불러온다`() {
        // given: RecentProductRepository가 비어 있는 뷰모델이 주어진다
        viewModel = RecommendViewModel(
            productRepository = FakeProductRepository(initial = TestProductFixture.products(5)),
            cartRepository = FakeCartRepository(productCatalog = comnProducts),
            orderRepository = FakeOrderRepository(),
            recentProductRepository = FakeRecentProductRepository(),
        )

        // when:  initialLoading 을 호출할 때
        viewModel.initialLoading()

        // then:  카테고리는 비어있고 상품 목록이 추천으로 노출된다
        assertThat(viewModel.uiState.value.recommendList).hasSize(5)
    }

    @Test
    fun `가장 최근에 본 상품의 카테고리의 상품만 불러온다`() {
        // given: 가장 최근 본 상품의 카테고리가 "의류"이다
        viewModel = RecommendViewModel(
            productRepository = FakeProductRepository(
                initial = beverageProducts + clothesProducts,
            ),
            cartRepository = FakeCartRepository(productCatalog = beverageProducts + clothesProducts),
            orderRepository = FakeOrderRepository(),
            recentProductRepository = FakeRecentProductRepository(initial = listOf(clothesProducts.first().id)),
        )

        // when:  initialLoading 을 호출할 때
        viewModel.initialLoading()

        // then:  의류 카테고리에 속하는 상품만 추천 목록에 노출된다
        val expectedSize = viewModel.uiState.value.recommendList.all { it.category == clothesProducts.first().category }
        assertThat(expectedSize).isEqualTo(true)
    }

    @Test
    fun `추천 상품은 최대 10개까지 노출된다`() {
        // given: 해당 카테고리에 10 개를 초과하는 상품이 존재한다
        viewModel = RecommendViewModel(
            productRepository = FakeProductRepository(
                initial = beverageProducts + clothesProducts,
            ),
            cartRepository = FakeCartRepository(productCatalog = beverageProducts + clothesProducts),
            orderRepository = FakeOrderRepository(),
            recentProductRepository = FakeRecentProductRepository(initial = listOf(clothesProducts.first().id)),
        )
        // when:  초기 로딩을 호출할 때
        viewModel.initialLoading()

        // then:  추천 목록의 사이즈가 10 이다
        val expected = viewModel.uiState.value.recommendList
        assertThat(expected).hasSize(10)
    }
}
