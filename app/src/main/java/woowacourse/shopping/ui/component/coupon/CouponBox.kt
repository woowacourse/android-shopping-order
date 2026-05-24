package woowacourse.shopping.ui.component.coupon

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.R
import woowacourse.shopping.model.coupon.AvailableTime
import woowacourse.shopping.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.model.coupon.Coupon
import woowacourse.shopping.model.coupon.FixedDiscountCoupon
import woowacourse.shopping.model.coupon.FreeShippingCoupon
import woowacourse.shopping.model.coupon.PercentageDiscountCoupon
import woowacourse.shopping.ui.theme.AndroidShoppingTheme
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun CouponBox(
    coupon: Coupon,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .defaultMinSize(minHeight = 104.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(4.dp),
            )
            .clickable(onClick = onSelect)
            .padding(vertical = 6.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                modifier = Modifier.align(Alignment.Top),
                checked = isSelected,
                onCheckedChange = { onSelect() },
            )
            Text(
                text = coupon.description,
                fontSize = 16.sp,
                color = Color.Black,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier.padding(start = 12.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = coupon.expirationDate.toExpirationText(),
                fontSize = 12.sp,
                color = Color.Black,
            )
            coupon.minimumAmountTextOrNull()?.let { minimumAmountText ->
                Text(
                    text = minimumAmountText,
                    fontSize = 12.sp,
                    color = Color.Black,
                )
            }
        }
    }
}

@Composable
private fun Coupon.minimumAmountTextOrNull(): String? {
    val priceFormatter = DecimalFormat(stringResource(R.string.price_format_pattern))

    return when (this) {
        is FixedDiscountCoupon -> "최소 주문 금액: ${priceFormatter.format(minimumAmount)}"
        is FreeShippingCoupon -> "최소 주문 금액: ${priceFormatter.format(minimumAmount)}"
        is BuyXGetYCoupon -> null
        is PercentageDiscountCoupon -> null
    }
}

private fun LocalDate.toExpirationText(): String =
    "만료일: ${year}년 ${monthValue}월 ${dayOfMonth}일"

@Preview(showBackground = true)
@Composable
private fun CouponBoxPreview() {
    AndroidShoppingTheme {
        CouponBox(
            coupon = FixedDiscountCoupon(
                id = 1L,
                code = "FIXED5000",
                description = "5,000원 할인 쿠폰",
                expirationDate = LocalDate.of(2026, 12, 31),
                discount = 5_000,
                minimumAmount = 30_000,
            ),
            isSelected = true,
            onSelect = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PercentageCouponBoxPreview() {
    AndroidShoppingTheme {
        CouponBox(
            coupon = PercentageDiscountCoupon(
                id = 2L,
                code = "MORNING30",
                description = "오전 30% 할인 쿠폰",
                expirationDate = LocalDate.of(2026, 12, 31),
                discountPercentage = 30,
                availableTime = AvailableTime(
                    start = LocalTime.of(4, 0),
                    end = LocalTime.of(7, 0),
                ),
            ),
            isSelected = false,
            onSelect = {},
        )
    }
}
