package woowacourse.shopping.ui.payment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import woowacourse.shopping.R
import woowacourse.shopping.ui.common.component.Header
import woowacourse.shopping.ui.payment.component.PaymentBody
import woowacourse.shopping.ui.payment.uistate.PaymentUiState

@Composable
fun PaymentScreen(
    uiState: PaymentUiState,
    onBackClick: () -> Unit,
    onCouponCheckedChange: (Long, Boolean) -> Unit,
    onPaymentClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Header(
            text = stringResource(R.string.payment_title),
            onBackClick = onBackClick,
        )
        PaymentBody(
            uiState = uiState,
            onCouponCheckedChange = onCouponCheckedChange,
            onPaymentClick = onPaymentClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
