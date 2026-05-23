package woowacourse.shopping.ui.payment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import woowacourse.shopping.model.coupon.Coupon
import woowacourse.shopping.model.Money
import woowacourse.shopping.ui.payment.component.PaymentBody
import woowacourse.shopping.ui.payment.component.PaymentBottomBar
import woowacourse.shopping.ui.payment.component.PaymentTopBar
import java.time.LocalDate

private const val DELIVERY_FEE = 3000

@Composable
fun PaymentScreen(
    modifier: Modifier = Modifier,
) {
    PaymentScreen(
        selectedCouponId = TODO(),
        coupons = TODO(),
        subtotal = TODO(),
        couponDiscount = TODO(),
        deliveryFee = TODO(),
        finalAmount = TODO(),
        modifier = TODO(),
        onBackClick = TODO(),
        onCouponSelected = TODO(),
        onPayClick = TODO()
    )
}

@Composable
fun PaymentScreen(
    selectedCouponId: Long,
    coupons: List<Coupon>,
    subtotal: Int,
    couponDiscount: Int,
    deliveryFee: Int,
    finalAmount: Int,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onCouponSelected: (Long) -> Unit,
    onPayClick: () -> Unit,
) {
    Column(modifier = modifier) {
        PaymentTopBar(
            modifier = Modifier.fillMaxWidth(),
            onBackClick = onBackClick,
        )

        PaymentBody(
            selectedCouponId = selectedCouponId,
            coupons = coupons,
            originalPrice = subtotal,
            discountPrice = couponDiscount,
            deliveryFee = deliveryFee,
            totalPrice = finalAmount,
            modifier = Modifier.weight(1f),
            onCouponSelected = onCouponSelected,
        )

        PaymentBottomBar(
            modifier = Modifier.fillMaxWidth(),
            onClick = onPayClick,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentScreenPreview() {
    val coupons = listOf(
        Coupon.FreeShipping(
            id = 1,
            code = "FREESHIPPING",
            description = "5만원 이상 구매 시 무료 배송",
            expirationDate = LocalDate.of(2026,8,31),
            minimumAmount = Money(50000)
        ),
        Coupon.BuyXGetY(
            id = 2,
            code = "",
            description = "2개 구매 시 1개 무료 쿠폰",
            expirationDate = LocalDate.of(2026,8,31),
            buyQuantity = 3,
            getQuantity = 1
        )
    )
    PaymentScreen(
        selectedCouponId = 2,
        coupons = coupons,
        subtotal = 20000,
        couponDiscount = -1000,
        deliveryFee = 3000,
        finalAmount = 22000,
        modifier = Modifier,
        onBackClick = {},
        onCouponSelected = {},
        onPayClick = {}
    )
}
