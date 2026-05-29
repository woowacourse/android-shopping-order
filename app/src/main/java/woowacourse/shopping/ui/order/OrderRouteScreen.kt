package woowacourse.shopping.ui.order

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import woowacourse.shopping.domain.model.cart.SelectedCartOrder

@Composable
fun OrderRouteScreen(
    orderViewModel: OrderViewModel,
    restorePendingOrder: Boolean,
    restorePendingOrderSession: () -> SelectedCartOrder?,
    onBackClick: () -> Unit,
    onPendingOrderUnavailable: () -> Unit,
    onOrderCompleted: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by orderViewModel.uiState.collectAsStateWithLifecycle()
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(restorePendingOrder) {
        if (restorePendingOrder && !uiState.hasPendingOrder) {
            val restoredOrder = restorePendingOrderSession()
            if (restoredOrder == null) {
                onPendingOrderUnavailable()
            } else {
                orderViewModel.loadOrder(restoredOrder)
            }
        }
    }

    LaunchedEffect(uiState.isReminderEnabled, uiState.hasPendingOrder) {
        if (!uiState.isReminderEnabled || !uiState.hasPendingOrder) return@LaunchedEffect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(orderViewModel) {
        orderViewModel.events.collect { event ->
            when (event) {
                OrderEvent.OrderCompleted -> {
                    Toast.makeText(context, "주문이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    onOrderCompleted()
                }

                is OrderEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    BackHandler(enabled = !uiState.isOrdering) {
        onBackClick()
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        OrderScreen(
            onBackClick = {
                onBackClick()
            },
            modifier = Modifier.padding(innerPadding),
            coupons = uiState.coupons,
            priceSummary = uiState.priceSummary,
            isOrdering = uiState.isOrdering,
            isPaymentEnabled = uiState.isPaymentEnabled,
            isNetworkConnected = uiState.isNetworkConnected,
            onCouponCheckedChange = orderViewModel::toggleCouponSelection,
            onPaymentClick = orderViewModel::placeOrder,
        )
    }
}
