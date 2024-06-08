package woowacourse.shopping.cart

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.cartItem
import woowacourse.shopping.cartItems
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
import woowacourse.shopping.product
import woowacourse.shopping.products
import woowacourse.shopping.recentProducts
import woowacourse.shopping.toCartUiModels
import woowacourse.shopping.ui.cart.CartViewModel

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class CartViewModelTest {
    private lateinit var viewModel: CartViewModel

    private lateinit var productRepository: ProductRepository
    private lateinit var recentProductRepository: RecentProductRepository
    private lateinit var cartRepository: CartRepository

    @Test
    fun `장바구니에 20개의 상품이 있으면 20개 상품을 모두 로드한다`(): Unit =
        runBlocking {
            // given
            val products = products(20)
            val cartItems = cartItems(20)
            setUpViewModel(savedProducts = products, savedCartItems = cartItems)

            // when
            viewModel.loadAllCartItems()

            // then
            val actual = viewModel.cartUiModels.getOrAwaitValue()
            assertThat(actual.size).isEqualTo(20)
            assertThat(actual).isEqualTo(cartItems.toCartUiModels(products))
        }

    @Test
    fun `장바구니에 담긴 상품을 삭제한다`(): Unit =
        runBlocking {
            // given
            val cartItems = cartItems(1)
            setUpViewModel(savedProducts = products(1), savedCartItems = cartItems)
            viewModel.loadAllCartItems()

            // when
            viewModel.deleteCartItem(cartItems.first().product.id)

            // then
            assertThat(viewModel.cartUiModels.getOrAwaitValue().size).isEqualTo(0)
        }

    @Test
    fun `장바구니에 담겨져 있지 않은 상품의 개수를 증가시키면 장바구니에 추가된다`(): Unit =
        runBlocking {
            // given
            setUpViewModel(savedProducts = products(5))
            viewModel.loadAllCartItems()

            // when
            viewModel.increaseQuantity(0)

            // then
            val actual = viewModel.cartUiModels.getOrAwaitValue()
            assertThat(actual.size).isEqualTo(1)
            assertThat(actual.uiModels.first().productId).isEqualTo(0)
        }

    @Test
    fun `장바구니에 1개의 상품이 있고 1개의 상품을 삭제하면 장바구니가 비어있다`(): Unit =
        runBlocking {
            // given
            setUpViewModel(savedProducts = products(5), savedCartItems = cartItems(1))
            viewModel.loadAllCartItems()

            // when
            viewModel.decreaseQuantity(0)

            // then
            val actual = viewModel.cartUiModels.getOrAwaitValue()
            assertThat(actual.isEmpty()).isTrue
        }

    @Test
    fun `장바구니에 담겨있는 상품의 개수를 2개에서 3개로 증가시킨다`(): Unit =
        runBlocking {
            // given
            val cartItems = listOf(cartItem(id = 0, quantity = 2))
            setUpViewModel(savedProducts = products(5), savedCartItems = cartItems)
            viewModel.loadAllCartItems()

            // when
            viewModel.increaseQuantity(0)

            // then
            val actual = viewModel.cartUiModels.getOrAwaitValue()
            assertThat(actual.uiModels.first().quantity).isEqualTo(Quantity(3))
        }

    @Test
    fun `장바구니에 담겨있는 상품의 개수를 3개에서 2개로 감소시킨다`(): Unit =
        runBlocking {
            // given
            val cartItems = listOf(cartItem(id = 0, quantity = 3))
            setUpViewModel(savedProducts = products(5), savedCartItems = cartItems)
            viewModel.loadAllCartItems()

            // when
            viewModel.decreaseQuantity(0)

            // then
            val actual = viewModel.cartUiModels.getOrAwaitValue()
            assertThat(actual.uiModels.first().quantity).isEqualTo(Quantity(2))
        }

    @Test
    fun `장바구니에 담겨있는 상품의 개수를 1개에서 0개로 감소시키는 경우 장바구니에서 상품이 삭제된다`(): Unit =
        runBlocking {
            // given
            val cartItems = listOf(cartItem(id = 0, quantity = 1))
            setUpViewModel(savedProducts = products(5), savedCartItems = cartItems)
            viewModel.loadAllCartItems()

            // when
            viewModel.decreaseQuantity(0)

            // then
            val actual = viewModel.cartUiModels.getOrAwaitValue()
            assertThat(actual.isEmpty()).isTrue
        }

    @Test
    fun `product id 0을 가진 상품을 장바구니에서 삭제한다`(): Unit =
        runBlocking {
            // given
            val cartItems = listOf(cartItem(id = 0, quantity = 5))
            setUpViewModel(savedProducts = products(5), savedCartItems = cartItems)
            viewModel.loadAllCartItems()

            // when
            viewModel.deleteCartItem(0)

            // then
            val actual = viewModel.cartUiModels.getOrAwaitValue()
            assertThat(actual.isEmpty()).isTrue
        }

    @Test
    fun `최근 본 상품이 없는 경우 상품을 추천하지 않는다`(): Unit =
        runBlocking {
            // given
            setUpViewModel(savedProducts = products(5), savedCartItems = cartItems(3))
            viewModel.loadAllCartItems()

            // when
            viewModel.loadRecommendProducts()

            // then
            assertThat(viewModel.recommendProductUiModels.isInitialized).isFalse
        }

    @Test
    fun `food 상품을 가장 최근에 보고 food 상품이 5개인 경우 5개의 상품을 추천한다`(): Unit =
        runBlocking {
            // given
            val products = List(5) { product(it).copy(category = "food") }
            val recentProducts = recentProducts(1)
            setUpViewModel(savedProducts = products, savedRecentProducts = recentProducts)
            viewModel.loadAllCartItems()

            // when
            viewModel.loadRecommendProducts()

            // then
            val actual = viewModel.recommendProductUiModels.getOrAwaitValue()
            assertThat(actual).hasSize(5)
        }

    @Test
    fun `food 상품을 가장 최근에 보고 food 상품이 15개인 경우 10개의 상품을 추천한다`(): Unit =
        runBlocking {
            // given
            val products = List(15) { product(it).copy(category = "food") }
            val recentProducts = recentProducts(1)
            setUpViewModel(savedProducts = products, savedRecentProducts = recentProducts)
            viewModel.loadAllCartItems()

            // when
            viewModel.loadRecommendProducts()

            // then
            val actual = viewModel.recommendProductUiModels.getOrAwaitValue()
            assertThat(actual).hasSize(10)
        }

    @Test
    fun `food 상품을 가장 최근에 보고 모든 food 상품이 장바구니에 있는 경우 상품을 추천하지 않는다`(): Unit =
        runBlocking {
            // given
            val products = List(15) { product(it).copy(category = "food") }
            val recentProducts = recentProducts(1)
            setUpViewModel(
                savedProducts = products,
                savedRecentProducts = recentProducts,
                savedCartItems = cartItems(15),
            )
            viewModel.loadAllCartItems()

            // when
            viewModel.loadRecommendProducts()

            // then
            val actual = viewModel.recommendProductUiModels.getOrAwaitValue()
            assertThat(actual).isEmpty()
        }

    @Test
    fun `장바구니에 상품이 3개가 있고 1개를 선택한 경우 모든 상품이 선택되지 않았다`() {
        // given
        setUpViewModel(savedProducts = products(5), savedCartItems = cartItems(3))
        viewModel.loadAllCartItems()

        // when
        viewModel.selectCartItem(productId = 0, isSelected = true)

        // then
        val actual = viewModel.cartItemAllSelected.getOrAwaitValue()
        assertThat(actual).isFalse
    }

    @Test
    fun `장바구니에 상품이 3개가 있고 3개를 선택한 경우 모든 상품이 선택되었다`(): Unit =
        runBlocking {
            // given
            setUpViewModel(savedProducts = products(5), savedCartItems = cartItems(3))
            viewModel.loadAllCartItems()

            // when
            viewModel.selectCartItem(productId = 0, isSelected = true)
            viewModel.selectCartItem(productId = 1, isSelected = true)
            viewModel.selectCartItem(productId = 2, isSelected = true)

            // then
            val actual = viewModel.cartItemAllSelected.getOrAwaitValue()
            assertThat(actual).isTrue
        }

    @Test
    fun `하나 이상의 상품을 선택한 경우 주문할 수 있다`(): Unit =
        runBlocking {
            // given
            setUpViewModel(savedProducts = products(5), savedCartItems = cartItems(3))
            viewModel.loadAllCartItems()

            // when
            viewModel.selectCartItem(productId = 0, isSelected = true)

            // then
            val actual = viewModel.isEnabledOrder.getOrAwaitValue()
            assertThat(actual).isTrue
        }

    @Test
    fun `상품을 하나도 선택하지 않은 경우 주문할 수 없다`(): Unit =
        runBlocking {
            // given
            setUpViewModel(savedProducts = products(5), savedCartItems = cartItems(3))
            viewModel.loadAllCartItems()

            // when

            // then
            val actual = viewModel.isEnabledOrder.getOrAwaitValue()
            assertThat(actual).isFalse
        }

    @Test
    fun `1500원 상품 2개와 3000원 상품 1개를 선택하면 총 가격이 6000원이다`(): Unit =
        runBlocking {
            // given
            val products = listOf(product(0).copy(price = 1500), product(1).copy(price = 3000))
            val cartItems = listOf(cartItem(0, quantity = 2), cartItem(1, quantity = 1))
            setUpViewModel(savedProducts = products, savedCartItems = cartItems)
            viewModel.loadAllCartItems()

            // when
            viewModel.selectCartItem(productId = 0, isSelected = true)
            viewModel.selectCartItem(productId = 1, isSelected = true)

            // then
            val actual = viewModel.totalPrice.getOrAwaitValue()
            assertThat(actual).isEqualTo(6000)
        }

    @Test
    fun `1500원 상품 2개와 3000원 상품 1개를 선택하면 총 개수가 3개다`(): Unit =
        runBlocking {
            // given
            val products = listOf(product(0).copy(price = 1500), product(1).copy(price = 3000))
            val cartItems = listOf(cartItem(0, quantity = 2), cartItem(1, quantity = 1))
            setUpViewModel(savedProducts = products, savedCartItems = cartItems)
            viewModel.loadAllCartItems()

            // when
            viewModel.selectCartItem(productId = 0, isSelected = true)
            viewModel.selectCartItem(productId = 1, isSelected = true)

            // then
            val actual = viewModel.totalQuantity.getOrAwaitValue()
            assertThat(actual).isEqualTo(3)
        }

    private fun setUpViewModel(
        savedProducts: List<Product>,
        savedRecentProducts: List<RecentProduct> = emptyList(),
        savedCartItems: List<CartItem> = emptyList(),
    ) {
        productRepository = FakeProductRepository(savedProducts)
        recentProductRepository = FakeRecentProductRepository(savedRecentProducts)
        cartRepository = FakeCartRepository(savedCartItems)

        viewModel =
            CartViewModel(
                productRepository,
                recentProductRepository,
                cartRepository,
            )
    }
}
