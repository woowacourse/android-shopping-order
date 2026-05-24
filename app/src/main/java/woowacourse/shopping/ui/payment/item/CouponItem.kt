package woowacourse.shopping.ui.payment.item

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.R
import woowacourse.shopping.domain.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.FixedCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.PercentCoupon
import woowacourse.shopping.ui.shopping.items.toPriceString
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun CouponItem(
    coupon: Coupon,
    isSelected: Boolean,
    onSelect: (Coupon) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .height(104.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFAAAAAA),
                    shape = RoundedCornerShape(4.dp),
                ).clickable(
                    onClick = { onSelect(coupon) },
                ),
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = {
                    onSelect(coupon)
                },
            )
            Text(
                text = coupon.description,
                fontSize = 18.sp,
                fontWeight = FontWeight.W700,
                color = Color(0xFF555555),
            )
        }
        Text(
            text = stringResource(R.string.label_expiry_date, coupon.expirationDate),
            fontWeight = FontWeight.W400,
            fontSize = 12.sp,
            color = Color(0xFF555555),
            modifier = Modifier.padding(start = 16.dp),
        )
        when (coupon) {
            is FixedCoupon ->
                Text(
                    text = stringResource(R.string.label_min_order_amount, coupon.minimumAmount.toPriceString()),
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                    color = Color(0xFF555555),
                    modifier = Modifier.padding(start = 16.dp),
                )

            is FreeShippingCoupon ->
                Text(
                    text = stringResource(R.string.label_min_order_amount, coupon.minimumAmount.toPriceString()),
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                    color = Color(0xFF555555),
                    modifier = Modifier.padding(start = 16.dp),
                )

            is PercentCoupon ->
                Text(
                    text = stringResource(R.string.label_available_time, coupon.startTime, coupon.endTime),
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                    color = Color(0xFF555555),
                    modifier = Modifier.padding(start = 16.dp),
                )
        }
    }
}

@Preview
@Composable
private fun CouponItemPreview1() {
    CouponItem(
        coupon =
            FixedCoupon(
                id = 1,
                code = "asd",
                description = "5000원 할인 쿠폰",
                expirationDate = LocalDate.now(),
                discountAmount = 5000,
                minimumAmount = 100000,
            ),
        isSelected = true,
        onSelect = { },
    )
}

@Preview
@Composable
private fun CouponItemPreview2() {
    CouponItem(
        coupon =
            FreeShippingCoupon(
                id = 2,
                code = "fads",
                description = "무료 배송 쿠폰",
                expirationDate = LocalDate.now(),
                minimumAmount = 0,
            ),
        isSelected = false,
        onSelect = { },
    )
}

@Preview
@Composable
private fun CouponItemPreview3() {
    CouponItem(
        coupon =
            PercentCoupon(
                id = 3,
                code = "asd",
                description = "점심시간 15% 할인 쿠폰",
                expirationDate = LocalDate.now(),
                discountPercent = 0.15,
                startTime = LocalTime.of(11, 0),
                endTime = LocalTime.of(14, 0),
            ),
        isSelected = false,
        onSelect = { },
    )
}

@Preview
@Composable
private fun CouponItemPreview4() {
    CouponItem(
        coupon =
            BuyXGetYCoupon(
                id = 4,
                code = "sdhja",
                description = "3+1 쿠폰",
                expirationDate = LocalDate.now(),
                buyQuantity = 3,
                getQuantity = 1,
            ),
        isSelected = true,
        onSelect = { },
    )
}
