package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.coupon.AvailableTime
import woowacourse.shopping.model.coupon.Coupon
import woowacourse.shopping.ui.common.theme.Gray5
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun PaymentBody(
    selectedCouponId: Long?,
    coupons: List<Coupon>,
    originalPrice: Long,
    discountPrice: Long,
    deliveryFee: Long,
    totalPrice: Long,
    modifier: Modifier = Modifier,
    onCouponSelected: (Long) -> Unit,
) {
    LazyColumn(
        modifier,
    ) {
        item {
            Header(modifier = Modifier.padding(top = 30.dp, bottom = 20.dp, start = 18.dp, end = 18.dp))
        }

        items(coupons) { coupon ->
            val couponId = coupon.id ?: return@items
            CouponItem(
                checked = selectedCouponId == couponId,
                couponName = coupon.description,
                expirationDate = coupon.expirationDate,
                minimumAmount = when (coupon) {
                    is Coupon.FixedDiscount -> coupon.minimumAmount.value
                    is Coupon.FreeShipping -> coupon.minimumAmount.value
                    else -> null
                },
                availableTime = when (coupon) {
                    is Coupon.PercentageDiscount -> coupon.availableTime
                    else -> null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp, start = 18.dp, end = 18.dp),
                onCheckedChange = {
                    if (it) onCouponSelected(couponId)
                }
            )
        }

        item {
            HorizontalDivider(
                modifier = Modifier.padding(top = 21.dp),
                thickness = 7.dp,
                color = Color(0xFFEBEBEB)
            )
        }

        item {
            OrderSummary(
                subtotal = originalPrice,
                couponDiscount = discountPrice,
                deliveryFee = deliveryFee,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 21.dp)
            )
        }

        item {
            HorizontalDivider(thickness = 7.dp, color = Color(0xFFEBEBEB))
        }

        item {
            PriceRow(
                text = "총 결제 금액",
                price = totalPrice,
                modifier = Modifier.padding(vertical = 21.dp, horizontal = 18.dp)
            )
        }
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "적용 가능한 쿠폰",
            fontSize = 24.sp,
            fontWeight = FontWeight.W700,
            color = Color(0xFF333333),
        )
        Text(
            text = "* 쿠폰은 1개만 적용 가능합니다.",
            fontSize = 12.sp,
            fontWeight = FontWeight.W400,
            color = Gray5,
        )
    }
}

@Composable
private fun OrderSummary(
    subtotal: Long,
    couponDiscount: Long,
    deliveryFee: Long,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PriceRow(text = "주문 금액", price = subtotal)
        PriceRow(text = "쿠폰 할인 금액", price = couponDiscount)
        PriceRow(text = "배송비", price = deliveryFee)
    }
}

@Composable
private fun PriceRow(
    text: String,
    price: Long,
    modifier: Modifier = Modifier
) {
    val price = "%,d".format(price)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.W700,
            fontSize = 18.sp,
            color = Color(0xFF333333)
        )
        Text(
            text = "${price}원",
            fontWeight = FontWeight.W500,
            fontSize = 18.sp,
            color = Color(0xFF333333)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentBodyPreview() {
    val coupons = listOf(
        Coupon.FixedDiscount(
            id = 1,
            code = "FIXED5000",
            description = "5,000원 할인 쿠폰",
            expirationDate = LocalDate.parse("2024-11-30"),
            discount = Money(5000),
            minimumAmount = Money(100000),
        ),
        Coupon.BuyXGetY(
            id = 2,
            code = "BOGO",
            description = "2개 구매 시 1개 무료",
            expirationDate = LocalDate.parse("2024-05-30"),
            buyQuantity = 2,
            getQuantity = 1,
        ),
        Coupon.FreeShipping(
            id = 3,
            code = "FREESHIPPING",
            description = "5만원 이상 구매 시 무료 배송",
            expirationDate = LocalDate.parse("2024-08-31"),
            minimumAmount = Money(50000),
        ),
        Coupon.PercentageDiscount(
            id = 4,
            code = "MIRACLESALE",
            description = "미라클모닝 30% 할인",
            expirationDate = LocalDate.parse("2024-07-31"),
            discountPercent = 30,
            availableTime = AvailableTime(
                start = LocalTime.parse("04:00:00"),
                end = LocalTime.parse("07:00:00"),
            ),
        ),
    )
    PaymentBody(
        selectedCouponId = 1,
        coupons = coupons,
        modifier = Modifier,
        onCouponSelected = {},
        originalPrice = 204200,
        discountPrice = -5000,
        deliveryFee = 3000,
        totalPrice = 202200,
    )
}

@Preview(showBackground = true)
@Composable
private fun HeaderPreview() {
    Header()
}

@Preview(showBackground = true)
@Composable
private fun OrderSummaryPreview() {
    OrderSummary(
        subtotal = 204200,
        couponDiscount = -5000,
        deliveryFee = 3000,
        modifier = Modifier,
    )
}
