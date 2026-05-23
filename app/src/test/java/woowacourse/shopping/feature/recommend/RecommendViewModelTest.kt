package woowacourse.shopping.feature.recommend

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.feature.MainDispatcherExtension
import woowacourse.shopping.feature.fake.FakeCartRepository
import woowacourse.shopping.feature.fake.FakeProductRepository
import woowacourse.shopping.feature.fake.FakeRecentProductRepository
import woowacourse.shopping.fixture.TestCartContentFixture
import woowacourse.shopping.fixture.TestProductFixture

@ExtendWith(MainDispatcherExtension::class)
class RecommendViewModelTest {

    private lateinit var viewModel: RecommendViewModel

    private val beverageProducts = TestProductFixture.products(15, category = "음료")
    private val clothesProducts = TestProductFixture.products(15, category = "의류", startIndex = 16)

    @Test
    fun `최근에 본 상품이 없으면 상품 목록을 불러온다`() {
        // given: RecentProductRepository가 비어 있는 뷰모델이 주어진다
        viewModel = RecommendViewModel(
            productRepository = FakeProductRepository(initial = TestProductFixture.products(5)),
            cartRepository = FakeCartRepository(productCatalog = beverageProducts + clothesProducts),
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
            recentProductRepository = FakeRecentProductRepository(initial = listOf(clothesProducts.first().id)),
        )
        // when:  초기 로딩을 호출할 때
        viewModel.initialLoading()

        // then:  추천 목록의 사이즈가 10 이다
        val expected = viewModel.uiState.value.recommendList
        assertThat(expected).hasSize(10)
    }

    @Test
    fun `카테고리 상품이 10개 미만이면 존재하는 개수만큼만 노출된다`() {
        // given: 해당 카테고리에 5개의 상품이 존재한다
        viewModel = RecommendViewModel(
            productRepository = FakeProductRepository(
                initial = beverageProducts.take(5),
            ),
            cartRepository = FakeCartRepository(productCatalog = beverageProducts.take(5)),
            recentProductRepository = FakeRecentProductRepository(initial = listOf(beverageProducts.first().id)),
        )
        // when: 초기 로딩을 호출할 때
        viewModel.initialLoading()

        // then:  추천 목록의 사이즈가 5개이다
        val expected = viewModel.uiState.value.recommendList
        assertThat(expected).hasSize(5)
    }

    @Test
    fun `이미 장바구니에 담긴 상품은 추천 목록에서 제외된다`() {
        // given: 카테고리 상품 중 일부가 이미 카트에 담겨 있다
        viewModel = RecommendViewModel(
            productRepository = FakeProductRepository(
                initial = beverageProducts.take(5),
            ),
            cartRepository = FakeCartRepository(
                initial = TestCartContentFixture.cartContentsOf(beverageProducts.take(2)),
                productCatalog = beverageProducts,
            ),
            recentProductRepository = FakeRecentProductRepository(initial = listOf(beverageProducts.first().id)),
        )
        // when:  초기 로딩을 호출할 때
        viewModel.initialLoading()

        // then:  추천 목록에 카트에 담긴 상품은 포함되지 않는다
        val expected = viewModel.uiState.value.recommendList
        assertThat(expected).hasSize(3)
    }

    @Test
    fun `선택 아이템과 추가된 아이템의 합산 금액이 노출된다`() = runTest {
        // given: 이전 화면에서 선택한 상품들이 입력된다
        val cartRepo = FakeCartRepository(
            initial = TestCartContentFixture.cartContentsOf(beverageProducts.take(2)),
            productCatalog = beverageProducts,
        )
        val cart = cartRepo.loadCart()
        val contentIds = cart.cartContents.map { it.id }

        viewModel = RecommendViewModel(
            productRepository = FakeProductRepository(
                initial = beverageProducts,
            ),
            cartRepository = cartRepo,
            recentProductRepository = FakeRecentProductRepository(initial = listOf(beverageProducts.first().id)),
        )
        viewModel.initialLoading()

        // when:  상품을 추가하고
        viewModel.increase(beverageProducts[3].id)
        viewModel.increase(beverageProducts[3].id)

        viewModel.increase(beverageProducts[4].id)
        // when:  금액 계산을 수행할 때
        val expected = viewModel.getTotalPrice(contentIds)

        // then:  이전 화면에서 선택한 상품들과 추천 화면에서 선택한 상품들의 합산 금액이 반환된다
        assertThat(expected).isEqualTo(16000)
    }
}
