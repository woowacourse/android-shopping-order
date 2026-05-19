package woowacourse.shopping.feature.productdetail

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.Product
import woowacourse.shopping.feature.MainDispatcherExtension
import woowacourse.shopping.feature.common.state.ProductUiModel
import woowacourse.shopping.feature.fake.FakeCartRepository
import woowacourse.shopping.feature.fake.FakeProductRepository
import woowacourse.shopping.fixture.TestProductFixture

private fun Product.toUiModel(): ProductUiModel = ProductUiModel(
    name = name,
    price = priceAmount(),
    imageUrl = imageUrl,
    id = id,
    quantity = 0,
)

@ExtendWith(MainDispatcherExtension::class)
class ProductDetailViewModelTest {
    private lateinit var viewModel: ProductDetailViewModel

    val comnProducts = TestProductFixture.products(35)

    @Test
    fun `초기 진입 시 상품 상세를 불러온다`() {
        // given: productId가 ProductRepository 에 전달된다
        viewModel = ProductDetailViewModel(
            productRepository = FakeProductRepository(
                comnProducts,
            ),
            cartRepository = FakeCartRepository(),
        )

        // when:  productId 로 initialLoading 을 호출할 때
        viewModel.initialLoading(
            productId = comnProducts.first().id,
        )
        // then:  product state 에 해당 상품 상세가 노출된다
        val expected = ProductDetailLoadingState.Success(
            product = comnProducts.first().toUiModel(),
        )
        assertThat(viewModel.uiState.value.productState).isEqualTo(expected)
    }

    @Test
    fun `recentProductId가 null이 아니고 productId와 다르면 최근 본 상품 목록을 불러온다`() {
        // given: recentProductId != null && recentProductId != productId이고
        viewModel = ProductDetailViewModel(
            productRepository = FakeProductRepository(
                comnProducts,
            ),
            cartRepository = FakeCartRepository(),
        )
        // when:  initialLoading 을 호출할 때
        viewModel.initialLoading(
            productId = comnProducts.first().id,
            recentProductId = comnProducts.last().id,
        )

        // then:  recentProduct state 에 최근 본 상품이 노출된다
        val expected = ProductDetailLoadingState.Success(
            product = comnProducts.last().toUiModel(),
        )
        assertThat(viewModel.uiState.value.recentProductState).isEqualTo(expected)
    }

    @Test
    fun `recentProductId가 null이면 최근 본 상품 목록을 불러오지 않는다`() {
        // given: recentProductId == null 인 상태이다
        viewModel = ProductDetailViewModel(
            productRepository = FakeProductRepository(
                comnProducts,
            ),
            cartRepository = FakeCartRepository(),
        )

        // when:  recentProductId 없이 initialLoading 을 호출할 때
        viewModel.initialLoading(
            productId = comnProducts.first().id,
            recentProductId = null,
        )

        // then:  recentProduct state 는 노출되지 않는다
        assertThat(viewModel.uiState.value.recentProductState).isEqualTo(ProductDetailLoadingState.None)
    }

    @Test
    fun `recentProductId가 productId와 같다면 최근 본 상품 목록을 불러오지 않는다`() {
        // given: recentProductId == productId 인 상태이다
        viewModel = ProductDetailViewModel(
            productRepository = FakeProductRepository(
                comnProducts,
            ),
            cartRepository = FakeCartRepository(),
        )

        // when:  productId 와 동일한 recentProductId 로 initialLoading 을 호출할 때
        viewModel.initialLoading(
            productId = comnProducts.first().id,
            recentProductId = comnProducts.first().id,
        )

        // then:  recentProduct state 는 노출되지 않는다
        assertThat(viewModel.uiState.value.recentProductState).isEqualTo(ProductDetailLoadingState.None)
    }

    @Test
    fun `addToCart를 하면 Cart에 새로운 항목으로 Insert 된다`() = runTest {
        // given: 카트에 해당 상품이 존재하지 않는다
        val cartRepository = FakeCartRepository(productCatalog = comnProducts)
        viewModel = ProductDetailViewModel(
            productRepository = FakeProductRepository(
                comnProducts,
            ),
            cartRepository = cartRepository,
        )
        viewModel.initialLoading(
            productId = comnProducts.first().id,
        )

        // when:  addToCart 를 호출할 때
        viewModel.addToCart()

        // then:  CartRepository 에 새로운 CartContent 로 insert 된다
        val cart = cartRepository.loadCart()
        assertThat(cart.cartContents).hasSize(1)
        assertThat(cart.cartContents.first().productId).isEqualTo(comnProducts.first().id)
        assertThat(cart.cartContents.first().quantity).isEqualTo(1)
    }
}
