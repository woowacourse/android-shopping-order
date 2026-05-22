package woowacourse.shopping.presentation.payment

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import kotlinx.collections.immutable.toImmutableList
import woowacourse.shopping.presentation.payment.model.CouponUiModel
import woowacourse.shopping.presentation.payment.ui.PaymentScreen

@Composable
fun PaymentRoute(
    orderAmount: Long,
    navController: NavController,
) {
    PaymentScreen(
        orderAmount = orderAmount,
        onBackClick = navController::popBackStack,
        onPayClick = {},
        onSelectCoupon = {},
        coupons = emptyList<CouponUiModel>().toImmutableList(),
    )
}
