package woowacourse.shopping.feature.productlist

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
class ProductListViewModelTest {

    private lateinit var viewModel: ProductListViewModel

    val comnProducts = TestProductFixture.products(35)

    @Test
    fun `초기 진입 시 상품 목록을 불러와 노출한다`() {
        // given: ProductRepository 에 상품들이 존재하고 ViewModel 이 초기화된다
        val productRepo = FakeProductRepository(
            initial = TestProductFixture.products(3),
        )
        viewModel = ProductListViewModel(
            productRepository = productRepo,
            cartRepository = FakeCartRepository(),
            recentProductRepository = FakeRecentProductRepository(),
        )

        // when:  initialLoading을 했을 때
        viewModel.initialLoading()

        // then:  products state 에 상품 목록이 노출된다
        assertThat(viewModel.uiState.value.productUiModels).hasSize(3)
    }

    @Test
    fun `상품을 추가 로딩하면 기존 상품 목록에 추가된다`() {
        // given: 초기 상품 목록이 로드되어 있고
        val productRepo = FakeProductRepository(
            initial = comnProducts,
        )
        viewModel = ProductListViewModel(
            productRepository = productRepo,
            cartRepository = FakeCartRepository(),
            recentProductRepository = FakeRecentProductRepository(),
        )
        viewModel.initialLoading()
        // when:  loadMore 를 호출할 때
        viewModel.loadingFetch()

        // then:  상품이 추가 로딩되어 기존 상품 목록에 추가된다
        assertThat(viewModel.uiState.value.productUiModels).hasSize(35)
    }

    @Test
    fun `이미 존재하는 상품을 추가하면 수량이 누적된다`() {
        // given: 카트에 productId 가 1인 상품이 1개 담겨 있다
        val productRepo = FakeProductRepository(
            initial = comnProducts,
        )

        val cartRepo = FakeCartRepository(
            initial = TestCartContentFixture.cartContentsOf(
                comnProducts,
            ),
        )
        viewModel = ProductListViewModel(
            productRepository = productRepo,
            cartRepository = cartRepo,
            recentProductRepository = FakeRecentProductRepository(),
        )
        viewModel.initialLoading()

        // when:  동일한 상품을 다시 추가하고 cart를 갱신할 때
        viewModel.increase(comnProducts.first().id)
        viewModel.cartRefresh()

        // then:  해당 상품의 수량이 2 로 누적된다
        val productUiModel = viewModel.uiState.value.productUiModels.find { it.id == comnProducts.first().id }
        assertThat(productUiModel?.quantity).isEqualTo(2)
    }

    @Test
    fun `상품을 추가하면 카트 수량에 반영된다`() {
        // given: 비어 있는 카트가 주어지고 상품을 카트에 추가한다.

        viewModel = ProductListViewModel(
            productRepository = FakeProductRepository(initial = comnProducts),
            cartRepository = FakeCartRepository(productCatalog = comnProducts),
            recentProductRepository = FakeRecentProductRepository(),
        )
        viewModel.initialLoading()

        // when:  상품을 추가하고 카트를 갱신할 때
        assertThat(viewModel.uiState.value.cartTotalQuantity).isEqualTo(0)
        viewModel.increase(comnProducts.first().id)

        // then:  totalQuantity state 가 1 증가한다
        assertThat(viewModel.uiState.value.cartTotalQuantity).isEqualTo(1)
    }

    @Test
    fun `보유 수량보다 적게 감소시키면 수량이 줄어든다`() {
        // given: 카트에 동일 상품이 3개 담겨 있다
        val targetProduct = comnProducts.first()
        val cartRepo = FakeCartRepository(
            initial = listOf(
                TestCartContentFixture.cartContent(product = targetProduct, quantity = 3),
            ),
            productCatalog = comnProducts,
        )
        viewModel = ProductListViewModel(
            productRepository = FakeProductRepository(initial = comnProducts),
            cartRepository = cartRepo,
            recentProductRepository = FakeRecentProductRepository(),
        )
        viewModel.initialLoading()

        // when:  해당 상품을 1개 감소시킬 때
        viewModel.decrease(targetProduct.id)

        // then:  해당 상품의 수량이 2 가 된다
        val productUiModel = viewModel.uiState.value.productUiModels.find { it.id == targetProduct.id }
        assertThat(productUiModel?.quantity).isEqualTo(2)
    }

    @Test
    fun `1에서 0으로 수량 감소시 해당 상품이 제거된다`() {
        // given: 카트에 동일 상품이 1개 담겨 있다
        val targetProduct = comnProducts.first()
        val cartRepo = FakeCartRepository(
            initial = listOf(
                TestCartContentFixture.cartContent(product = targetProduct, quantity = 1),
            ),
            productCatalog = comnProducts,
        )
        viewModel = ProductListViewModel(
            productRepository = FakeProductRepository(initial = comnProducts),
            cartRepository = cartRepo,
            recentProductRepository = FakeRecentProductRepository(),
        )
        viewModel.initialLoading()

        // when:  해당 상품을 1개 감소시킬 때
        viewModel.decrease(targetProduct.id)

        // then:  카트에서 해당 상품이 제거되어 수량이 0 이 된다
        val productUiModel = viewModel.uiState.value.productUiModels.find { it.id == targetProduct.id }
        assertThat(productUiModel?.quantity).isEqualTo(0)
    }

    @Test
    fun `상품을 삭제하면 카트 수량에 반영된다`() {
        // given: 카트에 3 종류의 상품이 각각 1개씩 담겨 있다
        val cartRepo = FakeCartRepository(
            initial = TestCartContentFixture.cartContentsOf(comnProducts.take(3)),
            productCatalog = comnProducts,
        )
        viewModel = ProductListViewModel(
            productRepository = FakeProductRepository(initial = comnProducts),
            cartRepository = cartRepo,
            recentProductRepository = FakeRecentProductRepository(),
        )
        viewModel.initialLoading()
        assertThat(viewModel.uiState.value.cartTotalQuantity).isEqualTo(3)

        // when:  상품 하나를 0 으로 감소시켜 제거할 때
        viewModel.decrease(comnProducts.first().id)

        // then:  totalQuantity state 가 2 로 감소한다
        assertThat(viewModel.uiState.value.cartTotalQuantity).isEqualTo(2)
    }

    @Test
    fun `장바구니에서 수량이 바뀐 뒤 cartRefresh를 호출하면 state에 반영된다`() = runTest {
        // given: ViewModel 외부(다른 화면)에서 카트 수량이 변경된 상태이다
        val cartRepo = FakeCartRepository(productCatalog = comnProducts)
        viewModel = ProductListViewModel(
            productRepository = FakeProductRepository(initial = comnProducts),
            cartRepository = cartRepo,
            recentProductRepository = FakeRecentProductRepository(),
        )
        viewModel.initialLoading()
        cartRepo.insert(comnProducts.first().id, 1)
        assertThat(viewModel.uiState.value.cartTotalQuantity).isEqualTo(0)

        // when:  cartRefresh 를 호출할 때
        viewModel.cartRefresh()

        // then:  products state 의 수량이 최신 카트 상태로 갱신된다
        assertThat(viewModel.uiState.value.cartTotalQuantity).isEqualTo(1)
        val productUiModel =
            viewModel.uiState.value.productUiModels.find { it.id == comnProducts.first().id }
        assertThat(productUiModel?.quantity).isEqualTo(1)
    }

    @Test
    fun `상품을 누르면 최근 본 상품 목록 맨 앞에 추가된다`() = runTest {
        // given: 최근 본 상품 목록에 다른 상품이 존재한다
        val recentRepo = FakeRecentProductRepository(initial = listOf(comnProducts[1].id))
        viewModel = ProductListViewModel(
            productRepository = FakeProductRepository(initial = comnProducts),
            cartRepository = FakeCartRepository(),
            recentProductRepository = recentRepo,
        )
        viewModel.initialLoading()

        // when:  특정 상품을 클릭할 때
        viewModel.insertRecentProduct(comnProducts.first().id)

        // then:  해당 상품이 RecentProductRepository 의 맨 앞에 삽입된다
        assertThat(recentRepo.loadProducts().first()).isEqualTo(comnProducts.first().id)
    }

    @Test
    fun `최근 본 상품이 노출된다`() {
        // given: RecentProductRepository 에 최근 본 상품 2개가 존재한다
        val recentRepo = FakeRecentProductRepository(
            initial = listOf(comnProducts[2].id, comnProducts[5].id),
        )
        viewModel = ProductListViewModel(
            productRepository = FakeProductRepository(initial = comnProducts),
            cartRepository = FakeCartRepository(),
            recentProductRepository = recentRepo,
        )

        // when:  ViewModel 이 초기화될 때
        viewModel.initialLoading()

        // then:  recentProducts state 에 최근 본 상품이 순서대로 노출된다
        assertThat(viewModel.uiState.value.recentProducts).hasSize(2)
        assertThat(viewModel.uiState.value.mostRecentProductId).isEqualTo(comnProducts[2].id)
    }
}
