package woowacourse.shopping.ui.payment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import woowacourse.shopping.constant.Format.formatDate
import woowacourse.shopping.constant.Format.formatPrice
import woowacourse.shopping.constant.ShoppingColor.APP_BAR_COLOR
import woowacourse.shopping.constant.ShoppingColor.PAYMENT_BUTTON_COLOR
import woowacourse.shopping.constant.ShoppingColor.PAYMENT_CARD_BORDER_COLOR
import woowacourse.shopping.constant.ShoppingColor.PAYMENT_DESCRIPTION_GRAY
import woowacourse.shopping.constant.ShoppingColor.PAYMENT_DIVIDER_COLOR
import woowacourse.shopping.constant.ShoppingColor.PAYMENT_SUMMARY_BACKGROUND_COLOR
import woowacourse.shopping.constant.ShoppingColor.PAYMENT_TITLE_GRAY
import woowacourse.shopping.domain.model.coupon.Coupon

@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel,
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
    onPayClick: () -> Unit = viewModel::onClickPay,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PaymentContent(
        uiState = uiState,
        modifier = modifier,
        onClose = onClose,
        onCouponClick = viewModel::selectCoupon,
        onPayClick = onPayClick,
    )
}

@Composable
private fun PaymentContent(
    uiState: PaymentUiState,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    onCouponClick: (Coupon?) -> Unit,
    onPayClick: () -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            PaymentTopAppBar(onClose = onClose)
        },
        bottomBar = {
            PaymentBottomBar(onPayClick = onPayClick)
        },
    ) { paddingValues ->
        when (uiState) {
            PaymentUiState.Loading -> {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                ) {
                    Text(text = "로딩 중...", color = Color.Gray)
                }
            }

            is PaymentUiState.Success -> {
                PaymentScreenContent(
                    uiState = uiState,
                    paddingValues = paddingValues,
                    onCouponClick = onCouponClick,
                )
            }
        }
    }
}

@Composable
private fun PaymentScreenContent(
    uiState: PaymentUiState.Success,
    paddingValues: PaddingValues,
    onCouponClick: (Coupon?) -> Unit,
) {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
                Text(
                    text = "적용 가능한 쿠폰",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "* 쿠폰은 1개만 적용 가능합니다.",
                    fontSize = 12.sp,
                    color = Color(PAYMENT_DESCRIPTION_GRAY),
                )
            }
        }

        items(uiState.availableCoupons, key = { it.code }) { coupon ->
            CouponCard(
                coupon = coupon,
                selected = uiState.selectedCoupon?.code == coupon.code,
                onClick = { onCouponClick(if (uiState.selectedCoupon?.code == coupon.code) null else coupon) },
            )
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 8.dp, color = Color(PAYMENT_DIVIDER_COLOR))
            SummarySection(uiState = uiState)
        }
    }
}

@Composable
private fun CouponCard(
    coupon: Coupon,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clickable { onClick() },
        border = BorderStroke(1.dp, Color(PAYMENT_CARD_BORDER_COLOR)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(4.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Checkbox(
                checked = selected,
                onCheckedChange = { onClick() },
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text(
                    text = coupon.description.ifBlank { coupon.code },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(PAYMENT_TITLE_GRAY),
                )
                Spacer(modifier = Modifier.height(8.dp))
                coupon.expireAt?.let {
                    Text(
                        text = "만료일: ${formatDate(it)}",
                        fontSize = 12.sp,
                        color = Color(PAYMENT_DESCRIPTION_GRAY),
                    )
                }
                coupon.minOrderAmount?.let {
                    Text(
                        text = "최소 주문 금액: ${formatPrice(it)}",
                        fontSize = 12.sp,
                        color = Color(PAYMENT_DESCRIPTION_GRAY),
                    )
                }
            }
        }
    }
}

@Composable
private fun SummarySection(uiState: PaymentUiState.Success) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color(PAYMENT_SUMMARY_BACKGROUND_COLOR))
                .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SummaryRow(label = "주문 금액", value = formatPrice(uiState.subtotal))
        SummaryRow(
            label = "쿠폰 할인 금액",
            value = if (uiState.couponDiscount > 0) "-${formatPrice(uiState.couponDiscount)}" else "0원",
        )
        SummaryRow(label = "배송비", value = formatPrice(uiState.shippingFee))

        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(thickness = 1.dp, color = Color(PAYMENT_DIVIDER_COLOR))
        Spacer(modifier = Modifier.height(4.dp))

        SummaryRow(
            label = "총 결제 금액",
            value = formatPrice(uiState.totalPrice),
            bold = true,
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    bold: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = if (bold) 18.sp else 16.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.SemiBold,
            color = Color.Black,
        )
        Text(
            text = value,
            fontSize = if (bold) 18.sp else 16.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.SemiBold,
            color = Color.Black,
        )
    }
}

@Composable
private fun PaymentBottomBar(onPayClick: () -> Unit) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
    ) {
        Button(
            onClick = onPayClick,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(PAYMENT_BUTTON_COLOR)),
            shape = RoundedCornerShape(0.dp),
        ) {
            Text(
                text = "결제하기",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentTopAppBar(onClose: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "결제하기",
                fontSize = 20.sp,
                color = Color.White,
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = Color.White,
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color(APP_BAR_COLOR),
                navigationIconContentColor = Color.White,
                titleContentColor = Color.White,
            ),
        windowInsets = WindowInsets(0, 0, 0, 0),
    )
}
