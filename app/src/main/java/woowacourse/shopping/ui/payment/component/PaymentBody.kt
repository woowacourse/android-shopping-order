package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.payment.CouponUiModel
import woowacourse.shopping.ui.payment.PaymentUiState

private val CouponListMaxHeight = 246.dp

@Composable
internal fun PaymentBody(
    uiState: PaymentUiState,
    onCouponClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.White),
    ) {
        CouponSection(
            uiState = uiState,
            onCouponClick = onCouponClick,
        )
        PaymentSummarySection(uiState = uiState)
    }
}

@Composable
private fun CouponSection(
    uiState: PaymentUiState,
    onCouponClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 26.dp)
                .padding(top = 36.dp, bottom = 34.dp),
    ) {
        Text(
            text = "적용 가능한 쿠폰",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "* 쿠폰은 1개만 적용 가능합니다.",
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 16.dp),
        )
        Spacer(modifier = Modifier.height(28.dp))
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = CouponListMaxHeight)
                    .verticalScroll(rememberScrollState()),
        ) {
            uiState.coupons.forEachIndexed { index, coupon ->
                CouponCard(
                    coupon = coupon,
                    checked = uiState.isSelected(coupon),
                    onClick = { onCouponClick(coupon.code) },
                )
                if (index < uiState.coupons.lastIndex) {
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentBodyPreview() {
    PaymentBody(
        uiState =
            PaymentUiState(
                coupons =
                    listOf(
                        CouponUiModel(
                            code = "FIXED5000",
                            title = "5,000원 할인 쿠폰",
                            expirationDate = "2026년 11월 30일",
                            minimumOrderAmount = "100,000원",
                        ),
                        CouponUiModel(
                            code = "BOGO",
                            title = "2개 구매 시 1개 무료 쿠폰",
                            expirationDate = "2026년 11월 30일",
                        ),
                        CouponUiModel(
                            code = "BOGO",
                            title = "2개 구매 시 1개 무료 쿠폰",
                            expirationDate = "2026년 11월 30일",
                        ),
                        CouponUiModel(
                            code = "RATE10",
                            title = "10% 할인 쿠폰",
                            expirationDate = "2026년 11월 30일",
                            minimumOrderAmount = "50,000원",
                        ),
                    ),
                selectedCouponCode = "FIXED5000",
                formattedOrderAmount = "204,200원",
                formattedCouponDiscountAmount = "-5,000원",
                formattedDeliveryFee = "3,000원",
                formattedTotalPaymentAmount = "202,200원",
            ),
        onCouponClick = {},
    )
}
