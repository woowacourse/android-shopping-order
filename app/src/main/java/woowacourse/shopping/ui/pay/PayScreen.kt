package woowacourse.shopping.ui.pay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import woowacourse.shopping.ui.component.ShoppingAppBar
import woowacourse.shopping.ui.pay.component.CouponCard
import woowacourse.shopping.ui.theme.Gray40
import woowacourse.shopping.ui.theme.Gray50
import woowacourse.shopping.ui.util.formattedPrice

@Composable
fun PayScreen(
    uiState: PayUiState,
    onBackClick: () -> Unit,
    onPayClick: () -> Unit,
    onCouponClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            ShoppingAppBar(
                contents = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로 가기",
                        tint = Color.White,
                        modifier =
                            Modifier
                                .size(16.dp)
                                .clickable { onBackClick() },
                    )
                    Spacer(modifier = Modifier.width(21.dp))
                    Text(
                        text = "결제하기",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                    )
                },
            )
        },
        bottomBar = {
            PayBottomBar(
                onPayClick = onPayClick,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        modifier = modifier.systemBarsPadding(),
    ) { innerPadding ->
        when {
            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                )
            }

            else -> {
                PayContent(
                    uiState = uiState,
                    onCouponClick = onCouponClick,
                    modifier =
                        Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun PayContent(
    uiState: PayUiState,
    onCouponClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        CouponHeader(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 18.dp),
        )
        CouponListSection(
            coupons = uiState.coupons,
            onCouponClick = onCouponClick,
            modifier = Modifier.weight(1f),
        )
        PaymentInfoSection(
            uiState = uiState,
        )

    }
}

@Composable
private fun CouponHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = "적용 가능한 쿠폰",
            fontWeight = FontWeight.W700,
            fontSize = 24.sp,
            lineHeight = 36.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "* 쿠폰은 1개만 적용 가능합니다.",
            fontWeight = FontWeight.W400,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = Gray50,
        )
    }
}
@Composable
private fun CouponListSection(
    coupons: ImmutableList<CouponUiModel>,
    onCouponClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(
            items = coupons,
            key = { coupon -> coupon.id },
        ) { coupon ->
            CouponCard(
                coupon = coupon,
                onClick = { onCouponClick(coupon.id) },
                modifier =
                    Modifier
                        .padding(horizontal = 18.dp)
                        .padding(bottom = 10.dp),
            )
        }
    }
}

@Composable
private fun PaymentInfoSection(
    uiState: PayUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        HorizontalDivider(thickness = 6.dp, color = Gray40)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PaymentInfo(description = "주문 금액", value = formattedPrice(uiState.totalOrderPrice))
            PaymentInfo(
                description = "쿠폰 할인 금액",
                value = (if (uiState.discountAmount > 0) "-" else "") + formattedPrice(uiState.discountAmount)
            )
            PaymentInfo(description = "배송비", value = formattedPrice(uiState.shippingFee))
        }
        HorizontalDivider(thickness = 6.dp, color = Gray40)
        PaymentInfo(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
            description = "총 결제 금액",
            value = formattedPrice(uiState.finalPrice)
        )
    }
}

@Composable
private fun PaymentInfo(
    description: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = description,
            fontSize = 18.sp,
            fontWeight = FontWeight.W700,
            color = Color.Black,
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.W700,
            color = Color.Black,
        )
    }
}


@Preview
@Composable
private fun PayScreenPreview() {
    PayScreen(
        uiState =
            PayUiState(
                coupons =
                    persistentListOf(
                        CouponUiModel(
                            id = "1",
                            code = "FIXED5000",
                            description = "5,000원 할인 쿠폰",
                            detail = "30,000원 이상 구매 시 사용 가능",
                            expirationDate = "만료일 2026년 12월 31일까지",
                            discountAmount = "5,000원 할인",
                        ),
                    ),
                totalOrderPrice = 30000,
                discountAmount = 5000,
                shippingFee = 3000,
                finalPrice = 28000,
            ),
        onBackClick = {},
        onPayClick = {},
        onCouponClick = {},
    )
}
