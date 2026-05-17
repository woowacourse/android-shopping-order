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
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.ui.catalog.ShoppingViewModel
import woowacourse.shopping.viewmodel.fakes.FakeCartRepository
import woowacourse.shopping.viewmodel.fakes.FakeProductRepository
import woowacourse.shopping.viewmodel.fakes.FakeRecentlyViewedProductRepository

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private lateinit var viewModel: ShoppingViewModel
    private lateinit var fakeProductRepository: FakeProductRepository
    private lateinit var fakeRecentlyViewedProductRepository: FakeRecentlyViewedProductRepository
    private lateinit var fakeCartRepository: FakeCartRepository

    @BeforeEach
    fun initViewModel() {
        fakeCartRepository = FakeCartRepository()
        fakeRecentlyViewedProductRepository = FakeRecentlyViewedProductRepository()
        fakeProductRepository = FakeProductRepository()

        viewModel = ShoppingViewModel(
            cartRepository = fakeCartRepository,
            recentlyViewedProductRepository = fakeRecentlyViewedProductRepository,
            productRepository = fakeProductRepository
        )
    }

    @Test
    fun `상품 목록을 성공적으로 불러오면 products 상태가 갱신되어야 한다`() = runTest {
        val products = listOf(Product(id = 1L, name = "사과", price = 1000, imageUri = "uri", category = "과일"))
        fakeProductRepository.setProducts(products)

        viewModel.fetchProducts(0)

        val actualProduct = viewModel.products
        assertEquals(1, actualProduct.value.size())
        assertEquals("사과", actualProduct.value.findWithId(1L)?.name)
    }

    @Test
    fun `장바구니에 상품을 추가하면 cart에 실시간으로 반영되어야 한다`() = runTest {
        val product = Product(
            id = 1L,
            imageUri = "테스트",
            name = "테스트",
            price = 1000,
            category = "카테고리"
        )

        fakeProductRepository.setProducts(listOf(product))

        val collectJob = backgroundScope.launch(context = UnconfinedTestDispatcher(testScheduler)) {
            viewModel.cart.collect()
        }

        viewModel.fetchProducts(0)
        viewModel.addToCart(PurchaseProduct(1L, product, 2))

        val cart = viewModel.cart.value
        val cartItem = cart.findById(1L)

        assertEquals(2, cartItem?.count)
        assertEquals(2000, cartItem?.totalPrice)

        collectJob.cancel()
    }

    @Test
    fun `최근 본 상품에 상품을 추가하면 viewHistory의 상태가 변경된다`() = runTest {
        val product = Product(
            id = 1L,
            imageUri = "테스트",
            name = "테스트",
            price = 1000,
            category = "카테고리"
        )
        fakeProductRepository.setProducts(listOf(product))

        val collectJob = backgroundScope.launch(context = UnconfinedTestDispatcher(testScheduler)) {
            viewModel.recentlyViewedProducts.collect()
        }

        viewModel.fetchProducts()
        viewModel.updateHistory(product)

        val history = viewModel.recentlyViewedProducts.value

        assertEquals(1L, history.products[0].id)
        assertEquals("테스트", history.products[0].name)
        collectJob.cancel()
    }

    @Test
    fun `특정 상품의 수량을 변경하면 장바구니에 반영되어야 한다`() = runTest {
        val product = Product(
            id = 1L,
            imageUri = "테스트",
            name = "테스트",
            price = 1000,
            category = "카테고리"
        )

        fakeProductRepository.setProducts(listOf(product))

        val collectJob = backgroundScope.launch(context = UnconfinedTestDispatcher(testScheduler)) {
            viewModel.cart.collect()
        }

        viewModel.fetchProducts(0)

        viewModel.addToCart(PurchaseProduct(1L, product, 2))

        viewModel.updateCountWithID(1L, 3)

        val purchaseProducts = viewModel.cart.value
        assertEquals(5, purchaseProducts.findById(1L)?.count)
        collectJob.cancel()
    }
}
