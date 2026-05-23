package woowacourse.shopping.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.model.coupon.AvailableTime
import woowacourse.shopping.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.model.coupon.FixedDiscountCoupon
import woowacourse.shopping.model.coupon.FreeShippingCoupon
import woowacourse.shopping.model.coupon.PercentageDiscountCoupon
import woowacourse.shopping.ui.component.coupon.CouponHeaderSection
import woowacourse.shopping.ui.component.coupon.CouponList
import woowacourse.shopping.ui.component.coupon.CouponSummarySection
import woowacourse.shopping.ui.component.coupon.CouponTopBar
import woowacourse.shopping.ui.state.CouponUiState
import woowacourse.shopping.ui.theme.AndroidShoppingTheme
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun CouponScreen(
    uiState: CouponUiState,
    onBackClick: () -> Unit,
    onCouponSelect: (Long) -> Unit,
    onPay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CouponTopBar(
                onBackClick = onBackClick,
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            CouponHeaderSection()
            CouponList(
                coupons = uiState.coupons,
                selectedCouponId = uiState.selectedCouponId,
                onCouponSelect = onCouponSelect,
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 12.dp),
            )
            CouponSummarySection(
                orderAmount = uiState.orderAmount,
                discountAmount = uiState.discountAmount,
                shippingFee = uiState.shippingFee,
                totalPaymentAmount = uiState.totalPaymentAmount,
                onPay = onPay,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CouponScreenPreview() {
    AndroidShoppingTheme {
        CouponScreen(
            uiState = CouponUiState(
                coupons = listOf(
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
                ),
                selectedCouponId = 1L,
            ),
            onBackClick = {},
            onCouponSelect = {},
            onPay = {},
        )
    }
}
