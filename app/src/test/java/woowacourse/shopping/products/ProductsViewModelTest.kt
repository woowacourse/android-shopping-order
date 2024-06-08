package woowacourse.shopping.products

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.cartItem
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.fake.FakeCartRepository
import woowacourse.shopping.fake.FakeProductRepository
import woowacourse.shopping.fake.FakeRecentProductRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.products
import woowacourse.shopping.recentProducts
import woowacourse.shopping.toProductUiModels
import woowacourse.shopping.toRecentProductUiModels
import woowacourse.shopping.ui.products.ProductsViewModel

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class ProductsViewModelTest {
    private lateinit var viewModel: ProductsViewModel

    private lateinit var productRepository: ProductRepository
    private lateinit var recentProductRepository: RecentProductRepository
    private lateinit var cartRepository: CartRepository

    @Test
    fun `한 페이지에는 20개의 상품이 있다`(): Unit =
        runBlocking {
            val products = products(20)
            setUpViewModel(savedProducts = products)

            val actual = viewModel.productUiModels.getOrAwaitValue()
            assertThat(actual).hasSize(20)
            assertThat(actual).isEqualTo(products.toProductUiModels(cartRepository).take(20))
        }

    @Test
    fun `상품이 40개인 경우 1페이지에서 20개의 상품을 불러온다`(): Unit =
        runBlocking {
            val products = products(40)
            setUpViewModel(savedProducts = products)

            val actual = viewModel.productUiModels.getOrAwaitValue()
            assertThat(actual).hasSize(20)
            assertThat(actual).isEqualTo(products.toProductUiModels(cartRepository).take(20))
        }

    @Test
    fun `상품이 5개인 경우 1페이지에서 5개의 상품을 불러온다`(): Unit =
        runBlocking {
            val products = products(5)
            setUpViewModel(savedProducts = products)

            val actual = viewModel.productUiModels.getOrAwaitValue()
            assertThat(actual).hasSize(5)
            assertThat(actual).isEqualTo(products.toProductUiModels(cartRepository).take(5))
        }

    @Test
    fun `상품이 10개이고 1페이지의 10번째 상품에 위치해 있는 경우 상품을 더 불러올 수 없다`(): Unit =
        runBlocking {
            // given
            val products = products(10)
            setUpViewModel(savedProducts = products)

            // when
            viewModel.changeSeeMoreVisibility(9)

            // then
            assertThat(viewModel.showLoadMore.getOrAwaitValue()).isFalse
        }

    @Test
    fun `상품이 25개이고 1페이지의 20번째 상품에 위치해 있는 경우 상품을 더 불러올 수 있다`(): Unit =
        runBlocking {
            // given
            val products = products(25)
            setUpViewModel(savedProducts = products)

            // when
            viewModel.changeSeeMoreVisibility(19)

            // then
            val actual = viewModel.showLoadMore.getOrAwaitValue()
            assertThat(actual).isTrue
        }

    @Test
    fun `상품이 25개이고 2페이지로 이동하면 25개의 상품이 보인다`(): Unit =
        runBlocking {
            // given
            val products = products(25)
            setUpViewModel(savedProducts = products)

            // when
            viewModel.loadPage()

            // then
            val actual = viewModel.productUiModels.getOrAwaitValue()
            assertThat(actual).hasSize(25)
            assertThat(actual).isEqualTo(products.toProductUiModels(cartRepository).take(25))
        }

    @Test
    fun `상품이 25개이고 2페이지의 25번째 상품에 위치해 있는 경우 상품을 더 불러올 수 없다`(): Unit =
        runBlocking {
            // given
            val products = products(25)
            setUpViewModel(savedProducts = products)

            // when
            viewModel.loadPage()
            viewModel.changeSeeMoreVisibility(24)

            // then
            val actual = viewModel.showLoadMore.getOrAwaitValue()
            assertThat(actual).isFalse
        }

    @Test
    fun `장바구니에 담겨지지 않은 상품의 개수를 증가시키면 장바구니에 담긴다`(): Unit =
        runBlocking {
            // given
            val products = products(10)
            setUpViewModel(savedProducts = products)

            // when
            viewModel.increaseQuantity(0)

            // then
            cartRepository.findByProductId(0)
                .onSuccess { cartItem ->
                    assertThat(cartItem).isNotNull
                    assertThat(cartItem?.product?.id).isEqualTo(0)
                    assertThat(cartItem?.quantity).isEqualTo(Quantity(1))
                }.onFailure { assertThat(true).isFalse }
        }

    @Test
    fun `장바구니에 담겨진 상품의 개수를 3개에서 4개로 증가시킨다`(): Unit =
        runBlocking {
            // given
            val products = products(10)
            val cartItems = listOf(cartItem(id = 0, quantity = 3))
            setUpViewModel(savedProducts = products, savedCartItems = cartItems)

            // when
            viewModel.increaseQuantity(0)

            // then
            cartRepository.findByProductId(0)
                .onSuccess { cartItem ->
                    assertThat(cartItem).isNotNull
                    assertThat(cartItem?.product?.id).isEqualTo(0)
                    assertThat(cartItem?.quantity).isEqualTo(Quantity(4))
                }.onFailure { assertThat(true).isFalse }
        }

    @Test
    fun `장바구니에 1개가 담겨진 상품의 개수를 감소시키면 장바구니에서 삭제된다`(): Unit =
        runBlocking {
            // given
            val products = products(10)
            val cartItems = listOf(cartItem(0, 1))
            setUpViewModel(savedProducts = products, savedCartItems = cartItems)

            // when
            viewModel.decreaseQuantity(0)

            // then
            cartRepository.findByProductId(0)
                .onSuccess { assertThat(it).isNull() }
                .onFailure { assertThat(true).isFalse }
        }

    @Test
    fun `장바구니에 담겨진 상품의 개수를 6개에서 5개로 감소시킨다`(): Unit =
        runBlocking {
            // given
            val products = products(10)
            val cartItems = listOf(cartItem(0, 6))
            setUpViewModel(savedProducts = products, savedCartItems = cartItems)

            // when
            viewModel.decreaseQuantity(0)

            // then
            cartRepository.findByProductId(0)
                .onSuccess { cartItem ->
                    assertThat(cartItem).isNotNull
                    assertThat(cartItem?.product?.id).isEqualTo(0)
                    assertThat(cartItem?.quantity).isEqualTo(Quantity(5))
                }.onFailure {
                    assertThat(true).isFalse
                }
        }

    @Test
    fun `최근 본 상품이 5개인 경우 5개의 최근 본 상품을 불러온다`(): Unit =
        runBlocking {
            val products = products(5)
            val recentProducts = recentProducts(5)
            setUpViewModel(savedProducts = products, savedRecentProducts = recentProducts)

            val actual = viewModel.recentProductUiModels.getOrAwaitValue()
            assertThat(actual).hasSize(5)
            assertThat(actual).isEqualTo(recentProducts.take(5).toRecentProductUiModels().reversed())
        }

    @Test
    fun `최근 본 상품이 15개인 경우 10개의 최근 본 상품을 불러온다`(): Unit =
        runBlocking {
            val products = products(15)
            val recentProducts = recentProducts(15)
            setUpViewModel(savedProducts = products, savedRecentProducts = recentProducts)

            val actual = viewModel.recentProductUiModels.getOrAwaitValue()
            assertThat(actual).hasSize(10)
            assertThat(actual).isEqualTo(recentProducts.take(10).toRecentProductUiModels().reversed())
        }

    @Test
    fun `각 상품 2개와 3개를 장바구니에 담으면 총 상품의 수량이 5개이다`(): Unit =
        runBlocking {
            val products = products(10)
            val cartItems = listOf(cartItem(id = 0, quantity = 2), cartItem(id = 1, quantity = 3))
            setUpViewModel(savedProducts = products, savedCartItems = cartItems)

            val actual = viewModel.cartTotalQuantity.getOrAwaitValue()
            assertThat(actual).isEqualTo(5)
        }

    private fun setUpViewModel(
        savedProducts: List<Product>,
        savedRecentProducts: List<RecentProduct> = emptyList(),
        savedCartItems: List<CartItem> = emptyList(),
    ) {
        productRepository = FakeProductRepository(savedProducts)
        recentProductRepository = FakeRecentProductRepository(savedRecentProducts)
        cartRepository = FakeCartRepository(savedCartItems)
        viewModel = ProductsViewModel(productRepository, recentProductRepository, cartRepository)
    }
}
