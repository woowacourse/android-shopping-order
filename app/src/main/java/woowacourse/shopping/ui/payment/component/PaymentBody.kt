package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.R
import woowacourse.shopping.ui.common.component.ActionButton
import woowacourse.shopping.ui.payment.uistate.CouponUiModel
import woowacourse.shopping.ui.payment.uistate.PaymentUiState

@Composable
fun PaymentBody(
    uiState: PaymentUiState,
    onCouponCheckedChange: (Long, Boolean) -> Unit,
    onPaymentClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        CouponList(
            coupons = uiState.coupons,
            onCouponCheckedChange = onCouponCheckedChange,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .weight(1f),
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            PaymentHorizontalDivider()

            PaymentAmountSummary(
                orderAmount = uiState.orderAmount,
                couponDiscountAmount = uiState.couponDiscountAmount,
                deliveryFee = uiState.deliveryFee,
                totalPaymentAmount = uiState.totalPaymentAmount,
                modifier = Modifier.fillMaxWidth(),
            )

            ActionButton(
                text = stringResource(R.string.payment_button),
                onClick = onPaymentClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 56.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentBodyPreview() {
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

    val uiState =
        PaymentUiState(
            coupons = coupons,
            orderAmount = "204,200원",
            couponDiscountAmount = "-5,000원",
            deliveryFee = "3,000원",
            totalPaymentAmount = "202,200원",
        )

    PaymentBody(
        uiState = uiState,
        onCouponCheckedChange = { _, _ -> },
        onPaymentClick = {},
    )
}
