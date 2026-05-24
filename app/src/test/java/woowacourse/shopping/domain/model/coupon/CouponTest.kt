package woowacourse.shopping.domain.model.coupon

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.order.Order
import woowacourse.shopping.domain.model.order.PurchaseProduct
import woowacourse.shopping.domain.model.order.PurchaseProducts
import woowacourse.shopping.domain.model.product.Product
import java.time.LocalDate

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

    private fun fixedAmountCoupon() =
        FixedAmountCoupon(
            code = "FIXED5000",
            name = "5,000원 할인 쿠폰",
            expirationDate = LocalDate.of(2024, 11, 30),
            discountAmount = 5_000,
            minimumOrderAmount = 100_000,
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
