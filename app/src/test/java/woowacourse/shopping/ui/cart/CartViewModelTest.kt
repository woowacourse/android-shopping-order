package woowacourse.shopping.ui.cart

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
import woowacourse.shopping.domain.usecase.DecreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.GetCartProductsUseCase
import woowacourse.shopping.domain.usecase.GetCartRecommendProductsUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.RemoveCartProductUseCase
import woowacourse.shopping.model.DUMMY_PRODUCTS_1
import woowacourse.shopping.model.DUMMY_PRODUCT_1
import woowacourse.shopping.ui.model.CartProductUiModel
import woowacourse.shopping.util.CoroutinesTestExtension
import woowacourse.shopping.util.InstantTaskExecutorExtension
import woowacourse.shopping.util.getOrAwaitValue
import woowacourse.shopping.util.setUpTestLiveData

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
class CartViewModelTest {
    private lateinit var getCartProductsUseCase: GetCartProductsUseCase
    private lateinit var removeCartProductUseCase: RemoveCartProductUseCase
    private lateinit var increaseCartProductQuantityUseCase: IncreaseCartProductQuantityUseCase
    private lateinit var decreaseCartProductQuantityUseCase: DecreaseCartProductQuantityUseCase
    private lateinit var getCartRecommendProductsUseCase: GetCartRecommendProductsUseCase
    private lateinit var getCatalogProductUseCase: GetCatalogProductUseCase

    private lateinit var viewModel: CartViewModel

    @BeforeEach
    fun setUp() {
        getCartProductsUseCase = mockk()
        removeCartProductUseCase = mockk()
        increaseCartProductQuantityUseCase = mockk()
        decreaseCartProductQuantityUseCase = mockk()
        getCartRecommendProductsUseCase = mockk()
        getCatalogProductUseCase = mockk()

        coEvery { getCartProductsUseCase(any(), any()) } returns DUMMY_PRODUCTS_1

        viewModel =
            CartViewModel(
                getCartProductsUseCase,
                removeCartProductUseCase,
                increaseCartProductQuantityUseCase,
                decreaseCartProductQuantityUseCase,
                getCartRecommendProductsUseCase,
                getCatalogProductUseCase,
            )
    }

    @Test
    fun `초기 로딩 시 장바구니 상품 목록을 불러온다`() {
        val state = viewModel.uiModel.getOrAwaitValue()
        assertThat(state.cartProducts.products).containsExactlyElementsIn(DUMMY_PRODUCTS_1.products)
    }

    @Test
    fun `장바구니 상품을 제거하면 UI 상태에서 제거되고 상품 목록을 다시 불러온다`() =
        runTest {
            coEvery { removeCartProductUseCase(DUMMY_PRODUCT_1.cartId!!) } returns Unit
            coEvery { getCartProductsUseCase(any(), any()) } returns DUMMY_PRODUCTS_1

            setUpTestLiveData(CartProductUiModel(cartProducts = DUMMY_PRODUCTS_1), "_uiModel", viewModel)

            viewModel.removeCartProduct(DUMMY_PRODUCT_1.cartId!!, DUMMY_PRODUCT_1.productDetail.id)
            advanceUntilIdle()

            val state = viewModel.uiModel.getOrAwaitValue()
            assertThat(state.editedProductIds).contains(DUMMY_PRODUCT_1.productDetail.id)
        }

    @Test
    fun `장바구니 상품 수량을 증가시키면 변경된 수량이 UI에 반영된다`() =
        runTest {
            coEvery { increaseCartProductQuantityUseCase(any()) } returns 10

            setUpTestLiveData(CartProductUiModel(cartProducts = DUMMY_PRODUCTS_1), "_uiModel", viewModel)

            viewModel.increaseCartProductQuantity(DUMMY_PRODUCT_1.productDetail.id)
            advanceUntilIdle()

            val state = viewModel.uiModel.getOrAwaitValue()
            assertThat(state.cartProducts.getProductByProductId(DUMMY_PRODUCT_1.productDetail.id)?.quantity).isEqualTo(10)
        }

    @Test
    fun `장바구니 상품 수량을 감소시키면 변경된 수량이 UI에 반영된다`() =
        runTest {
            coEvery { decreaseCartProductQuantityUseCase(any()) } returns 3

            setUpTestLiveData(CartProductUiModel(cartProducts = DUMMY_PRODUCTS_1), "_uiModel", viewModel)

            viewModel.decreaseCartProductQuantity(DUMMY_PRODUCT_1.productDetail.id)
            advanceUntilIdle()

            val state = viewModel.uiModel.getOrAwaitValue()
            assertThat(state.cartProducts.getProductByProductId(DUMMY_PRODUCT_1.productDetail.id)?.quantity).isEqualTo(3)
        }

    @Test
    fun `장바구니 상품의 선택 상태를 토글하면 상태가 반전된다`() {
        setUpTestLiveData(CartProductUiModel(cartProducts = DUMMY_PRODUCTS_1), "_uiModel", viewModel)

        val before =
            viewModel.uiModel
                .getOrAwaitValue()
                .cartProducts
                .getProductByCartId(DUMMY_PRODUCT_1.cartId!!)
                ?.isSelected

        viewModel.toggleCartProductSelection(DUMMY_PRODUCT_1.cartId!!)

        val after =
            viewModel.uiModel
                .getOrAwaitValue()
                .cartProducts
                .getProductByCartId(DUMMY_PRODUCT_1.cartId!!)
                ?.isSelected

        assertThat(before).isNotEqualTo(after)
    }

    @Test
    fun `전체 장바구니 상품의 선택 상태를 토글하면 전체 상태가 반전된다`() {
        setUpTestLiveData(CartProductUiModel(cartProducts = DUMMY_PRODUCTS_1), "_uiModel", viewModel)

        viewModel.toggleAllCartProductsSelection()

        val state = viewModel.uiModel.getOrAwaitValue()
        assertThat(state.cartProducts.products.all { it.isSelected }).isTrue()
    }

    @Test
    fun `추천 상품 수량을 증가시키면 반영된다`() =
        runTest {
            val updatedQuantity = 2
            val updated = DUMMY_PRODUCT_1.copy(quantity = updatedQuantity)

            coEvery { increaseCartProductQuantityUseCase(any()) } returns updatedQuantity

            setUpTestLiveData(CartProductUiModel(recommendedProducts = DUMMY_PRODUCTS_1), "_uiModel", viewModel)

            viewModel.increaseRecommendedProductQuantity(DUMMY_PRODUCT_1.productDetail.id)
            advanceUntilIdle()

            val state = viewModel.uiModel.getOrAwaitValue()
            assertThat(state.recommendedProducts.getProductByProductId(updated.productDetail.id)?.quantity).isEqualTo(updatedQuantity)
        }

    @Test
    fun `추천 상품 수량을 감소시키면 반영된다`() =
        runTest {
            val updatedQuantity = 1
            val updated = DUMMY_PRODUCT_1.copy(quantity = updatedQuantity)

            coEvery { decreaseCartProductQuantityUseCase(any()) } returns updatedQuantity

            setUpTestLiveData(CartProductUiModel(recommendedProducts = DUMMY_PRODUCTS_1), "_uiModel", viewModel)

            viewModel.decreaseRecommendedProductQuantity(DUMMY_PRODUCT_1.productDetail.id)
            advanceUntilIdle()

            val state = viewModel.uiModel.getOrAwaitValue()
            assertThat(state.recommendedProducts.getProductByProductId(updated.productDetail.id)?.quantity).isEqualTo(updatedQuantity)
        }

    @Test
    fun `선택된 장바구니 및 추천 상품 ID를 반환한다`() {
        val modifiedState =
            CartProductUiModel(
                cartProducts = DUMMY_PRODUCTS_1.toggleAllSelection(),
                recommendedProducts = DUMMY_PRODUCTS_1.toggleAllSelection(),
            )

        setUpTestLiveData(modifiedState, "_uiModel", viewModel)

        val selectedIds = viewModel.getSelectedProductIds()
        val expectedIds = (DUMMY_PRODUCTS_1.products + DUMMY_PRODUCTS_1.products).map { it.productDetail.id }.toSet()

        assertThat(selectedIds).isEqualTo(expectedIds)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }
}
