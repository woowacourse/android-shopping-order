package woowacourse.shopping.ui.payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.CouponBenefit
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

@Composable
fun PaymentScreen(
    couponList: List<Coupon>,
    selectedCouponId: Long?,
    shoppingCartTotalPrice: String,
    couponDiscountPrice: String,
    deliveryPrice: String,
    totalPrice: String,
    isPaymentReminderEnabled: Boolean,
    onBackClick: () -> Unit,
    onCouponCheckedChange: (Long, Boolean) -> Unit,
    onPaymentReminderEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    bottomContent: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = {
            PaymentTopBar(
                onBackClick = onBackClick,
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.payment_reminder_toggle_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Switch(
                        checked = isPaymentReminderEnabled,
                        onCheckedChange = onPaymentReminderEnabledChange,
                    )
                }
                Text(
                    text = "적용 가능한 쿠폰",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W700,
                )
                Text(
                    text = "* 쿠폰은 1개만 적용 가능합니다.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                )
                Box(
                    modifier =
                        Modifier
                            .height(250.dp)
                            .verticalScroll(rememberScrollState()),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        couponList.forEach { coupon ->
                            CouponCard(
                                couponTitle = coupon.description,
                                expirationDate = coupon.expirationDate,
                                minimumOrderAmount = coupon.minimumOrderAmountText(),
                                checked = selectedCouponId == coupon.id,
                                onCheckedChange = { isChecked ->
                                    onCouponCheckedChange(coupon.id, isChecked)
                                },
                            )
                        }
                    }
                }
            }
            HorizontalDivider(thickness = 4.dp)
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SummaryRow(
                    title = "주문 금액",
                    price = shoppingCartTotalPrice,
                )
                SummaryRow(
                    title = "쿠폰 할인 금액",
                    price = couponDiscountPrice,
                )
                SummaryRow(
                    title = "배송비",
                    price = deliveryPrice,
                )
            }
            HorizontalDivider(thickness = 4.dp)
            SummaryRow(
                title = "총 결제 금액",
                price = totalPrice,
                modifier = Modifier.padding(16.dp).weight(1f),
            )
            bottomContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.payment_top_bar_title),
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Image(
                    painter = painterResource(R.drawable.back_icon),
                    contentDescription = "결제하기",
                    modifier = Modifier.size(16.dp),
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        modifier = modifier,
    )
}

@Composable
private fun CouponCard(
    couponTitle: String,
    expirationDate: String,
    minimumOrderAmount: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(4.dp))
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline)
                .padding(18.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
            Text(
                text = couponTitle,
                fontSize = 18.sp,
                fontWeight = FontWeight.W700,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "만료일:",
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
            )
            Text(
                text = expirationDate,
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
            )
        }
        if (minimumOrderAmount != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "최소주문금액:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                )
                Text(
                    text = minimumOrderAmount,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    title: String,
    price: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.W700,
        )
        Text(
            text = price,
            fontSize = 18.sp,
            fontWeight = FontWeight.W700,
        )
    }
}


@Composable
fun PaymentButton(
    onPaymentButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onPaymentButtonClick,
        modifier =
            modifier.fillMaxWidth(),
        shape = RectangleShape,
    ) {
        Text(text = "결제하기")
    }
}

private fun Coupon.minimumOrderAmountText(): String? =
    when (val benefit = benefit) {
        is CouponBenefit.AmountDiscount ->
            DecimalFormat(PRICE_FORMAT).format(benefit.minimumOrderAmount)

        is CouponBenefit.FreeShipping ->
            DecimalFormat(PRICE_FORMAT).format(benefit.minimumOrderAmount)

        else -> null
    }

@Preview(showBackground = true)
@Composable
private fun PaymentScreenPreview() {
    AndroidShoppingTheme {
        PaymentScreen(
            couponList =
                listOf(
                    Coupon(
                        id = 1,
                        code = "FIXED5000",
                        description = "5000원 할인 쿠폰",
                        expirationDate = "2024.11.30",
                        benefit = CouponBenefit.AmountDiscount(5000, 100000),
                    ),
                    Coupon(
                        id = 2,
                        code = "FREESHIPPING",
                        description = "무료 배송 쿠폰",
                        expirationDate = "2024.08.31",
                        benefit = CouponBenefit.FreeShipping(50000),
                    ),
                ),
            selectedCouponId = 1L,
            shoppingCartTotalPrice = "120,000원",
            couponDiscountPrice = "5,000원",
            deliveryPrice = "3,000원",
            totalPrice = "118,000원",
            isPaymentReminderEnabled = true,
            onBackClick = {},
            onCouponCheckedChange = { _, _ -> },
            onPaymentReminderEnabledChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CouponCardPreview() {
    AndroidShoppingTheme {
        CouponCard(
            couponTitle = "5000원 할인 쿠폰",
            expirationDate = "2024.11.30",
            minimumOrderAmount = "100,000원",
            checked = true,
            onCheckedChange = {},
        )
    }
}
private const val PRICE_FORMAT: String = "#,###원"
