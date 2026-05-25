package woowacourse.shopping.ui.payment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import woowacourse.shopping.core.designsystem.component.layout.CommonFrame
import woowacourse.shopping.ui.payment.component.PaymentBody
import woowacourse.shopping.ui.payment.component.PaymentBottomBar
import woowacourse.shopping.ui.payment.component.PaymentHeader
import woowacourse.shopping.ui.payment.uimodel.CouponUiModel

@Composable
fun PaymentScreen(
    uiState: PaymentUiState,
    onBackClick: () -> Unit,
    onCouponClick: (String) -> Unit,
    onPaymentClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        CommonFrame(
            headerContent = { PaymentHeader(onBackClick) },
            bodyContent = {
                PaymentBody(
                    uiState = uiState,
                    onCouponClick = onCouponClick,
                )
            },
            modifier = Modifier.weight(1f),
        )
        PaymentBottomBar(onPaymentClick = onPaymentClick)
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentScreenPreview() {
    PaymentScreen(
        uiState =
            PaymentUiState(
                coupons =
                    listOf(
                        CouponUiModel(
                            code = "FIXED5000",
                            title = "5,000원 할인 쿠폰",
                            expirationDate = "2024년 11월 30일",
                            minimumOrderAmount = "100,000원",
                        ),
                        CouponUiModel(
                            code = "BOGO",
                            title = "2개 구매 시 1개 무료 쿠폰",
                            expirationDate = "2024년 11월 30일",
                        ),
                    ),
                selectedCouponCode = "FIXED5000",
                formattedOrderAmount = "204,200원",
                formattedCouponDiscountAmount = "-5,000원",
                formattedDeliveryFee = "3,000원",
                formattedTotalPaymentAmount = "202,200원",
            ),
        onBackClick = {},
        onCouponClick = {},
        onPaymentClick = {},
    )
}
