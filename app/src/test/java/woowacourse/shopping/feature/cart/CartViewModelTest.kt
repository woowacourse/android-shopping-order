package woowacourse.shopping.feature.cart

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.R
import woowacourse.shopping.data.carts.AddItemResult
import woowacourse.shopping.data.carts.CartFetchResult
import woowacourse.shopping.data.carts.CartUpdateResult
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.data.goods.repository.GoodsRepository
import woowacourse.shopping.data.payment.CouponFetchResult
import woowacourse.shopping.data.payment.OrderRequestError
import woowacourse.shopping.data.payment.OrderRequestResult
import woowacourse.shopping.data.payment.dto.CouponResponseItem
import woowacourse.shopping.data.payment.repository.PaymentRepository
import woowacourse.shopping.data.util.mapper.toCartItems
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Goods
import woowacourse.shopping.domain.model.coupon.CalculateBonusGoods
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponService
import woowacourse.shopping.domain.model.coupon.DiscountedAmount
import woowacourse.shopping.domain.model.coupon.ExpirationPeriod
import woowacourse.shopping.domain.model.coupon.MinimumAmount
import woowacourse.shopping.util.InstantTaskExecutorExtension
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class CartViewModelTest {
    @MockK
    private lateinit var cartRepository: CartRepository

    @MockK
    private lateinit var goodsRepository: GoodsRepository

    @MockK
    private lateinit var paymentRepository: PaymentRepository

    @MockK
    private lateinit var couponService: CouponService

    private lateinit var viewModel: CartViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleGoods1 = Goods("상품1", 100000, "url1", 1, "카테고리1")
    private val sampleGoods2 = Goods("상품2", 200000, "url2", 2, "카테고리2")
    private val sampleCartItem1 = CartItem(sampleGoods1, 2, 1, true)
    private val sampleCartItem2 = CartItem(sampleGoods2, 1, 2, false)
    private val fixedCoupon =
        Coupon.Fixed(
            id = 1,
            code = "FIXED5000",
            description = "최소금액 조건 없는 5000원 할인",
            expirationPeriod = ExpirationPeriod(endDate = LocalDate.parse("2099-12-31")),
            minimumAmount = MinimumAmount(0),
            discountedAmount = DiscountedAmount(discountPrice = 5000),
        )

    private val percentageCoupon =
        Coupon.Percentage(
            id = 2,
            code = "PERCENT10",
            description = "10% 할인 쿠폰",
            expirationPeriod =
                ExpirationPeriod(
                    endDate = LocalDate.parse("2099-12-31"),
                    startTime = LocalTime.parse("00:00:00"),
                    endTime = LocalTime.parse("23:59:59"),
                ),
            minimumAmount = MinimumAmount(0),
            discountedAmount = DiscountedAmount(discountRate = 10),
        )

    private val freeShippingCoupon =
        Coupon.FreeShipping(
            id = 3,
            code = "FREESHIP",
            description = "3만원이상 무료배송 쿠폰",
            expirationPeriod = ExpirationPeriod(endDate = LocalDate.parse("2099-12-31")),
            minimumAmount = MinimumAmount(30000),
        )

    private val bonusGoodsCoupon =
        Coupon.BonusGoods(
            id = 4,
            code = "BUY2GET1",
            description = "2+1 혜택",
            expirationPeriod = ExpirationPeriod(endDate = LocalDate.parse("2099-12-31")),
            minimumAmount = MinimumAmount(0),
            calculateBonusGoods = CalculateBonusGoods(buyQuantity = 2, getQuantity = 1),
        )

    @BeforeEach
    fun setUp() =
        runTest {
            Dispatchers.setMain(testDispatcher)
            MockKAnnotations.init(this@CartViewModelTest)
            setupRepositoryMocks()

            viewModel =
                CartViewModel(cartRepository, goodsRepository, paymentRepository, couponService)
            setupLiveDataObservers()
        }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setupRepositoryMocks() {
        coEvery { cartRepository.fetchAllCartItems() } returns
            CartFetchResult.Success(
                mockk(relaxed = true) { every { toCartItems() } returns emptyList() },
            )
        coEvery {
            cartRepository.updateQuantity(any(), any())
        } returns CartUpdateResult.Success(200)
        coEvery { cartRepository.delete(any()) } returns CartFetchResult.Success(200)
        coEvery { cartRepository.addCartItem(any(), any()) } returns
            CartFetchResult.Success(AddItemResult(200, 1))

        // GoodsRepository
        coEvery { goodsRepository.fetchMostRecentGoods() } returns null
        coEvery { goodsRepository.fetchCategoryGoods(any(), any()) } returns mockk(relaxed = true)
        coEvery { goodsRepository.fetchPageGoods(any(), any()) } returns mockk(relaxed = true)

        // PaymentRepository
        coEvery { paymentRepository.fetchAllCoupons() } returns mockk(relaxed = true)
        coEvery { paymentRepository.requestOrder(any()) } returns mockk(relaxed = true)

        // CouponService
        every { couponService.isValid(any(), any()) } returns true
        every { couponService.applyCoupon(any(), any(), any()) } returns mockk(relaxed = true)
    }

    private fun createCartResponseWithItems(items: List<CartItem>): CartResponse =
        mockk(relaxed = true) {
            every { toCartItems() } returns items
            every { content } returns items.map { mockk(relaxed = true) }
            every { totalElements } returns items.size
            every { empty } returns items.isEmpty()
        }

    private fun setupLiveDataObservers() {
        viewModel.cartsList.observeForever { }
        viewModel.isMultiplePages.observeForever { }
        viewModel.isRightPageEnable.observeForever { }
        viewModel.isLeftPageEnable.observeForever { }
        viewModel.totalPrice.observeForever { }
        viewModel.selectedItemCount.observeForever { }
        viewModel.isAllSelected.observeForever { }
        viewModel.currentPageCarts.observeForever { }
        viewModel.validCoupons.observeForever { }
        viewModel.selectedCoupon.observeForever { }
        viewModel.discountAmount.observeForever { }
        viewModel.shippingFee.observeForever { }
        viewModel.totalOrderPrice.observeForever { }
        viewModel.appBarTitle.observeForever { }
        viewModel.recommendedGoods.observeForever { }
    }

    private fun createMockCouponResponseItem(
        discountType: String,
        id: Int,
        discount: Int,
    ) = mockk<CouponResponseItem> {
        every { this@mockk.id } returns id
        every { this@mockk.discountType } returns discountType
        every { this@mockk.discount } returns discount
        every { code } returns "COUPON$id"
        every { description } returns "쿠폰 $id"
        every { expirationDate } returns "2025-12-31"
        every { minimumAmount } returns 0
        every { availableTime } returns null
        every { buyQuantity } returns null
        every { getQuantity } returns null
    }

    @Test
    fun `초기 페이지는 1이어야 한다`() {
        assertThat(viewModel.page.value).isEqualTo(1)
    }

    @Test
    fun `plusPage 호출 시 페이지가 증가한다`() {
        viewModel.plusPage()

        assertThat(viewModel.page.value).isEqualTo(2)
    }

    @Test
    fun `minusPage 호출 시 페이지가 감소한다`() {
        viewModel.setTestPage(2)

        viewModel.minusPage()

        assertThat(viewModel.page.value).isEqualTo(1)
    }

    @Test
    fun `6개 아이템이 있을 때 다중 페이지가 활성화된다`() {
        val items =
            (1..6).map {
                CartItem(Goods("상품$it", 1000, "url$it", it, "카테고리"), 1, it, true)
            }

        viewModel.setTestCarts(items)

        assertThat(viewModel.isMultiplePages.value).isTrue()
        assertThat(viewModel.isRightPageEnable.value).isTrue()
    }

    @Test
    fun `페이지가 2일 때 왼쪽 페이지 버튼이 활성화된다`() {
        val items =
            (1..6).map {
                CartItem(Goods("상품$it", 1000, "url$it", it, "카테고리"), 1, it, true)
            }
        viewModel.setTestCarts(items)

        viewModel.setTestPage(2)

        assertThat(viewModel.isLeftPageEnable.value).isTrue()
    }

    @Test
    fun `아이템 체크 상태가 변경된다`() {
        viewModel.setTestCarts(listOf(sampleCartItem1))

        viewModel.toggleCartItemCheck(sampleCartItem1)

        assertThat(
            viewModel.cartsList.value
                ?.first()
                ?.isSelected,
        ).isFalse()
    }

    @Test
    fun `선택된 아이템들의 총 가격이 계산된다`() {
        val selectedItems =
            listOf(
                CartItem(sampleGoods1, 2, 1, true),
                CartItem(sampleGoods2, 1, 2, true),
            )

        viewModel.setTestCarts(selectedItems)

        assertThat(viewModel.totalPrice.value).isEqualTo(400000)
    }

    @Test
    fun `선택된 아이템들의 총 수량이 계산된다`() {
        val selectedItems =
            listOf(
                CartItem(sampleGoods1, 2, 1, true),
                CartItem(sampleGoods2, 1, 2, true),
            )

        viewModel.setTestCarts(selectedItems)

        assertThat(viewModel.selectedItemCount.value).isEqualTo(3)
    }

    @Test
    fun `모든 아이템이 선택되었을 때 isAllSelected가 true가 된다`() {
        val allSelectedItems =
            listOf(
                CartItem(sampleGoods1, 2, 1, true),
                CartItem(sampleGoods2, 1, 2, true),
            )

        viewModel.setTestCarts(allSelectedItems)

        assertThat(viewModel.isAllSelected.value).isTrue()
    }

    @Test
    fun `첫 번째 페이지에서는 5개 아이템이 표시된다`() {
        val items =
            (1..7).map {
                CartItem(Goods("상품$it", 1000, "url$it", it, "카테고리"), 1, it, true)
            }

        viewModel.setTestCarts(items)

        assertThat(viewModel.currentPageCarts.value?.size).isEqualTo(5)
    }

    @Test
    fun `두 번째 페이지에서는 나머지 아이템들이 표시된다`() {
        val items =
            (1..7).map {
                CartItem(Goods("상품$it", 1000, "url$it", it, "카테고리"), 1, it, true)
            }
        viewModel.setTestCarts(items)

        viewModel.setTestPage(2)

        assertThat(viewModel.currentPageCarts.value?.size).isEqualTo(2)
    }

    @Test
    fun `selectAllItems 호출 시 모든 아이템이 선택된다`() {
        val unselectedItems =
            listOf(
                CartItem(sampleGoods1, 2, 1, false),
                CartItem(sampleGoods2, 1, 2, false),
            )
        viewModel.setTestCarts(unselectedItems)

        viewModel.selectAllItems()

        assertThat(viewModel.isAllSelected.value).isTrue()
    }

    @Test
    fun `increaseCartItemQuantity 호출 시 repository의 updateQuantity가 호출된다`() =
        runTest {
            val cartQuantitySlot = slot<CartQuantity>()
            coEvery {
                cartRepository.updateQuantity(any(), capture(cartQuantitySlot))
            } returns CartUpdateResult.Success(200)

            viewModel.setTestCarts(listOf(sampleCartItem1))

            viewModel.increaseCartItemQuantity(sampleCartItem1)

            coVerify { cartRepository.updateQuantity(eq(1), any()) }
            assertThat(cartQuantitySlot.captured.quantity).isEqualTo(3) // 2 + 1
        }

    @Test
    fun `delete 호출 시 repository의 delete가 호출된다`() =
        runTest {
            coEvery { cartRepository.delete(any()) } returns CartFetchResult.Success(200)
            viewModel.setTestCarts(listOf(sampleCartItem1))

            viewModel.delete(sampleCartItem1)

            coVerify { cartRepository.delete(eq(1)) }
        }

    @Test
    fun `장바구니에 없는 추천 상품 추가 시 장바구니의 아이템이 추가된다`() =
        runTest {
            val goodsSlot = slot<Goods>()
            coEvery {
                cartRepository.addCartItem(capture(goodsSlot), any())
            } returns CartFetchResult.Success(AddItemResult(200, 1))

            viewModel.addCartItemOrIncreaseQuantityFromRecommend(sampleCartItem1)

            coVerify { cartRepository.addCartItem(any(), eq(1)) }
            assertThat(goodsSlot.captured).isEqualTo(sampleCartItem1.goods)
        }

    @Test
    fun `장바구니에 이미 있는 추천 상품 추가 시 장바구니의 수량이 증가한다`() =
        runTest {
            coEvery {
                cartRepository.updateQuantity(any(), any())
            } returns CartUpdateResult.Success(200)

            viewModel.setTestCarts(listOf(sampleCartItem1))

            viewModel.addCartItemOrIncreaseQuantityFromRecommend(sampleCartItem1)

            coVerify { cartRepository.updateQuantity(eq(1), any()) }
        }

    @Test
    fun `getPosition 호출 시 장바구니 아이템의 인덱스를 반환한다`() {
        viewModel.setTestCarts(listOf(sampleCartItem1, sampleCartItem2))

        assertThat(viewModel.getPosition(sampleCartItem1)).isEqualTo(0)
        assertThat(viewModel.getPosition(sampleCartItem2)).isEqualTo(1)
    }

    @Test
    fun `decreaseCartItemQuantity 호출 시 수량이 1이면 delete가 호출된다`() =
        runTest {
            val itemWithQuantity1 = CartItem(sampleGoods1, 1, 1, true)
            coEvery { cartRepository.delete(any()) } returns CartFetchResult.Success(200)
            viewModel.setTestCarts(listOf(itemWithQuantity1))

            viewModel.removeCartItemOrDecreaseQuantity(itemWithQuantity1)

            coVerify { cartRepository.delete(eq(1)) }
        }

    @Test
    fun `decreaseCartItemQuantity 호출 시 수량이 2 이상이면 updateQuantity가 호출된다`() =
        runTest {
            coEvery {
                cartRepository.updateQuantity(any(), any())
            } returns CartUpdateResult.Success(200)
            viewModel.setTestCarts(listOf(sampleCartItem1)) // quantity = 2

            viewModel.removeCartItemOrDecreaseQuantity(sampleCartItem1)

            coVerify { cartRepository.updateQuantity(eq(1), match { it.quantity == 1 }) }
        }

    @Test
    fun `removeCartItemOrDecreaseQuantity 호출 시 수량이 1이면 delete가 호출된다`() =
        runTest {
            coEvery { cartRepository.delete(any()) } returns CartFetchResult.Success(200)
            viewModel.setTestCarts(listOf(sampleCartItem2))

            viewModel.removeCartItemOrDecreaseQuantity(sampleCartItem2)

            coVerify { cartRepository.delete(eq(2)) }
        }

    @Test
    fun `updateWholeCarts 호출 시 repository의 fetchAllCartItems가 호출된다`() =
        runTest {
            val cartResponse = createCartResponseWithItems(listOf(sampleCartItem1, sampleCartItem2))
            coEvery { cartRepository.fetchAllCartItems() } returns
                CartFetchResult.Success(
                    cartResponse,
                )

            viewModel.updateWholeCarts()

            coVerify { cartRepository.fetchAllCartItems() }
        }

    @Test
    fun `paymentSubmit 성공 시 orderSuccessEvent가 발생한다`() =
        runTest {
            val selectedItems =
                listOf(
                    sampleCartItem1.copy(isSelected = true),
                    sampleCartItem2.copy(isSelected = true),
                )
            viewModel.setTestCarts(selectedItems)
            coEvery {
                paymentRepository.requestOrder(
                    listOf(
                        1,
                        2,
                    ),
                )
            } returns OrderRequestResult.Success(200)

            viewModel.paymentSubmit()

            assertThat(viewModel.orderSuccessEvent.getValue()).isNotNull()
        }

    @Test
    fun `paymentSubmit 네트워크 에러 시 orderFailedEvent가 발생한다`() =
        runTest {
            viewModel.setTestCarts(listOf(sampleCartItem1.copy(isSelected = true)))
            coEvery { paymentRepository.requestOrder(any()) } returns
                OrderRequestResult.Error(OrderRequestError.Network)

            viewModel.paymentSubmit()

            assertThat(viewModel.orderFailedEvent.getValue())
                .isEqualTo(R.string.order_payment_network_error_alert)
        }

    @Test
    fun `paymentSubmit 서버 에러 시 orderFailedEvent가 발생한다`() =
        runTest {
            viewModel.setTestCarts(listOf(sampleCartItem1.copy(isSelected = true)))
            coEvery { paymentRepository.requestOrder(any()) } returns
                OrderRequestResult.Error(OrderRequestError.Server(500, "Internal Server Error"))

            viewModel.paymentSubmit()

            assertThat(viewModel.orderFailedEvent.getValue())
                .isEqualTo(R.string.order_payment_server_error_alert)
        }

    @Test
    fun `장바구니에서 선택된 아이템만 주문 요청된다`() =
        runTest {
            val items =
                listOf(
                    sampleCartItem1.copy(isSelected = true),
                    sampleCartItem2.copy(isSelected = false),
                )
            viewModel.setTestCarts(items)
            coEvery { paymentRepository.requestOrder(any()) } returns
                OrderRequestResult.Success(200)

            viewModel.paymentSubmit()

            coVerify { paymentRepository.requestOrder(eq(listOf(1))) }
        }

    @Test
    fun `updateWholeCoupons 성공 시 쿠폰 목록이 업데이트된다`() =
        runTest {
            val mockCouponResponse =
                listOf(
                    createMockCouponResponseItem("fixed", 1, 5000),
                    createMockCouponResponseItem("percentage", 2, 10),
                )
            coEvery { paymentRepository.fetchAllCoupons() } returns
                CouponFetchResult.Success(mockCouponResponse)

            viewModel.updateWholeCoupons()

            assertThat(viewModel.validCoupons.getValue()?.size).isEqualTo(2)
        }

    @Test
    fun `toggleCouponCheck 호출 시 쿠폰 선택 상태가 변경된다`() =
        runTest {
            viewModel.setTestCoupons(listOf(fixedCoupon))
            viewModel.toggleCouponCheck(fixedCoupon)

            assertThat(viewModel.selectedCoupon.getValue()?.id).isEqualTo(1)
        }

    @Test
    fun `Fixed 쿠폰 선택 시 고정 할인 금액이 적용된다`() =
        runTest {
            // Given: 선택된 장바구니 아이템과 Fixed 쿠폰 (200000원)
            val selectedItems = listOf(sampleCartItem1.copy(isSelected = true))
            viewModel.setTestCarts(selectedItems)
            viewModel.setTestCoupons(listOf(fixedCoupon))

            // When: 쿠폰 선택
            viewModel.toggleCouponCheck(fixedCoupon)

            // Then: 5000원 할인 적용
            assertThat(viewModel.discountAmount.value).isEqualTo(-5000)
            // 200000 - 5000 + 3000(배송비)
            assertThat(viewModel.totalOrderPrice.value).isEqualTo(198000)
        }

    @Test
    fun `Percentage 쿠폰 선택 시 비율 할인이 적용된다`() =
        runTest {
            // 200000원
            val selectedItems = listOf(sampleCartItem1.copy(isSelected = true))
            viewModel.setTestCarts(selectedItems)
            viewModel.setTestCoupons(listOf(percentageCoupon))

            viewModel.toggleCouponCheck(percentageCoupon)

            // 10% 할인
            assertThat(viewModel.discountAmount.value).isEqualTo(-20000)
            // 200000 - 20000 + 3000
            assertThat(viewModel.totalOrderPrice.value).isEqualTo(183000)
        }

    @Test
    fun `FreeShipping 쿠폰 선택 시 배송비가 0이 된다`() =
        runTest {
            // 200000원
            val selectedItems = listOf(sampleCartItem1.copy(isSelected = true))
            viewModel.setTestCarts(selectedItems)
            viewModel.setTestCoupons(listOf(freeShippingCoupon))

            viewModel.toggleCouponCheck(freeShippingCoupon)

            assertThat(viewModel.shippingFee.value).isEqualTo(0)
            // 배송비 없음
            assertThat(viewModel.totalOrderPrice.value).isEqualTo(200000)
        }

    @Test
    fun `BonusGoods 쿠폰 적용 시 실제 보너스 계산이 적용된다`() =
        runTest {
            val cartItems =
                listOf(
                    // 3개 구매
                    sampleCartItem1.copy(quantity = 3, isSelected = true),
                )
            viewModel.setTestCarts(cartItems)
            viewModel.setTestCoupons(listOf(bonusGoodsCoupon))

            viewModel.toggleCouponCheck(bonusGoodsCoupon)

            assertThat(viewModel.selectedCoupon.value?.id).isEqualTo(4)
            // 2개 + 배송비
            assertThat(viewModel.totalOrderPrice.value).isEqualTo(sampleGoods1.price * 2 + 3000)
        }

    @Test
    fun `최소금액 미달 시 쿠폰이 유효하지 않다`() =
        runTest {
            val smallAmountItems =
                listOf(
                    // 100000원 (최소금액 30000원 미달)
                    CartItem(sampleGoods1, 1, 1, true),
                )
            viewModel.setTestCarts(smallAmountItems)

            every { couponService.isValid(freeShippingCoupon, any()) } returns false
            viewModel.setTestCoupons(listOf(freeShippingCoupon))

            assertThat(viewModel.validCoupons.value).doesNotContain(freeShippingCoupon)
        }
}
