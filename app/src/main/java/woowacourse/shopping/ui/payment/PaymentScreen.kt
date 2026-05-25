package woowacourse.shopping.ui.payment

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.component.ShoppingAppBar
import woowacourse.shopping.ui.component.ShoppingButton
import woowacourse.shopping.ui.component.ShoppingCheckBox
import woowacourse.shopping.ui.model.UiCoupon
import woowacourse.shopping.ui.model.UiPaymentPrice
import woowacourse.shopping.ui.theme.Gary30
import woowacourse.shopping.ui.theme.Gray40
import woowacourse.shopping.ui.theme.Gray50
import woowacourse.shopping.ui.theme.Gray70
import woowacourse.shopping.ui.util.formattedDate
import woowacourse.shopping.ui.util.formattedPrice
import java.time.LocalDateTime

@Composable
fun PaymentScreen(
    onBackClick: () -> Unit,
    onCheckedChange: (Long) -> Unit,
    onPaymentClick: () -> Unit,
    coupons: List<UiCoupon>,
    paymentPrice: UiPaymentPrice,
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
        modifier = Modifier.systemBarsPadding(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding),
        ) {
            CouponTitle(
                modifier =
                    Modifier.padding(
                        start = 18.dp,
                        end = 18.dp,
                        top = 30.dp,
                        bottom = 20.dp,
                    ),
            )

            coupons.forEach {
                CouponCard(
                    isChecked = it.isChecked,
                    onCheckedChange = { onCheckedChange(it.id) },
                    title = it.title,
                    expiryDateTime = it.expiryDateTime,
                    minimumPrice = it.minimumPrice,
                    modifier = Modifier.padding(horizontal = 18.dp),
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(thickness = 7.dp, color = Gary30)

            PaymentSummary(
                totalPrice = paymentPrice.totalPrice,
                couponDiscountPrice = paymentPrice.couponDiscountPrice,
                deliveryFee = paymentPrice.deliveryFee,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
            )

            HorizontalDivider(thickness = 7.dp, color = Gary30)

            Spacer(modifier = Modifier.height(21.dp))

            PaymentText(
                title = "총 결제 금액",
                price = paymentPrice.paymentPrice,
                modifier = Modifier.padding(horizontal = 18.dp),
            )

            Spacer(modifier = Modifier.height(76.dp))

            ShoppingButton(
                text = "결제하기",
                onClick = onPaymentClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CouponTitle(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "적용 가능한 쿠폰",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Gray70,
        )

        Text(
            text = "* 쿠폰은 1개만 적용 가능합니다.",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Gray50,
        )
    }
}

@Composable
private fun CouponCard(
    isChecked: Boolean,
    onCheckedChange: () -> Unit,
    title: String,
    expiryDateTime: LocalDateTime,
    modifier: Modifier = Modifier,
    minimumPrice: Long?,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .border(1.dp, Gray40, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 18.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShoppingCheckBox(
                onCheckedChange = onCheckedChange,
                isChecked = isChecked,
            )

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Gray50,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "만료일 : ${expiryDateTime.formattedDate()}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
        )

        if (minimumPrice != null) {
            Text(
                text = "최소 주문 금액 : ${formattedPrice(minimumPrice)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun PaymentSummary(
    totalPrice: Long,
    couponDiscountPrice: Long,
    deliveryFee: Long,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PaymentText(
            title = "주문 금액",
            price = totalPrice,
        )

        PaymentText(
            title = "쿠폰 할인 금액",
            price = couponDiscountPrice,
        )

        PaymentText(
            title = "배송비",
            price = deliveryFee,
        )
    }
}

@Composable
private fun PaymentText(
    title: String,
    price: Long,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Gray70,
        )

        Text(
            text = formattedPrice(price),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Gray70,
        )
    }
}

@Preview
@Composable
private fun PaymentScreenPreview() {
    PaymentScreen(
        onBackClick = { },
        onCheckedChange = { },
        coupons =
            listOf(
                UiCoupon(
                    id = 1,
                    title = "랜덤 할인",
                    expiryDateTime = LocalDateTime.of(2026, 5, 25, 11, 59),
                    isChecked = true,
                    minimumPrice = 100_000,
                ),
                UiCoupon(
                    id = 2,
                    title = "랜덤 할인",
                    expiryDateTime = LocalDateTime.of(2026, 5, 25, 11, 59),
                    isChecked = true,
                ),
            ),
        paymentPrice =
            UiPaymentPrice(
                totalPrice = 1_000,
                deliveryFee = 3_000,
                couponDiscountPrice = -1_000,
                paymentPrice = 3_000,
            ),
        onPaymentClick = { },
    )
}
