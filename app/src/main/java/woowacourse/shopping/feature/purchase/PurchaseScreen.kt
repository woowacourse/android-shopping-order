package woowacourse.shopping.feature.purchase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.constants.MockData
import woowacourse.shopping.feature.purchase.component.CouponElement
import woowacourse.shopping.feature.purchase.component.PriceLine
import woowacourse.shopping.feature.purchase.component.PurchaseAppBar

@Composable
fun PurchaseScreen(
    activityFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PurchaseViewModel = viewModel(factory = PurchaseViewModel.Factory),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.initialLoading()
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//    ErrorDialog(
//        error = uiState.error,
//        onDismiss = {
//            viewModel.dismissError()
//            activityFinish()
//        },
//        onRetry = {
//            viewModel.dismissError()
//            viewModel.initialLoading()
//        },
//    )

    PurchaseScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        selectedCouponId = uiState.selectedCouponId,
        onCheckClick = viewModel::couponSelect,
        onCloseClick = activityFinish,
        modifier = modifier,

    )
}

@Composable
fun PurchaseScreenContent(
    uiState: PurchaseUiState,
    selectedCouponId: String?,
    onCheckClick: (String) -> Unit,
    onCloseClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White,
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            PurchaseAppBar(onCloseClick = onCloseClick)
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
            ) {
                Spacer(Modifier.height(30.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("적용 가능한 쿠폰", fontWeight = FontWeight.W700, fontSize = 24.sp)
                    Spacer(Modifier.height(5.dp))
                    Text("*쿠폰은 1개만 적용 가능합니다.", fontWeight = FontWeight.W400, fontSize = 12.sp, color = Color(0xff555555))
                }
                LazyColumn(
                    modifier = Modifier
                        .height(250.dp)
                        .padding(vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(
                        key = { it.id },
                        items = uiState.couponUiModels,
                    ) {
                        CouponElement(
                            model = it,
                            selectedCouponId = selectedCouponId,
                            onCheckClick = onCheckClick,
                        )
                    }
                }
            }
            HorizontalDivider(
                thickness = 6.dp,
                color = Color(0xffebebeb),
                modifier = Modifier.padding(vertical = 10.dp),
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
            ) {
                PriceLine("주문 금액", uiState.originalPrice)
                PriceLine("쿠폰 할인 금액", uiState.couponDiscountPrice)
                PriceLine("배송비", uiState.shippingPrice)
            }
            HorizontalDivider(
                thickness = 6.dp,
                color = Color(0xffebebeb),
                modifier = Modifier.padding(vertical = 10.dp),
            )
            PriceLine(
                header = "총 결제 금액",
                price = uiState.totalDiscountedPrice,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
            )
            Spacer(modifier.weight(1f))
            TextButton(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color(0xff04c09e)),
            ) {
                Text("결제하기", fontWeight = FontWeight.W700, color = Color(0xffffffff))
            }
        }
    }
}

@Preview
@Composable
private fun PurchaseScreenContentPreview() {
    PurchaseScreenContent(
        uiState = PurchaseUiState(
            couponUiModels = MockData.MOCK_COUPONS,
        ),
        onCloseClick = { },
        snackbarHostState = SnackbarHostState(),
        selectedCouponId = "1",
        onCheckClick = { },
    )
}
