package woowacourse.shopping.cart

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.repository.CartProductRepository
import woowacourse.shopping.data.repository.CatalogProductRepository
import woowacourse.shopping.data.repository.RecentlyViewedProductRepository
import woowacourse.shopping.product.catalog.ProductUiModel
import woowacourse.shopping.util.CoroutinesTestExtension
import woowacourse.shopping.util.InstantTaskExecutorExtension
import woowacourse.shopping.util.getOrAwaitValue

@ExperimentalCoroutinesApi
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
class CartViewModelTest {
    private lateinit var cartProductRepository: CartProductRepository
    private lateinit var catalogProductRepository: CatalogProductRepository
    private lateinit var recentlyViewedProductRepository: RecentlyViewedProductRepository

    private lateinit var viewModel: CartViewModel

    @BeforeEach
    fun setUp() {
        cartProductRepository = mockk(relaxed = true)
        catalogProductRepository = mockk(relaxed = true)
        recentlyViewedProductRepository = mockk(relaxed = true)

        viewModel =
            CartViewModel(
                cartProductRepository,
                catalogProductRepository,
                recentlyViewedProductRepository,
            )
    }

    @Test
    fun `addProduct - 장바구니에 상품을 추가하면 updatedProduct와 cartProducts가 갱신된다`() =
        runTest {
            // given
            val dummyProduct =
                ProductUiModel(id = 1L, name = "Test", price = 1000, imageUrl = "url")
            coEvery { cartProductRepository.insertCartProduct(dummyProduct.id, any()) } returns 99L
            coEvery { cartProductRepository.getTotalElements() } returns 1L
            coEvery { cartProductRepository.getCartProductsInRange(any(), any()) } returns
                listOf(
                    dummyProduct.copy(cartItemId = 99L),
                )

            // when
            viewModel.addProduct(dummyProduct)

            // then
            val updated = viewModel.updatedProduct.getOrAwaitValue()
            assertThat(1L).isEqualTo(updated.id)
            assertThat(1).isEqualTo(updated.quantity)
            assertThat(99L).isEqualTo(updated.cartItemId)

            val cart = viewModel.cartProducts.getOrAwaitValue()
            assertThat(cart.any { it.productItem.id == 1L }).isTrue()
        }

    @Test
    fun `updateQuantity - 수량을 증가시키면 updatedProduct가 변경되고 cartProducts가 갱신된다`() =
        runTest {
            // given
            val initialProduct =
                ProductUiModel(
                    id = 1L,
                    name = "Test",
                    price = 1000,
                    quantity = 1,
                    cartItemId = 10L,
                    imageUrl = "url",
                )
            coEvery { cartProductRepository.updateProduct(10L, 2) } returns true
            coEvery { cartProductRepository.getTotalElements() } returns 1L
            coEvery { cartProductRepository.getCartProductsInRange(any(), any()) } returns
                listOf(
                    initialProduct.copy(quantity = 2),
                )

            // when
            viewModel.updateQuantity(ButtonEvent.INCREASE, initialProduct)

            // then
            val updated = viewModel.updatedProduct.getOrAwaitValue()
            assertThat(2).isEqualTo(updated.quantity)
            assertThat(initialProduct.id).isEqualTo(updated.id)
        }

    @Test
    fun `deleteCartProduct - 삭제 요청이 들어오면 repository를 호출하고 cartProducts를 갱신한다`() =
        runTest {
            // given
            coEvery { cartProductRepository.deleteCartProduct(10L) } returns true
            coEvery { cartProductRepository.getTotalElements() } returns 0L
            coEvery {
                cartProductRepository.getCartProductsInRange(
                    any(),
                    any(),
                )
            } returns emptyList()

            // when
            viewModel.deleteCartProduct(10L)

            // then
            coVerify { cartProductRepository.deleteCartProduct(10L) }
            val cart = viewModel.cartProducts.getOrAwaitValue()
            assertThat(cart.isEmpty()).isTrue()
        }

    @Test
    fun `loadRecommendProducts - 최근 본 상품이 있고, 추천 상품이 로드되면 recommendedProducts가 업데이트된다`() =
        runTest {
            // given
            val viewedProduct =
                ProductUiModel(
                    id = 1L,
                    name = "최근본",
                    price = 1000,
                    imageUrl = "url",
                    category = "음료",
                )
            val recommended =
                listOf(
                    ProductUiModel(id = 2L, name = "추천1", price = 1500, imageUrl = "url"),
                    ProductUiModel(id = 3L, name = "추천2", price = 2000, imageUrl = "url"),
                )
            coEvery { recentlyViewedProductRepository.getLatestViewedProduct() } returns viewedProduct
            coEvery { catalogProductRepository.getProduct(1L) } returns viewedProduct
            coEvery {
                catalogProductRepository.getRecommendedProducts(
                    "음료",
                    0,
                    10,
                )
            } returns recommended

            // when
            viewModel.loadRecommendProducts()

            // then
            val recommendResult = viewModel.recommendedProducts.getOrAwaitValue()
            assertThat(2).isEqualTo(recommendResult.size)
            assertThat(2L).isEqualTo(recommendResult[0].id)
        }
}
