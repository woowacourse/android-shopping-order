package woowacourse.shopping.ui.payment

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.di.appContainer
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.coupon.Coupon
import woowacourse.shopping.ui.common.component.ShoppingLoading
import woowacourse.shopping.ui.payment.component.PaymentBody
import woowacourse.shopping.ui.payment.component.PaymentBottomBar
import woowacourse.shopping.ui.payment.component.PaymentTopBar
import java.time.LocalDate

@Composable
fun PaymentScreen(
    onBackClick: () -> Unit,
    onPayClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PaymentViewModel =
        viewModel(
            factory =
                PaymentViewModel.provideFactory(
                    container = appContainer(),
                ),
        ),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.onScreenEnter()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    PaymentScreen(
        selectedCouponId = state.selectedCouponId,
        coupons = state.availableCoupons,
        subtotal = state.payment.subtotal.value,
        couponDiscount = state.payment.couponDiscount.value,
        shippingFee = state.payment.shippingFee.value,
        finalAmount = state.payment.finalAmount.value,
        modifier = modifier,
        onBackClick = onBackClick,
        onCouponSelected = { viewModel.updateSelectedId(it) },
        onPayClick = {
            viewModel.pay()
            onPayClick()
        },
    )

    if (state.isLoading) ShoppingLoading()
}

@Composable
fun PaymentScreen(
    selectedCouponId: Long?,
    coupons: List<Coupon>,
    subtotal: Long,
    couponDiscount: Long,
    shippingFee: Long,
    finalAmount: Long,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onCouponSelected: (Long) -> Unit,
    onPayClick: () -> Unit,
) {
    Column(modifier = modifier) {
        PaymentTopBar(
            modifier = Modifier.fillMaxWidth(),
            onBackClick = onBackClick,
        )

        PaymentBody(
            selectedCouponId = selectedCouponId,
            coupons = coupons,
            originalPrice = subtotal,
            discountPrice = couponDiscount,
            deliveryFee = shippingFee,
            totalPrice = finalAmount,
            modifier = Modifier.weight(1f),
            onCouponSelected = onCouponSelected,
        )

        PaymentBottomBar(
            modifier = Modifier.fillMaxWidth(),
            onClick = onPayClick,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentScreenPreview() {
    val coupons =
        listOf(
            Coupon.FreeShipping(
                id = 1,
                code = "FREESHIPPING",
                description = "5만원 이상 구매 시 무료 배송",
                expirationDate = LocalDate.of(2026, 8, 31),
                minimumAmount = Money(50000),
            ),
            Coupon.BuyXGetY(
                id = 2,
                code = "",
                description = "2개 구매 시 1개 무료 쿠폰",
                expirationDate = LocalDate.of(2026, 8, 31),
                buyQuantity = 3,
                getQuantity = 1,
            ),
            Coupon.FreeShipping(
                id = 1,
                code = "FREESHIPPING",
                description = "5만원 이상 구매 시 무료 배송",
                expirationDate = LocalDate.of(2026, 8, 31),
                minimumAmount = Money(50000),
            ),
        )
    PaymentScreen(
        selectedCouponId = 2,
        coupons = coupons,
        subtotal = 20000,
        couponDiscount = -1000,
        shippingFee = 3000,
        finalAmount = 22000,
        modifier = Modifier,
        onBackClick = {},
        onCouponSelected = {},
        onPayClick = {},
    )
}
