package woowacourse.shopping.ui.catalog

import android.util.Log
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.usecase.DecreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.GetCartProductsQuantityUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductsByProductIdsUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductsUseCase
import woowacourse.shopping.domain.usecase.GetSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductQuantityUseCase
import woowacourse.shopping.model.DUMMY_HISTORY_PRODUCT_1
import woowacourse.shopping.model.DUMMY_PRODUCTS_1
import woowacourse.shopping.model.DUMMY_PRODUCTS_2
import woowacourse.shopping.model.DUMMY_PRODUCTS_3
import woowacourse.shopping.model.DUMMY_PRODUCT_1
import woowacourse.shopping.ui.model.CatalogUiState
import woowacourse.shopping.util.CoroutinesTestExtension
import woowacourse.shopping.util.InstantTaskExecutorExtension
import woowacourse.shopping.util.getOrAwaitValue
import woowacourse.shopping.util.setUpTestLiveData

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
class CatalogViewModelTest {
    private lateinit var getCatalogProductsUseCase: GetCatalogProductsUseCase
    private lateinit var getCatalogProductUseCase: GetCatalogProductUseCase
    private lateinit var getCatalogProductsByProductIdsUseCase: GetCatalogProductsByProductIdsUseCase
    private lateinit var getSearchHistoryUseCase: GetSearchHistoryUseCase
    private lateinit var increaseCartProductQuantityUseCase: IncreaseCartProductQuantityUseCase
    private lateinit var decreaseCartProductQuantityUseCase: DecreaseCartProductQuantityUseCase
    private lateinit var getCartProductsQuantityUseCase: GetCartProductsQuantityUseCase

    private lateinit var viewModel: CatalogViewModel

    @BeforeEach
    fun setup() {
        getCatalogProductsUseCase = mockk()
        getCatalogProductUseCase = mockk()
        getCatalogProductsByProductIdsUseCase = mockk()
        getSearchHistoryUseCase = mockk()
        increaseCartProductQuantityUseCase = mockk()
        decreaseCartProductQuantityUseCase = mockk()
        getCartProductsQuantityUseCase = mockk()

        coEvery { getCatalogProductsUseCase(any(), any()) } returns Result.success(DUMMY_PRODUCTS_1)

        viewModel =
            CatalogViewModel(
                getCatalogProductsUseCase,
                getCatalogProductUseCase,
                getCatalogProductsByProductIdsUseCase,
                getSearchHistoryUseCase,
                increaseCartProductQuantityUseCase,
                decreaseCartProductQuantityUseCase,
                getCartProductsQuantityUseCase,
            )

        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
    }

    @Test
    fun `ViewModel이 초기화되면 상품 목록을 불러온다`() {
        val state = viewModel.uiState.getOrAwaitValue()
        assertThat(state.catalogProducts.products).containsExactlyElementsIn(DUMMY_PRODUCTS_1.products)
        assertThat(state.isProductsLoading).isFalse()
    }

    @Test
    fun `상품을 더 불러오면 페이지가 증가하고 기존 목록에 추가된다`() =
        runTest {
            coEvery { getCatalogProductsUseCase(any(), any()) } returns Result.success(DUMMY_PRODUCTS_3)

            setUpTestLiveData(
                CatalogUiState(catalogProducts = DUMMY_PRODUCTS_1),
                "_uiState",
                viewModel,
            )

            viewModel.loadMoreCatalogProducts()

            val state = viewModel.uiState.getOrAwaitValue()
            assertThat(state.catalogProducts.products).containsExactlyElementsIn(
                DUMMY_PRODUCTS_1.products + DUMMY_PRODUCTS_3.products,
            )
        }

    @Test
    fun `장바구니 수량을 증가시키면 상품 수량에 반영된다`() =
        runTest {
            val updatedProduct = DUMMY_PRODUCT_1.copy(quantity = 10)

            coEvery { increaseCartProductQuantityUseCase(any()) } returns Result.success(10)
            coEvery { getCatalogProductUseCase(DUMMY_PRODUCT_1.productDetail.id) } returns Result.success(updatedProduct)

            setUpTestLiveData(
                CatalogUiState(catalogProducts = DUMMY_PRODUCTS_2),
                "_uiState",
                viewModel,
            )

            viewModel.increaseCartProduct(DUMMY_PRODUCT_1.productDetail.id)

            val result = viewModel.uiState.getOrAwaitValue()
            assertThat(result.catalogProducts.getProductByProductId(updatedProduct.productDetail.id)?.quantity).isEqualTo(10)
        }

    @Test
    fun `장바구니 수량을 감소시키면 상품 수량에 반영된다`() =
        runTest {
            val updatedProduct = DUMMY_PRODUCT_1.copy(quantity = 3)

            coEvery { decreaseCartProductQuantityUseCase(any()) } returns Result.success(3)
            coEvery { getCatalogProductUseCase(DUMMY_PRODUCT_1.productDetail.id) } returns Result.success(updatedProduct)

            setUpTestLiveData(
                CatalogUiState(catalogProducts = DUMMY_PRODUCTS_2),
                "_uiState",
                viewModel,
            )

            viewModel.decreaseCartProduct(DUMMY_PRODUCT_1.productDetail.id)

            val result = viewModel.uiState.getOrAwaitValue()
            assertThat(result.catalogProducts.getProductByProductId(updatedProduct.productDetail.id)?.quantity).isEqualTo(3)
        }

    @Test
    fun `특정 상품 정보를 불러오면 상품 목록에 반영된다`() =
        runTest {
            val updatedProduct = DUMMY_PRODUCT_1.copy(quantity = 1234)

            coEvery { getCatalogProductUseCase(DUMMY_PRODUCT_1.productDetail.id) } returns Result.success(updatedProduct)

            setUpTestLiveData(
                CatalogUiState(catalogProducts = DUMMY_PRODUCTS_1),
                "_uiState",
                viewModel,
            )

            viewModel.loadCartProduct(DUMMY_PRODUCT_1.productDetail.id)

            val result = viewModel.uiState.getOrAwaitValue()
            assertThat(result.catalogProducts.getProductByProductId(updatedProduct.productDetail.id)?.quantity).isEqualTo(1234)
        }

    @Test
    fun `여러 상품 정보를 불러오면 상품 목록에 반영된다`() =
        runTest {
            val updated = listOf(DUMMY_PRODUCT_1.copy(quantity = 1234))

            coEvery { getCatalogProductsByProductIdsUseCase(any()) } returns Result.success(updated)

            setUpTestLiveData(
                CatalogUiState(catalogProducts = DUMMY_PRODUCTS_2),
                "_uiState",
                viewModel,
            )

            viewModel.loadCartProductsByProductIds(listOf(DUMMY_PRODUCT_1.productDetail.id))

            val result = viewModel.uiState.getOrAwaitValue()
            assertThat(result.catalogProducts.products).containsExactlyElementsIn(updated)
        }

    @Test
    fun `최근 검색 상품 목록을 불러온다`() =
        runTest {
            coEvery { getSearchHistoryUseCase() } returns Result.success(listOf(DUMMY_HISTORY_PRODUCT_1))

            viewModel.loadHistoryProducts()

            val result = viewModel.uiState.getOrAwaitValue()
            assertThat(result.historyProducts).containsExactly(DUMMY_HISTORY_PRODUCT_1)
        }

    @Test
    fun `장바구니 상품 수량을 불러와서 상품 목록에 반영한다`() =
        runTest {
            coEvery { getCartProductsQuantityUseCase() } returns Result.success(10)

            viewModel.loadCartProductsQuantity()

            val result = viewModel.uiState.getOrAwaitValue()
            assertThat(result.cartProductsQuantity).isEqualTo(10)
        }

    @Test
    fun `상품 목록 로딩 실패 시 에러 메시지를 반환한다`() =
        runTest {
            val exception = Throwable("ERROR")
            coEvery { getCatalogProductsUseCase(any(), any()) } returns Result.failure(exception)

            viewModel.loadMoreCatalogProducts()

            val state = viewModel.uiState.getOrAwaitValue()
            assertThat(state.connectionErrorMessage).contains("ERROR")
        }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }
}
