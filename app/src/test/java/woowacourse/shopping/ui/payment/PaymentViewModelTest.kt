package woowacourse.shopping.ui.payment

import android.util.Log
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.usecase.GetCatalogProductsByProductIdsUseCase
import woowacourse.shopping.domain.usecase.GetCouponsUseCase
import woowacourse.shopping.domain.usecase.OrderProductsUseCase
import woowacourse.shopping.model.DUMMY_COUPONS_1
import woowacourse.shopping.model.DUMMY_LOCAL_DATE_TIME_1
import woowacourse.shopping.model.DUMMY_PRODUCTS_1
import woowacourse.shopping.model.DUMMY_PRODUCTS_3
import woowacourse.shopping.util.CoroutinesTestExtension
import woowacourse.shopping.util.InstantTaskExecutorExtension
import woowacourse.shopping.util.getOrAwaitValue

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
class PaymentViewModelTest {
    private lateinit var getCatalogProductsByProductIdsUseCase: GetCatalogProductsByProductIdsUseCase
    private lateinit var getCouponsUseCase: GetCouponsUseCase
    private lateinit var orderProductsUseCase: OrderProductsUseCase

    private lateinit var viewModel: PaymentViewModel

    @BeforeEach
    fun setUp() {
        getCatalogProductsByProductIdsUseCase = mockk()
        getCouponsUseCase = mockk()
        orderProductsUseCase = mockk()

        viewModel =
            PaymentViewModel(
                getCatalogProductsByProductIdsUseCase,
                getCouponsUseCase,
                orderProductsUseCase,
            )

        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
    }

    @Test
    fun `상품 ID 목록을 전달하면 해당 상품들과 쿠폰을 불러온다`() =
        runTest {
            coEvery { getCatalogProductsByProductIdsUseCase(any()) } returns DUMMY_PRODUCTS_3.products
            coEvery { getCouponsUseCase() } returns DUMMY_COUPONS_1

            viewModel.loadProducts(DUMMY_PRODUCTS_3.products.map { it.productDetail.id })
            advanceUntilIdle()

            viewModel.loadCoupons(DUMMY_PRODUCTS_3, DUMMY_LOCAL_DATE_TIME_1)
            advanceUntilIdle()

            val state = viewModel.uiModel.getOrAwaitValue()

            val expectedProducts = DUMMY_PRODUCTS_3.products.map { it.copy(isSelected = true) }

            assertThat(state.products.products).containsExactlyElementsIn(expectedProducts)
            assertThat(state.coupons.value).containsExactlyElementsIn(DUMMY_COUPONS_1.value)
            assertThat(state.connectionErrorMessage).isNull()
        }

    @Test
    fun `쿠폰을 선택하면 선택된 쿠폰만 참이 된다`() =
        runTest {
            coEvery { getCatalogProductsByProductIdsUseCase(any()) } returns DUMMY_PRODUCTS_3.products
            coEvery { getCouponsUseCase() } returns DUMMY_COUPONS_1

            viewModel.loadProducts(DUMMY_PRODUCTS_3.products.map { it.productDetail.id })
            advanceUntilIdle()

            viewModel.loadCoupons(DUMMY_PRODUCTS_3, DUMMY_LOCAL_DATE_TIME_1)
            advanceUntilIdle()

            viewModel.selectCoupon(
                DUMMY_COUPONS_1.value
                    .first()
                    .detail.id,
                DUMMY_LOCAL_DATE_TIME_1,
            )

            val state = viewModel.uiModel.getOrAwaitValue()
            val selected = state.coupons.value.filter { it.isSelected }
            assertThat(selected).hasSize(1)
            assertThat(state.price.result).isGreaterThan(0)
        }

    @Test
    fun `주문 성공 시 isOrderSuccess가 참이다`() =
        runTest {
            coEvery { getCatalogProductsByProductIdsUseCase(any()) } returns DUMMY_PRODUCTS_1.products
            coEvery { getCouponsUseCase() } returns DUMMY_COUPONS_1
            coEvery { orderProductsUseCase(any()) } returns Unit

            viewModel.loadProducts(DUMMY_PRODUCTS_1.products.map { it.productDetail.id })
            advanceUntilIdle()

            viewModel.orderProducts()
            advanceUntilIdle()

            val state = viewModel.uiModel.getOrAwaitValue()
            assertThat(state.isOrderSuccess).isTrue()
        }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }
}
