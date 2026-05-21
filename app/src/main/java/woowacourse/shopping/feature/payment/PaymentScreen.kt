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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.feature.common.component.CommonAppBar
import woowacourse.shopping.feature.payment.component.CouponCard
import woowacourse.shopping.feature.payment.component.PurchaseButton
import woowacourse.shopping.feature.payment.component.PurchaseInfo

@Composable
fun PaymentScreen(
    onCloseClick: () -> Unit,
    onPaymentClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentContext = LocalContext.current

    Scaffold(
        containerColor = Color.White,
        modifier =
            modifier
                .fillMaxSize(),
        topBar = {
            CommonAppBar(
                title = "결제하기",
                onCloseClick = onCloseClick
            )
        },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f),
                verticalArrangement = Arrangement.Center,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp)
                ) {
                    Text("적용 가능한 쿠폰", fontWeight = FontWeight.W700, fontSize = 24.sp)
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                    )
                    Text(
                        "* 쿠폰은 1개만 적용 가능합니다.",
                        fontWeight = FontWeight.W500,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                CouponCard(
                    title = "5,000원 할인 쿠폰",
                    year = 2026,
                    month = 5,
                    day = 30,
                    minimumPrice = 100000,
                    checked = false,
                    onCheckedChange = {}
                )
                CouponCard(
                    title = "2개 구매 시 1개 무료 쿠폰",
                    year = 2026,
                    month = 5,
                    day = 30,
                    minimumPrice = 0,
                    checked = false,
                    onCheckedChange = {}
                )
            }

            HorizontalDivider(color = Color.LightGray, thickness = 7.dp)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                PurchaseInfo(
                    infoText = "주문 금액",
                    price = 204200
                )
                PurchaseInfo(
                    infoText = "쿠폰 할인 금액",
                    price = -5000
                )
                PurchaseInfo(
                    infoText = "배송비",
                    price = 3000
                )
            }

            HorizontalDivider(color = Color.LightGray, thickness = 7.dp)

            PurchaseInfo(
                modifier = Modifier
                    .weight(1f),
                infoText = "총 결제 금액",
                price = 202200
            )

            PurchaseButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(78.dp)
                    .background(Color(0xff555555)),
                onClick = {
                    onPaymentClick()
                    Toast.makeText(currentContext, "주문이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentScreenPreview() {
    PaymentScreen(
        onCloseClick = {},
        onPaymentClick = {}
    )
}
