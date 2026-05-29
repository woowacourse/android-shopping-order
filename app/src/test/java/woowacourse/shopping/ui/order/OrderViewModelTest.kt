@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.ui.order

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FreeShippingPolicy
import woowacourse.shopping.domain.model.coupon.OrderFixedAmountDiscountPolicy
import woowacourse.shopping.domain.model.coupon.OrderPercentageDiscountPolicy
import woowacourse.shopping.domain.model.cart.SelectedCartOrder
import woowacourse.shopping.domain.model.cart.SelectedCartOrderItem
import woowacourse.shopping.domain.model.coupon.SameProductQuantityDiscountPolicy
import woowacourse.shopping.data.remote.common.NetworkMonitor
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.repository.CartRepositoryFixture
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.NotificationSettingRepository
import woowacourse.shopping.domain.repository.PendingOrderRepository
import woowacourse.shopping.domain.repository.query.CartPageResult
import woowacourse.shopping.notification.UnpaidOrderReminderScheduler
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
class OrderViewModelTest {
    private lateinit var dispatcher: TestDispatcher
    private lateinit var cartRepository: RecordingCartRepository
    private lateinit var couponRepository: FakeCouponRepository
    private lateinit var pendingOrderRepository: FakePendingOrderRepository
    private lateinit var notificationSettingRepository: FakeNotificationSettingRepository
    private lateinit var reminderScheduler: FakeUnpaidOrderReminderScheduler
    private lateinit var viewModel: OrderViewModel

    private val shrimpCracker = CartRepositoryFixture.shrimpCracker
    private val sourCandy = CartRepositoryFixture.sourCandy

    @BeforeEach
    fun setUp() {
        dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)

        cartRepository = RecordingCartRepository()
        pendingOrderRepository = FakePendingOrderRepository()
        notificationSettingRepository = FakeNotificationSettingRepository(isEnabled = false)
        reminderScheduler = FakeUnpaidOrderReminderScheduler()
        couponRepository =
            FakeCouponRepository(
                coupons =
                    listOf(
                        Coupon(
                            id = 1L,
                            code = "FIXED5000",
                            title = "5,000원 할인 쿠폰",
                            description = "",
                            expirationDate = LocalDate.of(2026, 12, 31),
                            minimumOrderAmount = 100_000,
                            policy = OrderFixedAmountDiscountPolicy(amount = 5_000),
                        ),
                        Coupon(
                            id = 2L,
                            code = "FREESHIPPING",
                            title = "무료 배송 쿠폰",
                            description = "",
                            expirationDate = LocalDate.of(2026, 10, 31),
                            minimumOrderAmount = 50_000,
                            policy = FreeShippingPolicy,
                        ),
                        Coupon(
                            id = 3L,
                            code = "BOGO",
                            title = "3개 구매 1개 가격 할인 쿠폰",
                            description = "",
                            expirationDate = LocalDate.of(2026, 9, 30),
                            policy = SameProductQuantityDiscountPolicy(requiredSameProductQuantity = 3),
                        ),
                        Coupon(
                            id = 4L,
                            code = "MIRACLESALE",
                            title = "미라클 세일 30% 할인 쿠폰",
                            description = "",
                            expirationDate = LocalDate.of(2026, 9, 30),
                            availableFromHour = 4,
                            availableToHourExclusive = 7,
                            policy = OrderPercentageDiscountPolicy(rate = 30),
                        ),
                        Coupon(
                            id = 5L,
                            code = "MIRACLESALE",
                            title = "미라클 세일 30% 할인 쿠폰",
                            description = "",
                            expirationDate = LocalDate.of(2026, 5, 1),
                            availableFromHour = 4,
                            availableToHourExclusive = 7,
                            policy = OrderPercentageDiscountPolicy(rate = 30),
                        ),
                    ),
            )
        viewModel =
            OrderViewModel(
                cartRepository = cartRepository,
                couponRepository = couponRepository,
                pendingOrderRepository = pendingOrderRepository,
                notificationSettingRepository = notificationSettingRepository,
                reminderScheduler = reminderScheduler,
                networkMonitor = FakeNetworkMonitor(),
                clock = Clock.fixed(Instant.parse("2026-05-22T05:00:00Z"), ZoneId.of("UTC")),
            )

        dispatcher.scheduler.advanceUntilIdle()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `선택된 장바구니 상품으로 결제 요약을 구성한다`() {
        viewModel.startOrder(
            SelectedCartOrder(
                items =
                    listOf(
                        SelectedCartOrderItem(
                            cartItemId = 101L,
                            productId = shrimpCracker.id,
                            price = shrimpCracker.price.value,
                            quantity = 2,
                        ),
                        SelectedCartOrderItem(
                            cartItemId = 102L,
                            productId = sourCandy.id,
                            price = sourCandy.price.value,
                            quantity = 1,
                        ),
                    ),
            ),
        )

        val uiState = viewModel.uiState.value

        assertEquals(7_500L, uiState.priceSummary.items[0].price)
        assertEquals(0L, uiState.priceSummary.items[1].price)
        assertEquals(3_000L, uiState.priceSummary.items[2].price)
        assertEquals(10_500L, uiState.priceSummary.totalPaymentPrice)
        assertTrue(uiState.isPaymentEnabled)
    }

    @Test
    fun `주문 화면 진입 시 현재 주문에 적용 가능한 쿠폰 목록을 조회한다`() =
        runTest(dispatcher.scheduler) {
            viewModel.startOrder(
                SelectedCartOrder(
                    items =
                        listOf(
                            SelectedCartOrderItem(
                                cartItemId = 101L,
                                productId = shrimpCracker.id,
                                price = 60_000,
                                quantity = 2,
                            ),
                        ),
                ),
            )
            advanceUntilIdle()

            assertEquals(
                listOf("5,000원 할인 쿠폰", "무료 배송 쿠폰", "미라클 세일 30% 할인 쿠폰"),
                viewModel.uiState.value.coupons
                    .map { it.title },
            )
        }

    @Test
    fun `주문 화면 진입 시 현재 주문 세션을 저장한다`() =
        runTest(dispatcher.scheduler) {
            val order = selectedCartOrder(totalPrice = 120_000, quantity = 2)

            viewModel.startOrder(order)
            advanceUntilIdle()

            assertEquals(order, pendingOrderRepository.getPendingOrder())
        }

    @Test
    fun `저장된 주문 세션이 있으면 복원해 결제 화면 상태를 다시 구성한다`() =
        runTest(dispatcher.scheduler) {
            val order = selectedCartOrder(totalPrice = 120_000, quantity = 2)
            pendingOrderRepository.savePendingOrder(order)

            val restored = viewModel.restorePendingOrderIfAvailable()
            advanceUntilIdle()

            assertTrue(restored)
            assertTrue(viewModel.uiState.value.hasPendingOrder)
            assertEquals(123_000L, viewModel.uiState.value.priceSummary.totalPaymentPrice)
        }

    @Test
    fun `쿠폰은 한 번에 하나만 선택할 수 있다`() =
        runTest(dispatcher.scheduler) {
            viewModel.startOrder(
                selectedCartOrder(totalPrice = 120_000, quantity = 2),
            )
            advanceUntilIdle()

            viewModel.toggleCouponSelection(couponId = 1L, isSelected = true)
            viewModel.toggleCouponSelection(couponId = 2L, isSelected = true)

            assertEquals(
                mapOf(
                    1L to false,
                    2L to true,
                    4L to false,
                ),
                viewModel.uiState.value.coupons
                    .associate { it.id to it.isSelected },
            )
        }

    @Test
    fun `정액 할인 쿠폰을 선택하면 할인 금액과 총 결제 금액이 반영된다`() =
        runTest(dispatcher.scheduler) {
            viewModel.startOrder(
                selectedCartOrder(totalPrice = 120_000, quantity = 2),
            )
            advanceUntilIdle()

            viewModel.toggleCouponSelection(couponId = 1L, isSelected = true)

            val summary = viewModel.uiState.value.priceSummary
            assertEquals(-5_000L, summary.items[1].price)
            assertEquals(118_000L, summary.totalPaymentPrice)
        }

    @Test
    fun `무료 배송 쿠폰을 선택하면 배송비가 0원이 된다`() =
        runTest(dispatcher.scheduler) {
            viewModel.startOrder(
                selectedCartOrder(totalPrice = 60_000, quantity = 1),
            )
            advanceUntilIdle()

            viewModel.toggleCouponSelection(couponId = 2L, isSelected = true)

            val summary = viewModel.uiState.value.priceSummary
            assertEquals(0L, summary.items[1].price)
            assertEquals(0L, summary.items[2].price)
            assertEquals(60_000L, summary.totalPaymentPrice)
        }

    @Test
    fun `미라클 세일 쿠폰을 선택하면 주문 금액의 30퍼센트를 할인한다`() =
        runTest(dispatcher.scheduler) {
            viewModel.startOrder(
                selectedCartOrder(totalPrice = 100_000, quantity = 1),
            )
            advanceUntilIdle()

            viewModel.toggleCouponSelection(couponId = 4L, isSelected = true)

            val summary = viewModel.uiState.value.priceSummary
            assertEquals(-30_000L, summary.items[1].price)
            assertEquals(73_000L, summary.totalPaymentPrice)
        }

    @Test
    fun `보고 쿠폰을 선택하면 수량 조건을 만족하는 상품 중 가장 비싼 한 개 가격만 할인한다`() =
        runTest(dispatcher.scheduler) {
            viewModel.startOrder(
                SelectedCartOrder(
                    items =
                        listOf(
                            SelectedCartOrderItem(
                                cartItemId = 101L,
                                productId = shrimpCracker.id,
                                price = 15_000,
                                quantity = 3,
                            ),
                            SelectedCartOrderItem(
                                cartItemId = 102L,
                                productId = sourCandy.id,
                                price = 20_000,
                                quantity = 3,
                            ),
                        ),
                ),
            )
            advanceUntilIdle()

            assertTrue(
                viewModel.uiState.value.coupons
                    .any { it.id == 3L },
            )

            viewModel.toggleCouponSelection(couponId = 3L, isSelected = true)

            val summary = viewModel.uiState.value.priceSummary
            assertEquals(-20_000L, summary.items[1].price)
            assertEquals(88_000L, summary.totalPaymentPrice)
        }

    @Test
    fun `조건에 맞지 않거나 만료된 쿠폰은 목록에 포함되지 않는다`() =
        runTest(dispatcher.scheduler) {
            viewModel.startOrder(
                selectedCartOrder(totalPrice = 20_000, quantity = 2),
            )
            advanceUntilIdle()

            assertEquals(
                listOf(4L),
                viewModel.uiState.value.coupons
                    .map { it.id },
            )
        }

    @Test
    fun `결제하기 버튼을 누르면 주문을 생성하고 완료 이벤트를 보낸다`() =
        runTest(dispatcher.scheduler) {
            cartRepository.setCartItems(
                listOf(
                    StoredCartItem(cartItemId = 101L, productId = shrimpCracker.id, quantity = 2),
                    StoredCartItem(cartItemId = 102L, productId = sourCandy.id, quantity = 1),
                ),
            )
            viewModel.startOrder(
                SelectedCartOrder(
                    items =
                        listOf(
                            SelectedCartOrderItem(
                                cartItemId = 101L,
                                productId = shrimpCracker.id,
                                price = shrimpCracker.price.value,
                                quantity = 2,
                            ),
                            SelectedCartOrderItem(
                                cartItemId = 102L,
                                productId = sourCandy.id,
                                price = sourCandy.price.value,
                                quantity = 1,
                            ),
                        ),
                ),
            )

            val event = async { viewModel.events.first() }

            viewModel.placeOrder()
            advanceUntilIdle()

            assertEquals(listOf(listOf(101L, 102L)), cartRepository.createdOrders)
            assertTrue(cartRepository.getCartItemsByProductIds(setOf(shrimpCracker.id, sourCandy.id)).isEmpty())
            assertEquals(OrderEvent.OrderCompleted, event.await())
            assertEquals(0L, viewModel.uiState.value.priceSummary.totalPaymentPrice)
            assertEquals(null, pendingOrderRepository.getPendingOrder())
        }

    @Test
    fun `주문 화면을 이탈하면 저장된 주문 세션을 삭제한다`() {
        viewModel.startOrder(selectedCartOrder(totalPrice = 120_000, quantity = 2))

        viewModel.clearPendingOrderSession()

        assertEquals(null, pendingOrderRepository.getPendingOrder())
        assertEquals(false, viewModel.uiState.value.hasPendingOrder)
    }

    @Test
    fun `미결제 알림 설정이 바뀌면 주문 화면 상태도 함께 갱신된다`() =
        runTest(dispatcher.scheduler) {
            notificationSettingRepository.setUnpaidNotificationEnabled(true)
            advanceUntilIdle()

            assertEquals(true, viewModel.uiState.value.isReminderEnabled)
        }

    @Test
    fun `주문 세션과 알림 설정이 모두 활성화되면 미결제 알림을 예약한다`() =
        runTest(dispatcher.scheduler) {
            notificationSettingRepository.setUnpaidNotificationEnabled(true)

            viewModel.startOrder(selectedCartOrder(totalPrice = 120_000, quantity = 2))
            advanceUntilIdle()

            assertEquals(1, reminderScheduler.scheduleCount)
            assertEquals(1, reminderScheduler.cancelCount)
        }

    @Test
    fun `주문 세션이 종료되면 미결제 알림을 취소한다`() =
        runTest(dispatcher.scheduler) {
            notificationSettingRepository.setUnpaidNotificationEnabled(true)
            viewModel.startOrder(selectedCartOrder(totalPrice = 120_000, quantity = 2))
            advanceUntilIdle()

            viewModel.clearPendingOrderSession()
            advanceUntilIdle()

            assertEquals(2, reminderScheduler.cancelCount)
        }

    private class FakeNetworkMonitor : NetworkMonitor {
        override val isNetworkConnected = MutableStateFlow(true)
    }

    private fun selectedCartOrder(
        totalPrice: Int,
        quantity: Int,
    ): SelectedCartOrder =
        SelectedCartOrder(
            items =
                listOf(
                    SelectedCartOrderItem(
                        cartItemId = 101L,
                        productId = shrimpCracker.id,
                        price = totalPrice / quantity,
                        quantity = quantity,
                    ),
                ),
        )

    private class FakeCouponRepository(
        private val coupons: List<Coupon>,
    ) : CouponRepository {
        override suspend fun getCoupons(): List<Coupon> = coupons
    }

    private class FakePendingOrderRepository : PendingOrderRepository {
        private var pendingOrder: SelectedCartOrder? = null

        override fun getPendingOrder(): SelectedCartOrder? = pendingOrder

        override fun savePendingOrder(order: SelectedCartOrder) {
            pendingOrder = order
        }

        override fun clearPendingOrder() {
            pendingOrder = null
        }
    }

    private class FakeNotificationSettingRepository(
        private var isEnabled: Boolean,
    ) : NotificationSettingRepository {
        override val unpaidNotificationEnabled = MutableStateFlow(isEnabled)

        override fun isUnpaidNotificationEnabled(): Boolean = isEnabled

        override fun setUnpaidNotificationEnabled(isEnabled: Boolean) {
            this.isEnabled = isEnabled
            unpaidNotificationEnabled.value = isEnabled
        }
    }

    private class FakeUnpaidOrderReminderScheduler : UnpaidOrderReminderScheduler {
        var scheduleCount: Int = 0
            private set
        var cancelCount: Int = 0
            private set

        override fun schedule() {
            scheduleCount += 1
        }

        override fun cancel() {
            cancelCount += 1
        }
    }

    private class RecordingCartRepository : CartRepository {
        val createdOrders = mutableListOf<List<Long>>()
        private val cartItems = linkedMapOf<Long, StoredCartItem>()

        fun setCartItems(items: List<StoredCartItem>) {
            cartItems.clear()
            items.forEach { item ->
                cartItems[item.productId] = item
            }
        }

        override suspend fun createOrder(cartItemIds: List<Long>) {
            createdOrders += cartItemIds
            val orderedIds = cartItemIds.toSet()
            cartItems.entries.removeIf { (_, item) -> item.cartItemId in orderedIds }
        }

        override suspend fun setQuantity(
            productId: Long,
            quantity: Int,
        ) {
            if (quantity == 0) {
                cartItems.remove(productId)
                return
            }

            cartItems[productId] =
                StoredCartItem(
                    cartItemId = cartItems[productId]?.cartItemId ?: productId,
                    productId = productId,
                    quantity = quantity,
                )
        }

        override suspend fun getCartPage(
            page: Int,
            size: Int,
        ): CartPageResult = CartPageResult(emptyList(), 0, 0, 0)

        override suspend fun getCartItemsByProductIds(productIds: Set<Long>): List<CartItem> =
            cartItems.values
                .filter { it.productId in productIds }
                .map { item ->
                    CartItem(
                        productId = item.productId,
                        quantity = item.quantity,
                    )
                }

        override suspend fun count(): Int = cartItems.size
    }

    private data class StoredCartItem(
        val cartItemId: Long,
        val productId: Long,
        val quantity: Int,
    )
}
