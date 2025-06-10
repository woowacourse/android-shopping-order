package woowacourse.shopping.viewModel.order

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.coupon.repository.CouponRepository
import woowacourse.shopping.data.order.repository.OrderRepository
import woowacourse.shopping.fixture.COUPONS
import woowacourse.shopping.fixture.SHOPPING_CART_PRODUCTS_TO_ORDER
import woowacourse.shopping.view.order.CouponState
import woowacourse.shopping.view.order.OrderEvent
import woowacourse.shopping.view.order.OrderState
import woowacourse.shopping.view.order.OrderViewModel
import woowacourse.shopping.viewModel.common.CoroutinesTestExtension
import woowacourse.shopping.viewModel.common.InstantTaskExecutorExtension
import woowacourse.shopping.viewModel.common.getOrAwaitValue

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
@Suppress("FunctionName")
class OrderViewModelTest {
    private lateinit var viewModel: OrderViewModel
    private val orderRepository: OrderRepository = mockk()
    private val couponRepository: CouponRepository = mockk()

    @BeforeEach
    fun setUp() {
        coEvery { couponRepository.getAllCoupons() } returns
            Result.success(
                COUPONS,
            )

        viewModel =
            OrderViewModel(
                couponRepository,
                orderRepository,
                SHOPPING_CART_PRODUCTS_TO_ORDER,
            )
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `쿠폰을 선택하면 쿠폰의 선택 상태가 변경된다`() {
        // given
        coEvery { couponRepository.getAllCoupons() } returns
            Result.success(
                COUPONS,
            )

        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)

        val couponState =
            CouponState(
                coupon = COUPONS[0],
                isSelected = false,
            )

        // when
        viewModel.couponState.observeForever {}
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.selectCouponState(couponState)

        // then
        assertThat(viewModel.couponState.getOrAwaitValue()[0].isSelected).isEqualTo(true)
    }

    @Test
    fun `선택한 쿠폰을 기준으로 할인 가격이 변경된다`() {
        // given
        // id가 0인 쿠폰은 배송비 무료 쿠폰
        coEvery { couponRepository.getAllCoupons() } returns
            Result.success(
                COUPONS,
            )
        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)

        val couponState =
            CouponState(
                coupon = COUPONS[0],
                isSelected = false,
            )

        // when
        viewModel.couponState.observeForever {}
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.selectCouponState(couponState)

        // then
        assertThat(viewModel.orderState.getOrAwaitValue()).isEqualTo(
            OrderState(
                totalPrice = 300000,
                discountPrice = 3000,
                shippingFee = 0,
                finalPrice = 297000,
            ),
        )
    }

    @Test
    fun `사용 가능한 쿠폰만 표시된다`() {
        // given
        coEvery { couponRepository.getAllCoupons() } returns
            Result.success(
                COUPONS,
            )

        // when
        viewModel =
            OrderViewModel(
                couponRepository,
                orderRepository,
                SHOPPING_CART_PRODUCTS_TO_ORDER,
            )

        // then
        assertThat(viewModel.couponState.getOrAwaitValue()).isEqualTo(
            listOf(
                CouponState(
                    coupon = COUPONS[0],
                    isSelected = false,
                ),
            ),
        )
    }

    @Test
    fun `상품을 결제하면 성공 이벤트를 발송한다`() {
        // given
        coEvery { couponRepository.getAllCoupons() } returns
            Result.success(
                COUPONS,
            )
        val expected1 = SHOPPING_CART_PRODUCTS_TO_ORDER[0].id
        val expected2 = SHOPPING_CART_PRODUCTS_TO_ORDER[1].id

        coEvery { orderRepository.removeProductsFromShoppingCart(any()) } returns
            Result.success(
                Unit,
            )

        // when
        viewModel.proceedOrder()

        // then
        assertThat(viewModel.event.getOrAwaitValue()).isEqualTo(OrderEvent.ORDER_SUCCESS)
    }

    @Test
    fun `상품을 결제하면 장바구니에서 주문한 상품이 삭제된다`() {
        // given
        coEvery { couponRepository.getAllCoupons() } returns
            Result.success(
                COUPONS,
            )

        coEvery { orderRepository.removeProductsFromShoppingCart(any()) } returns
            Result.success(
                Unit,
            )

        // when
        viewModel.proceedOrder()

        // then
        coVerify { orderRepository.removeProductsFromShoppingCart(SHOPPING_CART_PRODUCTS_TO_ORDER) }
    }
}
