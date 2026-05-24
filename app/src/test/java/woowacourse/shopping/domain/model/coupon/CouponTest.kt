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

    @Test
    fun `BOGO 쿠폰은 무료 제공 가능한 상품 중 단가가 가장 비싼 상품 금액을 할인한다`() {
        val coupon = buyGetFreeCoupon()
        val cheap = purchaseProduct(id = 1L, productId = 1L, price = 1_000, count = 3)
        val expensive = purchaseProduct(id = 2L, productId = 2L, price = 3_000, count = 3)
        val order = orderOf(cheap, expensive)

        coupon.discountAmount(order) shouldBe 3_000
    }

    @Test
    fun `BOGO 쿠폰은 같은 상품 묶음마다 무료 제공 수량만큼 할인한다`() {
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
