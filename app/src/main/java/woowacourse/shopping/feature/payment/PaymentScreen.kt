package woowacourse.shopping.feature.payment

import android.widget.Toast
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.feature.common.component.CommonAppBar
import woowacourse.shopping.feature.payment.component.CouponCard
import woowacourse.shopping.feature.payment.component.PurchaseButton
import woowacourse.shopping.feature.payment.component.PurchaseInfo

@Composable
fun PaymentScreen(
    cartContentIds: List<Long>,
    onCloseClick: () -> Unit,
    onPaymentClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PaymentViewModel = viewModel(factory = PaymentViewModel.Factory),
) {
    val currentContext = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.couponEvent.collect { event ->
            when (event) {
                is CouponEvent.Success -> {
                    Toast.makeText(currentContext, event.message, Toast.LENGTH_SHORT).show()
                }
                is CouponEvent.Failed -> {
                    Toast.makeText(currentContext, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.paymentEvent.collect { event ->
            when (event) {
                is PaymentEvent.Success -> {
                    Toast.makeText(currentContext, event.message, Toast.LENGTH_SHORT).show()
                    onPaymentClick()
                }
                is PaymentEvent.Failed -> {
                    Toast.makeText(currentContext, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadCart(cartContentIds)
        viewModel.cancelPaymentAlarm()
        viewModel.startPaymentAlarm(cartContentIds)
    }

    Scaffold(
        containerColor = Color.White,
        modifier =
            modifier
                .fillMaxSize(),
        topBar = {
            CommonAppBar(
                title = "결제하기",
                onCloseClick = onCloseClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(3f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text("적용 가능한 쿠폰", fontWeight = FontWeight.W700, fontSize = 24.sp)
                Spacer(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                )
                Text(
                    "* 쿠폰은 1개만 적용 가능합니다.",
                    fontWeight = FontWeight.W500,
                    fontSize = 14.sp,
                    color = Color.Gray,
                )

                LazyColumn(
                    modifier = Modifier.padding(horizontal = 18.dp),
                ) {
                    items(uiState.couponList) {
                        CouponCard(
                            title = it.title,
                            year = it.year,
                            month = it.month,
                            day = it.day,
                            minimumPrice = it.minimumPrice,
                            checked = uiState.couponCheckMap[it.code] ?: false,
                            onCheckedChange = {
                                viewModel.couponCheck(it.code)
                            },
                        )
                    }
                }
            }

            HorizontalDivider(color = Color.LightGray, thickness = 7.dp)

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
            ) {
                PurchaseInfo(
                    infoText = "주문 금액",
                    price = uiState.totalPrice,
                )
                PurchaseInfo(
                    infoText = "쿠폰 할인 금액",
                    price = uiState.couponDiscountPrice * -1,
                )
                PurchaseInfo(
                    infoText = "배송비",
                    price = uiState.shippingFee,
                )
            }

            HorizontalDivider(color = Color.LightGray, thickness = 7.dp)

            PurchaseInfo(
                modifier =
                    Modifier
                        .weight(1f),
                infoText = "총 결제 금액",
                price = uiState.totalPaymentPrice,
            )

            PurchaseButton(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(78.dp)
                        .background(Color(0xff555555)),
                onClick = {
                    viewModel.order(cartContentIds)
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentScreenPreview() {
    PaymentScreen(
        onCloseClick = {},
        onPaymentClick = {},
        cartContentIds = emptyList(),
    )
}
