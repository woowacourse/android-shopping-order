package woowacourse.shopping.presentation.ui.productlist

import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.remote.api.DummyData.CARTS
import woowacourse.shopping.remote.api.DummyData.PRODUCTS
import woowacourse.shopping.remote.api.DummyData.PRODUCT_LIST

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class ProductListViewModelTest {
    private lateinit var viewModel: ProductListViewModel

    @MockK
    private lateinit var shoppingCartRepository: ShoppingCartRepository

    @MockK
    private lateinit var productRepository: ProductRepository

    @MockK
    private lateinit var productHistoryRepository: ProductHistoryRepository

    @BeforeEach
    fun setUp() {
        coEvery { productRepository.getPagingProduct(0, 20) } returns
            Result.success(PRODUCTS.copy(content = PRODUCTS.content.subList(0, 20)))
        coEvery { productRepository.getPagingProduct(1, 20) } returns
            Result.success(PRODUCTS.copy(content = PRODUCTS.content.subList(20, 40)))
        coEvery { productHistoryRepository.getProductHistoriesBySize(any()) } returns
            Result.success(emptyList())
        coEvery { shoppingCartRepository.getAllCarts() } returns Result.success(CARTS)
        coEvery { shoppingCartRepository.getCartProductsQuantity() } returns Result.success(0)

        viewModel =
            ProductListViewModel(
                productRepository,
                shoppingCartRepository,
                productHistoryRepository,
            )
    }

    @Test
    fun `상품을 불러온다`() {
        // when
        val actual = viewModel.uiState.getOrAwaitValue()

        // then
        assertThat(actual.pagingCart.cartList).isEqualTo(
            PRODUCTS.content.subList(0, 20).map { Cart(product = it) },
        )
    }

    @Test
    fun `더보기 버튼을 눌렀을 때 상품을 더 불러온다`() {
        // when
        viewModel.loadMoreProducts()

        // then
        val actual = viewModel.uiState.getOrAwaitValue().pagingCart.cartList
        assertThat(actual).isEqualTo(PRODUCT_LIST.subList(0, 40).map { Cart(product = it) })
    }

    @Test
    fun `상품을 누르면 상품의 상세 소개 화면으로 넘어가는 이벤트를 발생시킨다`() {
        // given
        val productList = PRODUCT_LIST.subList(0, 20)

        // when
        viewModel.navigateToProductDetail(productList.first().id)

        // then
        val actual = viewModel.navigateAction.getOrAwaitValue()
        val expected = ProductListNavigateAction.NavigateToProductDetail(productList.first().id)
        assertThat(actual.value).isEqualTo(expected)
    }

    @Test
    fun `장바구니를 누르면 장바구니로 넘어가는 이벤트를 발생시킨다`() {
        // when
        viewModel.navigateToShoppingCart()

        // then
        val actual = viewModel.navigateAction.getOrAwaitValue()
        val expected = ProductListNavigateAction.NavigateToShoppingCart
        assertThat(actual.value).isEqualTo(expected)
    }

    @Test
    fun `상품을 장바구니에 추가한다`() {
        // given
        val productList = PRODUCT_LIST.subList(0, 20)
        val cartItemId = 1

        coEvery {
            shoppingCartRepository.insertCartProduct(productList.first().id, 1)
        } returns Result.success(cartItemId)

        // when
        viewModel.plusProductQuantity(productList.first().id, 0)

        // then
        val actual = viewModel.uiState.getOrAwaitValue()
        assertThat(actual.pagingCart.cartList.first().product.id).isEqualTo(productList.first().id)
    }

    @Test
    fun `상품을 장바구니에서 제거한다`() {
        // given
        val productList = PRODUCT_LIST.subList(0, 20)
        val cartItemId = 1

        coEvery {
            shoppingCartRepository.insertCartProduct(productList.first().id, 1)
        } returns Result.success(cartItemId)
        coEvery {
            shoppingCartRepository.deleteCartProductById(cartItemId)
        } returns Result.success(Unit)

        // when
        viewModel.plusProductQuantity(productList.first().id, 0)
        viewModel.minusProductQuantity(productList.first().id, 0)

        // then
        val actual = viewModel.uiState.getOrAwaitValue()
        assertThat(actual.pagingCart.cartList.first().id).isEqualTo(Cart.EMPTY_CART_ID)
    }
}
