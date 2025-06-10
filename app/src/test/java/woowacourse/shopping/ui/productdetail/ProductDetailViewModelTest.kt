package woowacourse.shopping.ui.productdetail

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.usecase.AddSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductUseCase
import woowacourse.shopping.domain.usecase.GetRecentSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.UpdateCartProductUseCase
import woowacourse.shopping.model.DUMMY_HISTORY_PRODUCT_1
import woowacourse.shopping.model.DUMMY_PRODUCT_1
import woowacourse.shopping.ui.model.ProductDetailUiModel
import woowacourse.shopping.util.CoroutinesTestExtension
import woowacourse.shopping.util.InstantTaskExecutorExtension
import woowacourse.shopping.util.getOrAwaitValue
import woowacourse.shopping.util.setUpTestLiveData

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
class ProductDetailViewModelTest {
    private lateinit var getCatalogProductUseCase: GetCatalogProductUseCase
    private lateinit var getRecentSearchHistoryUseCase: GetRecentSearchHistoryUseCase
    private lateinit var addSearchHistoryUseCase: AddSearchHistoryUseCase
    private lateinit var updateCartProductUseCase: UpdateCartProductUseCase

    private lateinit var viewModel: ProductDetailViewModel

    @BeforeEach
    fun setup() {
        getCatalogProductUseCase = mockk()
        getRecentSearchHistoryUseCase = mockk()
        addSearchHistoryUseCase = mockk(relaxed = true)
        updateCartProductUseCase = mockk()

        viewModel =
            ProductDetailViewModel(
                getCatalogProductUseCase,
                getRecentSearchHistoryUseCase,
                addSearchHistoryUseCase,
                updateCartProductUseCase,
            )
    }

    @Test
    fun `상품 상세 정보를 불러온다`() =
        runTest {
            // given
            val expected = DUMMY_PRODUCT_1
            coEvery { getCatalogProductUseCase(expected.productDetail.id) } returns expected

            // when
            viewModel.loadProductDetail(expected.productDetail.id)
            advanceUntilIdle()

            // then
            val state = viewModel.uiModel.getOrAwaitValue()
            assertThat(state.product).isEqualTo(expected)
            assertThat(state.connectionErrorMessage).isNull()
        }

    @Test
    fun `최근 탐색한 상품을 불러온다`() =
        runTest {
            // given
            val expected = DUMMY_HISTORY_PRODUCT_1
            coEvery { getRecentSearchHistoryUseCase() } returns expected

            // when
            viewModel.loadLastHistoryProduct()
            advanceUntilIdle()

            // then
            val state = viewModel.uiModel.getOrAwaitValue()
            assertThat(state.lastHistoryProduct).isEqualTo(expected)
        }

    @Test
    fun `장바구니 수량을 증가시키면 상품 수량이 1 증가한다`() {
        // given
        val original = DUMMY_PRODUCT_1
        setUpTestLiveData(ProductDetailUiModel(product = original), "_uiModel", viewModel)

        // when
        viewModel.increaseCartProductQuantity()

        // then
        val state = viewModel.uiModel.getOrAwaitValue()
        assertThat(state.product.quantity).isEqualTo(original.quantity + 1)
    }

    @Test
    fun `장바구니 수량을 감소시키면 상품 수량이 1 감소한다`() {
        // given
        val original = DUMMY_PRODUCT_1
        setUpTestLiveData(ProductDetailUiModel(product = original), "_uiModel", viewModel)

        // when
        viewModel.decreaseCartProductQuantity()

        // then
        val state = viewModel.uiModel.getOrAwaitValue()
        assertThat(state.product.quantity).isEqualTo(original.quantity - 1)
    }

    @Test
    fun `장바구니 수량 업데이트 성공 여부를 State에 반영한다`() =
        runTest {
            // given
            val product = DUMMY_PRODUCT_1
            setUpTestLiveData(ProductDetailUiModel(product = product), "_uiModel", viewModel)

            coEvery {
                updateCartProductUseCase(productId = product.productDetail.id, cartId = product.cartId, quantity = product.quantity)
            } returns Unit

            // when
            viewModel.updateCartProduct()
            advanceUntilIdle()

            // then
            val state = viewModel.uiModel.getOrAwaitValue()
            assertThat(state.isCartProductUpdateSuccess).isTrue()
        }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }
}
