package woowacourse.shopping.ui.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OrderScreen(
    orderViewModel: OrderViewModel,
    onClickClose: () -> Unit,
    onOrderSuccess: () -> Unit,
) {
    val uiState by orderViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(orderViewModel.events) {
        orderViewModel.events.collect { event ->
            when (event) {
                OrderEvent.OrderSuccess -> onOrderSuccess()
            }
        }
    }

    Column {
        PaymentTopAppBar(onNavigateBack = onClickClose)
        OrderScreenContent(
            uiState = uiState,
            onCouponClick = orderViewModel::selectCoupon,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        )
        PaymentBottomBar(orderViewModel::order)
    }
}

@Composable
fun OrderScreenContent(
    uiState: OrderUiState,
    onCouponClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "적용가능한 쿠폰",
                    color = Color(0xff333333),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.W700,
                )
                Text(
                    text = "* 쿠폰은 1개만 적용 가능합니다.",
                    color = Color(0xff555555),
                    fontSize = 12.sp,
                )
            }
        }
        items(items = uiState.coupons, key = { it.id }) { coupon ->
            CouponCard(
                coupon = coupon,
                onClick = { onCouponClick(coupon.id) },
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
            )
        }

        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 18.dp),
                thickness = 7.dp,
                color = Color(0xffEBEBEB),
            )
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PriceRow("주문 금액", uiState.totalPrice, modifier = Modifier.fillMaxWidth())
                PriceRow("쿠폰 할인 금액", uiState.discountAmount, modifier = Modifier.fillMaxWidth())
                PriceRow("배송비", uiState.shippingFee, modifier = Modifier.fillMaxWidth())
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 18.dp),
                thickness = 7.dp,
                color = Color(0xffEBEBEB),
            )
            PriceRow(
                "총 결제 금액",
                uiState.amountToPay,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
fun PriceRow(
    type: String,
    price: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = type,
            color = Color(0xff333333),
            fontSize = 18.sp,
            fontWeight = FontWeight.W700,
        )
        Text(
            text = price,
            color = Color(0xff333333),
            fontSize = 18.sp,
            fontWeight = FontWeight.W500,
        )
    }
}

@Composable
fun PaymentBottomBar(onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color(0xff04C09E))
                .clickable { onClick() }
                .padding(10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "결제하기",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.W700,
        )
    }
}

@Composable
private fun PaymentTopAppBar(onNavigateBack: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color(0xff555555))
                .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "뒤로가기",
            modifier =
                Modifier
                    .padding(10.dp)
                    .clickable { onNavigateBack() },
            tint = Color.White,
        )
        Text(
            text = "결제하기",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.W500,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OrderScreenPreview() {
    OrderScreenContent(
        uiState =
            OrderUiState(
                totalPrice = "100,000원",
                discountAmount = "10,000원",
                shippingFee = "30,000원",
                amountToPay = "90,000원",
                coupons =
                    listOf(
                        CouponUiModel(
                            id = 1,
                            description = "신규 회원",
                            expiredDate = "2023.06.30",
                            isSelected = true,
                        ),
                        CouponUiModel(
                            id = 2,
                            description = "신규 회원",
                            expiredDate = "2023.06.30",
                            isSelected = false,
                        ),
                    ),
            ),
        onCouponClick = {},
        modifier =
            Modifier
                .fillMaxSize(),
    )
}
