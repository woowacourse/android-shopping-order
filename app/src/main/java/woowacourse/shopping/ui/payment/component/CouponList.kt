package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.payment.uistate.CouponUiModel

@Composable
fun CouponList(
    coupons: List<CouponUiModel>,
    onCouponCheckedChange: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 18.dp, vertical = 30.dp),
    ) {
        CouponListLabel()

        Spacer(modifier = Modifier.height(20.dp))

        coupons.forEachIndexed { index, coupon ->
            CouponItem(
                item = coupon,
                onCheckedChange = { checked ->
                    onCouponCheckedChange(coupon.id, checked)
                },
                modifier = Modifier.fillMaxWidth(),
            )

            if (index != coupons.lastIndex) {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CouponListPreview() {
    val coupons =
        listOf(
            CouponUiModel(
                id = 1L,
                code = "FIXED5000",
                description = "5,000원 할인 쿠폰",
                expirationDate = "2024년 11월 30일",
                minimumAmount = "100,000원",
                isSelected = false,
            ),
            CouponUiModel(
                id = 2L,
                code = "FIXED2000",
                description = "2,000원 할인 쿠폰",
                expirationDate = "2024년 12월 31일",
                minimumAmount = null,
                isSelected = true,
            ),
        )

    CouponList(
        coupons = coupons,
        onCouponCheckedChange = { _, _ -> },
    )
}
