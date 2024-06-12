package woowacourse.shopping.presentation.ui.productdetail

import androidx.lifecycle.SavedStateHandle
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.CartItemId
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.remote.api.DummyData.STUB_CART_A

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class, MockKExtension::class)
class ProductDetailViewModelTest {
    private lateinit var viewModel: ProductDetailViewModel

    private lateinit var savedStateHandle: SavedStateHandle

    @MockK
    private lateinit var shoppingCartRepository: ShoppingCartRepository

    @MockK
    private lateinit var productRepository: ProductRepository

    @MockK
    private lateinit var productHistoryRepository: ProductHistoryRepository

    private val productId = 1L

    @BeforeEach
    fun setUp() =
        runTest {
            coEvery { productRepository.findCartByProductId(productId) } returns
                Result.success(STUB_CART_A.toDomain())
            coEvery { productHistoryRepository.getProductHistory(any()) } returns
                Result.success(
                    emptyList(),
                )

            val initialState = mapOf(ProductDetailActivity.PUT_EXTRA_PRODUCT_ID to productId)
            savedStateHandle = SavedStateHandle(initialState)
            viewModel =
                ProductDetailViewModel(
                    savedStateHandle,
                    productRepository,
                    shoppingCartRepository,
                    productHistoryRepository,
                )
        }

    @Test
    fun `선택한 상품의 상세 정보를 불러온다`() {
        // then
        val actual = viewModel.uiState.getOrAwaitValue()
        assertThat(actual.cart).isEqualTo(STUB_CART_A.toDomain())
    }

    @Test
    fun `선택한 상품을 장바구니에 추가한다`() {
        // given
        val productIdSlot = CapturingSlot<Long>()
        val quantitySlot = CapturingSlot<Int>()

        coEvery {
            productHistoryRepository.insertProductHistory(
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } returns Result.success(Unit)

        coEvery {
            shoppingCartRepository.postCartItem(
                capture(productIdSlot),
                capture(quantitySlot),
            )
        } returns Result.success(CartItemId(id = 1))

        // when
        viewModel.addToCart()

        // then
        assertAll(
            { assertThat(productIdSlot.captured).isEqualTo(STUB_CART_A.product.id) },
            { assertThat(quantitySlot.captured).isEqualTo(STUB_CART_A.quantity) },
        )
    }
}
