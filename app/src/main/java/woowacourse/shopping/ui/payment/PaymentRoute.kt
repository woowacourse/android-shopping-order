@file:Suppress("FunctionName")

package woowacourse.shopping.ui.payment

import android.icu.text.DecimalFormat
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.R
import woowacourse.shopping.data.remote.retrofit.dto.OrderInfo
import woowacourse.shopping.di.AppViewModelFactory
import woowacourse.shopping.ui.cart.ShoppingCartViewModel
import woowacourse.shopping.ui.order.OrderViewModel

@Composable
fun PaymentRouteContent(
    viewModelFactory: AppViewModelFactory,
    sharedViewModelStoreOwner: ViewModelStoreOwner,
    selectedProductIds: Set<Long>,
    onNavigateBack: () -> Unit,
    onOrderCompleted: () -> Unit,
) {
    val paymentViewModel: PaymentViewModel =
        viewModel(
            viewModelStoreOwner = sharedViewModelStoreOwner,
            factory = viewModelFactory,
        )
    val shoppingCartViewModel: ShoppingCartViewModel =
        viewModel(
            viewModelStoreOwner = sharedViewModelStoreOwner,
            factory = viewModelFactory,
        )
    val orderViewModel: OrderViewModel =
        viewModel(
            viewModelStoreOwner = sharedViewModelStoreOwner,
            factory = viewModelFactory,
        )

    val paymentUiState by paymentViewModel.uiState.collectAsStateWithLifecycle()
    val cartUiState by shoppingCartViewModel.uiState.collectAsStateWithLifecycle()

    val selectedCartItemIds =
        cartUiState
            .shoppingCartItems
            .filter { shoppingCartItem -> shoppingCartItem.product.id in selectedProductIds }
            .map { shoppingCartItem -> shoppingCartItem.getId() }

    LaunchedEffect(selectedProductIds) {
        paymentViewModel.initialize(selectedProductIds = selectedProductIds)
    }

    LifecycleResumeEffect(Unit) {
        paymentViewModel.requestPaymentData()
        onPauseOrDispose { }
    }

    PaymentScreen(
        couponList = paymentUiState.coupons,
        selectedCouponId = paymentUiState.selectedCouponId,
        shoppingCartTotalPrice = formatPrice(paymentUiState.subtotalPrice),
        couponDiscountPrice = formatPrice(paymentUiState.couponDiscountPrice),
        deliveryPrice = formatPrice(paymentUiState.deliveryPrice),
        totalPrice = formatPrice(paymentUiState.totalPrice),
        onBackClick = onNavigateBack,
        onCouponCheckedChange = { couponId, isChecked ->
            paymentViewModel.selectCoupon(
                couponId =
                    if (isChecked) {
                        couponId
                    } else {
                        null
                    },
            )
        }
    ) {
        PaymentButton(
            onPaymentButtonClick = {
                if (selectedCartItemIds.isEmpty()) return@PaymentButton

                orderViewModel.order(
                    orderInfo = OrderInfo(cartItemIds = selectedCartItemIds),
                    onSuccess = {
                        shoppingCartViewModel.requestCartItems(force = true)
                        onOrderCompleted()
                    },
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun formatPrice(price: Int): String =
    DecimalFormat(stringResource(R.string.price_format_pattern)).format(price)
