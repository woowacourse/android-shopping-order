package woowacourse.shopping.ui.order

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OrderRouteScreen(
    orderViewModel: OrderViewModel,
    onBackClick: () -> Unit,
    onOrderCompleted: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by orderViewModel.uiState.collectAsStateWithLifecycle()

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

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        OrderScreen(
            onBackClick = onBackClick,
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
