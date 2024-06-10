package woowacourse.shopping.presentation.ui.productlist

import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.domain.model.CartItemId
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.remote.api.DummyData.CARTS
import woowacourse.shopping.remote.api.DummyData.PRODUCTS
import woowacourse.shopping.remote.api.DummyData.PRODUCT_LIST

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class, MockKExtension::class)
class ProductListViewModelTest {
    private lateinit var viewModel: ProductListViewModel

    @MockK
    private lateinit var shoppingCartRepository: ShoppingCartRepository

    @MockK
    private lateinit var productRepository: ProductRepository

    @MockK
    private lateinit var productHistoryRepository: ProductHistoryRepository

    @BeforeEach
    fun setUp() =
        runTest {
            coEvery { productRepository.getPagingProduct(0, 20) } returns
                Result.success(PRODUCTS.copy(content = PRODUCTS.content.subList(0, 20)))
            coEvery { productRepository.getPagingProduct(1, 20) } returns
                Result.success(PRODUCTS.copy(content = PRODUCTS.content.subList(20, 40)))

            coEvery { shoppingCartRepository.getAllCarts() } returns Result.success(CARTS)
            coEvery { productHistoryRepository.getProductHistory(any()) } returns
                Result.success(
                    emptyList(),
                )
            coEvery { shoppingCartRepository.getCartItemsCount() } returns Result.success(0)

            viewModel =
                ProductListViewModel(
                    productRepository,
                    shoppingCartRepository,
                    productHistoryRepository,
                )
        }

    @Test
    fun `상품을 불러온다`() =
        runTest {
            // then
            val actual = viewModel.uiState.getOrAwaitValue()

            assertThat(actual.pagingProduct.products).isEqualTo(
                PRODUCTS.content.subList(0, 20),
            )
        }

    @Test
    fun `더보기 버튼을 눌렀을 때 상품을 더 불러온다`() =
        runTest {
            // when
            viewModel.loadMoreProducts()

            // then
            val actual = viewModel.uiState.getOrAwaitValue()
            assertThat(actual.pagingProduct.products).isEqualTo(
                PRODUCTS.content.subList(0, 40),
            )
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
    fun `상품을 장바구니에 추가한다`() =
        runTest {
            // given
            val productList = PRODUCT_LIST.subList(0, 20)
            val cartItemId = CartItemId(1)

            coEvery {
                shoppingCartRepository.postCartItem(
                    productList.first().id,
                    1,
                )
            } returns Result.success(cartItemId)

            // when
            viewModel.plusProductQuantity(productList.first().id)

            // then
            val actual = viewModel.uiState.getOrAwaitValue()
            assertThat(actual.pagingProduct.products.first().id).isEqualTo(productList.first().id)
        }
}
