package woowacourse.shopping.ui.component.coupon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.model.coupon.AvailableTime
import woowacourse.shopping.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.model.coupon.Coupon
import woowacourse.shopping.model.coupon.FixedDiscountCoupon
import woowacourse.shopping.model.coupon.FreeShippingCoupon
import woowacourse.shopping.model.coupon.PercentageDiscountCoupon
import woowacourse.shopping.ui.theme.AndroidShoppingTheme
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun CouponList(
    coupons: List<Coupon>,
    selectedCouponId: Long?,
    onCouponSelect: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        items(
            items = coupons,
            key = { coupon -> coupon.id },
        ) { coupon ->
            CouponBox(
                coupon = coupon,
                isSelected = selectedCouponId == coupon.id,
                onSelect = { onCouponSelect(coupon.id) },
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 420)
@Composable
private fun CouponListPreview() {
    AndroidShoppingTheme {
        CouponList(
            coupons = previewCoupons,
            selectedCouponId = 1L,
            onCouponSelect = {},
        )
    }
}

private val previewCoupons: List<Coupon> =
    listOf(
        FixedDiscountCoupon(
            id = 1L,
            code = "FIXED5000",
            description = "5,000원 할인 쿠폰",
            expirationDate = LocalDate.of(2026, 12, 31),
            discount = 5_000,
            minimumAmount = 30_000,
        ),
        FreeShippingCoupon(
            id = 2L,
            code = "FREESHIP50000",
            description = "5만원 이상 무료 배송",
            expirationDate = LocalDate.of(2026, 12, 31),
            minimumAmount = 50_000,
        ),
        BuyXGetYCoupon(
            id = 3L,
            code = "BUY2GET1",
            description = "2+1 쿠폰",
            expirationDate = LocalDate.of(2026, 12, 31),
            buyQuantity = 2,
            getQuantity = 1,
        ),
        PercentageDiscountCoupon(
            id = 4L,
            code = "MORNING30",
            description = "오전 30% 할인 쿠폰",
            expirationDate = LocalDate.of(2026, 12, 31),
            discountPercentage = 30,
            availableTime = AvailableTime(
                start = LocalTime.of(4, 0),
                end = LocalTime.of(7, 0),
            ),
        ),
    )
