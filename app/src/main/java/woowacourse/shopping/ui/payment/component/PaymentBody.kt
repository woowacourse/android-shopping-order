package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.common.theme.PrimaryColor
import woowacourse.shopping.ui.payment.PaymentUiState

@Composable
fun PaymentBody(
    uiState: PaymentUiState,
    modifier: Modifier = Modifier,
    onCouponSelected: (Long) -> Unit,
    onPayClick: () -> Unit,
) {
    Column(modifier = modifier.background(Color(0xFFF6F6F6))) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
        ) {
            OrderItemsSection(items = uiState.items)

            Spacer(modifier = Modifier.height(8.dp))

            CouponSection(
                coupons = uiState.coupons,
                selectedCouponId = uiState.selectedCouponId,
                onCouponSelected = onCouponSelected,
            )

            Spacer(modifier = Modifier.height(8.dp))

            PaymentSummarySection(
                orderAmount = uiState.orderAmount,
                discountAmount = uiState.discountAmount,
                shippingFee = uiState.shippingFee,
                totalAmount = uiState.totalAmount,
            )
        }

        PayButton(
            totalAmount = uiState.totalAmount,
            enabled = uiState.items.isNotEmpty(),
            onClick = onPayClick,
        )
    }
}

@Composable
private fun PayButton(
    totalAmount: Long,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    contentColor = Color.White,
                ),
        ) {
            Text(
                text = "${totalAmount.formatPrice()} 결제하기",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

internal fun Long.formatPrice(): String = "%,d원".format(this)
