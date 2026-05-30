package woowacourse.shopping.presentation.payment.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import woowacourse.shopping.presentation.cart.ui.components.CartCheckBox
import woowacourse.shopping.presentation.payment.model.CouponUiModel
import woowacourse.shopping.ui.theme.AndroidshoppingTheme
import woowacourse.shopping.ui.theme.Gray40
import woowacourse.shopping.ui.theme.Gray50
import woowacourse.shopping.util.formattedPrice

@Composable
fun CouponCard(
    coupon: CouponUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(120.dp)
                .border(1.dp, Gray40, RoundedCornerShape(5.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                CartCheckBox(
                    isSelected = isSelected,
                    onClick = onClick,
                )
                Text(
                    text = coupon.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp,
                    color = Color.Black,
                )
            }
            CouponContent(
                expiredDate = coupon.expiredDate,
                minPayAmount = coupon.minPayAmount,
            )
        }
    }
}

@Composable
private fun CouponContent(
    expiredDate: String?,
    minPayAmount: String?,
) {
    Column {
        if (expiredDate != null) {
            Text(
                text = "${stringResource(R.string.coupon_expiration_date)} $expiredDate",
                fontSize = 12.sp,
                color = Gray50,
            )
        }
        if (minPayAmount != null) {
            Text(
                text = "${stringResource(R.string.minimum_order_amount)} $minPayAmount",
                fontSize = 12.sp,
                color = Gray50,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CouponCardPreview() {
    AndroidshoppingTheme {
        CouponCard(
            isSelected = true,
            coupon =
                CouponUiModel(
                    id = 1L,
                    name = "5,000원 할인 쿠폰",
                    expiredDate = "2026년 5월 22일",
                    minPayAmount = formattedPrice(10000),
                ),
            onClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CouponCardNoExpiredDatePreview() {
    AndroidshoppingTheme {
        CouponCard(
            isSelected = false,
            coupon =
                CouponUiModel(
                    id = 1L,
                    name = "5,000원 할인 쿠폰",
                    expiredDate = null,
                    minPayAmount = formattedPrice(10000),
                ),
            onClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CouponCardNoMinPayAmountPreview() {
    AndroidshoppingTheme {
        CouponCard(
            isSelected = false,
            coupon =
                CouponUiModel(
                    id = 1L,
                    name = "5,000원 할인 쿠폰",
                    expiredDate = "2026년 5월 22일",
                    minPayAmount = null,
                ),
            onClick = {},
        )
    }
}
