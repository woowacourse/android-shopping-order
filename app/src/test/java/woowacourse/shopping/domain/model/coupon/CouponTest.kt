package woowacourse.shopping.domain.model.coupon

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.order.Order
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.order.PurchaseProduct
import woowacourse.shopping.domain.model.order.PurchaseProducts
import java.time.LocalDate
import java.time.LocalTime

class CouponTest {
    @Test
    fun `고정 금액 쿠폰은 최소 주문 금액 이상일 때 정해진 금액을 할인한다`() {
        val coupon = fixedAmountCoupon()
        val order = orderOf(purchaseProduct(price = 100_000, count = 1))

        coupon.discountAmount(order) shouldBe 5_000
    }

    @Test
    fun `고정 금액 쿠폰은 최소 주문 금액 미만이면 할인하지 않는다`() {
        val coupon = fixedAmountCoupon()
        val order = orderOf(purchaseProduct(price = 99_999, count = 1))

        coupon.discountAmount(order) shouldBe 0
    }

    @Test
    fun `N+M 쿠폰은 하나의 라인이 아닌 모든 상품라인에 대해 적용된다`() {
        val coupon = buyGetFreeCoupon()
        val product1 = purchaseProduct(id = 1L, productId = 1L, price = 1_000, count = 3)
        val product2 = purchaseProduct(id = 2L, productId = 2L, price = 3_000, count = 3)
        val order = orderOf(product1, product2)

        coupon.discountAmount(order) shouldBe 4_000
    }

    @Test
    fun `N+M 쿠폰은 같은 상품 묶음마다 무료 제공 수량만큼 할인한다`() {
        val coupon = buyGetFreeCoupon()
        val order = orderOf(purchaseProduct(price = 3_000, count = 6))

        coupon.discountAmount(order) shouldBe 6_000
    }

    @Test
    fun `무료 배송 쿠폰은 최소 주문 금액 이상일 때 배송비를 0원으로 만든다`() {
        val coupon = freeShippingCoupon()
        val order = orderOf(purchaseProduct(price = 50_000, count = 1))

        coupon.deliveryFee(order, defaultDeliveryFee = 3_000) shouldBe 0
    }

    @Test
    fun `미라클모닝 쿠폰은 오전 4시 이상 7시 미만에 주문 금액의 30퍼센트를 할인한다`() {
        val coupon = miracleMorningCoupon()
        val order = orderOf(purchaseProduct(price = 10_000, count = 2))

        coupon.discountAmount(
            order = order,
            context = CouponContext(currentTime = LocalTime.of(4, 0)),
        ) shouldBe 6_000
    }

    @Test
    fun `미라클모닝 쿠폰은 사용 가능 시간이 아니면 할인하지 않는다`() {
        val coupon = miracleMorningCoupon()
        val order = orderOf(purchaseProduct(price = 10_000, count = 2))

        coupon.discountAmount(
            order = order,
            context = CouponContext(currentTime = LocalTime.of(7, 0)),
        ) shouldBe 0
    }

    private fun fixedAmountCoupon() =
        FixedAmountCoupon(
            code = "FIXED5000",
            name = "5,000원 할인 쿠폰",
            expirationDate = LocalDate.of(2024, 11, 30),
            discountAmount = 5_000,
            minimumOrderAmount = 100_000,
        )

    private fun buyGetFreeCoupon() =
        NplusMFreeCoupon(
            code = "BOGO",
            name = "2개 구매 시 1개 무료 쿠폰",
            expirationDate = LocalDate.of(2024, 5, 30),
            purchaseQuantity = 2,
            freeQuantity = 1,
        )

    private fun freeShippingCoupon() =
        FreeShippingCoupon(
            code = "FREESHIPPING",
            name = "5만원 이상 구매 시 무료 배송 쿠폰",
            expirationDate = LocalDate.of(2024, 8, 31),
            minimumOrderAmount = 50_000,
        )

    private fun miracleMorningCoupon() =
        TimeBasedPercentCoupon(
            code = "MIRACLESALE",
            name = "미라클모닝 30% 할인 쿠폰",
            expirationDate = LocalDate.of(2024, 7, 31),
            discountRate = 0.3,
            startTime = LocalTime.of(4, 0),
            endTime = LocalTime.of(7, 0),
        )

    private fun orderOf(vararg purchaseProducts: PurchaseProduct) =
        Order(PurchaseProducts(purchaseProducts.toList()))

    private fun purchaseProduct(
        id: Long = 1L,
        productId: Long = id,
        price: Int,
        count: Int,
    ) =
        PurchaseProduct(
            id = id,
            product =
                Product(
                    category = "category",
                    id = productId,
                    imageUri = "uri$productId",
                    name = "상품$productId",
                    price = price,
                ),
            count = count,
        )
}
