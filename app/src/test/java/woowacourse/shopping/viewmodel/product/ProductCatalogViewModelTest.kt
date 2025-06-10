package woowacourse.shopping.viewmodel.product

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.usecase.cart.AddToCartUseCase
import woowacourse.shopping.domain.usecase.cart.GetCartProductsUseCase
import woowacourse.shopping.domain.usecase.cart.GetTotalCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.cart.RemoveFromCartUseCase
import woowacourse.shopping.domain.usecase.cart.UpdateCartQuantityUseCase
import woowacourse.shopping.domain.usecase.product.GetProductsUseCase
import woowacourse.shopping.domain.usecase.product.GetRecentProductsUseCase
import woowacourse.shopping.fixture.FakeCartProductRepository
import woowacourse.shopping.fixture.FakeProductRepository
import woowacourse.shopping.fixture.FakeRecentProductRepository
import woowacourse.shopping.view.product.catalog.ProductCatalogViewModel
import woowacourse.shopping.view.product.catalog.adapter.ProductCatalogItem
import woowacourse.shopping.viewmodel.CoroutinesTestExtension
import woowacourse.shopping.viewmodel.InstantTaskExecutorExtension
import woowacourse.shopping.viewmodel.getOrAwaitValue

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class ProductCatalogViewModelTest {
    private lateinit var viewModel: ProductCatalogViewModel
    private lateinit var productRepository: ProductRepository
    private lateinit var cartProductRepository: CartProductRepository
    private lateinit var recentProductRepository: RecentProductRepository

    private lateinit var getRecentProductsUseCase: GetRecentProductsUseCase
    private lateinit var getProductsUseCase: GetProductsUseCase
    private lateinit var getCartProductsUseCase: GetCartProductsUseCase
    private lateinit var getTotalCartProductQuantityUseCase: GetTotalCartProductQuantityUseCase
    private lateinit var removeFromCartUseCase: RemoveFromCartUseCase
    private lateinit var updateCartQuantityUseCase: UpdateCartQuantityUseCase
    private lateinit var addToCartUseCase: AddToCartUseCase

    @BeforeEach
    fun setup() =
        runTest {
            productRepository = FakeProductRepository()
            cartProductRepository = FakeCartProductRepository()
            recentProductRepository = FakeRecentProductRepository()

            getRecentProductsUseCase = GetRecentProductsUseCase(recentProductRepository)
            getProductsUseCase = GetProductsUseCase(productRepository)
            getCartProductsUseCase = GetCartProductsUseCase(cartProductRepository)
            getTotalCartProductQuantityUseCase = GetTotalCartProductQuantityUseCase(cartProductRepository)
            removeFromCartUseCase = RemoveFromCartUseCase(cartProductRepository)
            updateCartQuantityUseCase = UpdateCartQuantityUseCase(cartProductRepository, removeFromCartUseCase)
            addToCartUseCase = AddToCartUseCase(cartProductRepository, updateCartQuantityUseCase)

            repeat(12) { id -> cartProductRepository.insert(id, 1) }
            viewModel =
                ProductCatalogViewModel(
                    getRecentProductsUseCase,
                    getProductsUseCase,
                    getCartProductsUseCase,
                    getTotalCartProductQuantityUseCase,
                    addToCartUseCase,
                    updateCartQuantityUseCase,
                )
            viewModel.loadCatalog()
        }

    @Test
    fun `초기 로드 시 첫 페이지의 상품과 더보기 버튼이 포함된다`() {
        // when
        val items = viewModel.productCatalogItems.getOrAwaitValue()

        // then
        assertEquals(21, items.size) // 20 ProductItem + 1 LoadMoreItem
        assert(items.first() is ProductCatalogItem.ProductItem)
        assert(items.last() is ProductCatalogItem.LoadMoreItem)
    }

    @Test
    fun `더보기 버튼 클릭 시 다음 페이지의 상품이 추가되고 더보기 버튼이 포함된다`() {
        // when
        viewModel.onLoadMoreClick()
        val items = viewModel.productCatalogItems.getOrAwaitValue()

        // then
        assertEquals(41, items.size) // 40 ProductItem + 1 LoadMoreItem
        assert(items.last() is ProductCatalogItem.LoadMoreItem)
    }

    @Test
    fun `마지막 페이지 이후에는 더보기 버튼이 없다`() {
        // when
        repeat(4) { viewModel.onLoadMoreClick() }
        val items = viewModel.productCatalogItems.getOrAwaitValue()

        // then
        assertEquals(100, items.size)
        assert(items.none { it is ProductCatalogItem.LoadMoreItem })
    }

    @Test
    fun `더보기 후에도 이전 페이지 상품이 유지된다`() {
        viewModel.onLoadMoreClick()

        val items =
            viewModel.productCatalogItems
                .getOrAwaitValue()
                .filterIsInstance<ProductCatalogItem.ProductItem>()
                .map { it.product }

        assertEquals("Product 0", items.first().name)
        assertEquals("Product 39", items.last().name)
    }

    @Test
    fun `상품 수량 증가 클릭 시 수량이 1 증가한다`() {
        // given
        val product =
            viewModel.productCatalogItems
                .getOrAwaitValue()
                .filterIsInstance<ProductCatalogItem.ProductItem>()
                .first()
                .product

        // when
        viewModel.onQuantityIncreaseClick(product)

        // then
        val updatedItem =
            viewModel.productCatalogItems
                .getOrAwaitValue()
                .filterIsInstance<ProductCatalogItem.ProductItem>()
                .first { it.product.id == product.id }

        assertEquals(2, updatedItem.quantity)
    }

    @Test
    fun `상품 수량 감소 클릭 시 수량이 1 감소한다`() {
        // given
        val product =
            viewModel.productCatalogItems
                .getOrAwaitValue()
                .filterIsInstance<ProductCatalogItem.ProductItem>()
                .first()
                .product
        viewModel.onQuantityIncreaseClick(product)

        // when
        viewModel.onQuantityDecreaseClick(product)

        // then
        val updatedItem =
            viewModel.productCatalogItems
                .getOrAwaitValue()
                .filterIsInstance<ProductCatalogItem.ProductItem>()
                .first { it.product.id == product.id }

        assertEquals(1, updatedItem.quantity)
    }

    @Test
    fun `상품 수량 변경 시 총 수량에 반영된다`() {
        // given
        val product =
            viewModel.productCatalogItems
                .getOrAwaitValue()
                .filterIsInstance<ProductCatalogItem.ProductItem>()
                .first()
                .product
        val totalQuantity = viewModel.totalQuantity.getOrAwaitValue()

        // when
        viewModel.onQuantityIncreaseClick(product)
        val actual = viewModel.totalQuantity.getOrAwaitValue() - totalQuantity

        // then
        assertEquals(1, actual)
    }

    @Test
    fun `상품 클릭 시 선택된 상품이 selectedProduct에 반영된다`() {
        // given
        val product =
            viewModel.productCatalogItems
                .getOrAwaitValue()
                .filterIsInstance<ProductCatalogItem.ProductItem>()
                .first()
                .product

        // when
        viewModel.onProductClick(product)

        // then
        assertEquals(product, viewModel.selectedProduct.getValue())
    }
}
