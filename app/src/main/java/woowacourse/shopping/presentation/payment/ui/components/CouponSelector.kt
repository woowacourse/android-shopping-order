package woowacourse.shopping.presentation.payment.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import woowacourse.shopping.presentation.payment.model.CouponUiModel
import woowacourse.shopping.ui.theme.AndroidshoppingTheme

@Composable
fun CouponSelector(
    coupons: ImmutableList<CouponUiModel>,
    onSelect: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(coupons) { coupon ->
            CouponCard(
                coupon = coupon,
                onClick = { onSelect(coupon.id) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CouponSelectorPreview() {
    val coupons =
        listOf(
            CouponUiModel(
                id = 1L,
                name = "5,000원 할인 쿠폰",
                isSelected = true,
                expiredDate = "2026년 5월 22일",
                minPayAmount = "10000",
            ),
            CouponUiModel(
                id = 1L,
                name = "5,000원 할인 쿠폰",
                isSelected = false,
                expiredDate = "2026년 5월 22일",
                minPayAmount = "10000",
            ),
        ).toImmutableList()
    AndroidshoppingTheme {
        CouponSelector(
            coupons = coupons,
            onSelect = { },
        )
    }
}
