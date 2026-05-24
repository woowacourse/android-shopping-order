package woowacourse.shopping.ui.component.coupon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.R
import woowacourse.shopping.ui.theme.AndroidShoppingTheme
import java.text.DecimalFormat

@Composable
fun CouponSummarySection(
    orderAmount: Int,
    discountAmount: Int,
    shippingFee: Int,
    totalPaymentAmount: Int,
    isNotificationEnabled: Boolean,
    onNotificationEnabledChange: (Boolean) -> Unit,
    onPay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        HorizontalDivider(
            thickness = 3.dp,
            color = Color.LightGray,
        )
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 21.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CouponAmountRow(
                label = "주문 금액",
                amount = orderAmount.toPriceText(),
            )
            CouponAmountRow(
                label = "쿠폰 할인 금액",
                amount = discountAmount.toDiscountPriceText(),
            )
            CouponAmountRow(
                label = "배송비",
                amount = shippingFee.toPriceText(),
            )
        }
        HorizontalDivider(
            thickness = 3.dp,
            color = Color.LightGray,
        )
        CouponAmountRow(
            label = "총 결제 금액",
            amount = totalPaymentAmount.toPriceText(),
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 21.dp),
        )
        PaymentReminderToggleRow(
            isNotificationEnabled = isNotificationEnabled,
            onNotificationEnabledChange = onNotificationEnabledChange,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
        )
        Button(
            onClick = onPay,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RectangleShape,
        ) {
            Text(stringResource(R.string.payment))
        }
    }
}

@Composable
private fun PaymentReminderToggleRow(
    isNotificationEnabled: Boolean,
    onNotificationEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = paymentReminderToggleDescriptionText(),
                color = Color.Black,
                modifier = Modifier.padding(top = 4.dp),
            )
            Switch(
                checked = isNotificationEnabled,
                onCheckedChange = onNotificationEnabledChange,
            )
        }

    }
}

@Composable
private fun CouponAmountRow(
    label: String,
    amount: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.W700,
            color = Color.Black,
        )
        Text(
            text = amount,
            fontSize = 18.sp,
            fontWeight = FontWeight.W700,
            color = Color.Black,
        )
    }
}

@Composable
private fun Int.toPriceText(): String =
    DecimalFormat(stringResource(R.string.price_format_pattern)).format(this)

@Composable
private fun Int.toDiscountPriceText(): String =
    if (this == 0) {
        "0원"
    } else {
        "-${DecimalFormat(stringResource(R.string.price_format_pattern)).format(this)}"
    }

@Composable
private fun paymentReminderToggleText(): String =
    if (LocalInspectionMode.current) {
        "결제 리마인더 알림"
    } else {
        stringResource(R.string.payment_reminder_toggle)
    }

@Composable
private fun paymentReminderToggleDescriptionText(): String =
    if (LocalInspectionMode.current) {
        "결제 미완료 시 5분 뒤 알림 받기"
    } else {
        stringResource(R.string.payment_reminder_toggle_description)
    }

@Preview(showBackground = true)
@Composable
private fun CouponSummarySectionPreview() {
    AndroidShoppingTheme {
        CouponSummarySection(
            orderAmount = 42_000,
            discountAmount = 5_000,
            shippingFee = 3_000,
            totalPaymentAmount = 40_000,
            isNotificationEnabled = true,
            onNotificationEnabledChange = {},
            onPay = {},
        )
    }
}
