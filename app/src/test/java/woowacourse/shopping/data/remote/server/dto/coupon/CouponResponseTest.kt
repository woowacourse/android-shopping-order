package woowacourse.shopping.data.remote.server.dto.coupon

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.coupon.CouponContext
import woowacourse.shopping.domain.model.coupon.TimeBasedPercentCoupon
import woowacourse.shopping.domain.model.order.Order
import woowacourse.shopping.domain.model.order.PurchaseProduct
import woowacourse.shopping.domain.model.order.PurchaseProducts
import woowacourse.shopping.domain.model.product.Product
import java.time.LocalTime

class CouponResponseTest {
    @Test
    fun `퍼센트 쿠폰의 할인 값 1은 100퍼센트가 아니라 1퍼센트 할인율로 변환한다`() {
        val coupon =
            CouponResponse(
                id = 1L,
                code = "PERCENT1",
                description = "1% 할인 쿠폰",
                expirationDate = "2026-12-31",
                discountType = "percentage",
                discount = 1,
                availableTime =
                    AvailableTimeResponse(
                        start = "00:00:00",
                        end = "23:59:59",
                    ),
            ).toDomainCoupon() as TimeBasedPercentCoupon
        val order = orderOf(purchaseProduct(price = 10_000, count = 1))

        coupon.discountAmount(
            order = order,
            context = CouponContext(currentTime = LocalTime.NOON),
        ) shouldBe 100
    }

    @Test
    fun `퍼센트 쿠폰의 음수 할인 값은 0퍼센트 할인율로 변환한다`() {
        val coupon =
            CouponResponse(
                id = 1L,
                code = "PERCENT_MINUS",
                description = "잘못된 할인 쿠폰",
                expirationDate = "2026-12-31",
                discountType = "percentage",
                discount = -1,
                availableTime =
                    AvailableTimeResponse(
                        start = "00:00:00",
                        end = "23:59:59",
                    ),
            ).toDomainCoupon() as TimeBasedPercentCoupon
        val order = orderOf(purchaseProduct(price = 10_000, count = 1))

        coupon.discountAmount(
            order = order,
            context = CouponContext(currentTime = LocalTime.NOON),
        ) shouldBe 0
    }

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
