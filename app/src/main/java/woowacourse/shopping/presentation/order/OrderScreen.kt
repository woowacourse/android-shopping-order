package woowacourse.shopping.presentation.order

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.R
import woowacourse.shopping.presentation.common.components.ShoppingAppBar
import woowacourse.shopping.presentation.order.components.CouponItem
import woowacourse.shopping.presentation.order.model.CouponUiModel
import woowacourse.shopping.ui.theme.Gray30
import woowacourse.shopping.ui.theme.Gray40
import woowacourse.shopping.ui.theme.Green40
import woowacourse.shopping.util.formattedPrice

@Composable
fun OrderScreen(
    productIds: List<Long>,
    onBackClick: () -> Unit,
    onOrderSuccess: () -> Unit,
    onEnterOrder: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrderViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        onEnterOrder()
        viewModel.initializePaymentItems(productIds)
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.event.collect { event ->
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                when (event) {
                    is OrderEvent.Success -> {
                        onOrderSuccess()
                    }
                    is OrderEvent.Fail -> {}
                }
            }
        }
    }

    OrderContent(
        coupons = uiState.coupons,
        selectedCoupon = uiState.selectedCoupon,
        totalPrice = uiState.totalPrice,
        discountAmount = uiState.discountAmount,
        deliveryFee = uiState.deliveryFee,
        finalPrice = uiState.finalPrice,
        onOrderClick = viewModel::orderCartItems,
        onBackClick = onBackClick,
        onCouponSelect = { viewModel.selectCoupon(it) },
        modifier = modifier,
    )
}

@Composable
fun OrderContent(
    coupons: List<CouponUiModel>,
    selectedCoupon: CouponUiModel?,
    totalPrice: Long,
    discountAmount: Long,
    deliveryFee: Long,
    finalPrice: Long,
    onOrderClick: () -> Unit,
    onBackClick: () -> Unit,
    onCouponSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        containerColor = Gray30,
        topBar = {
            ShoppingAppBar(
                contents = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
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
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .height(56.dp)
                        .background(Green40)
                        .clickable { onOrderClick() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "결제하기",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        modifier = modifier.statusBarsPadding(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            CouponSection(
                coupons = coupons,
                selectedCoupon = selectedCoupon,
                onCouponSelect = onCouponSelect,
            )

            HorizontalDivider(thickness = 8.dp, color = Gray30)

            PriceSummarySection(
                totalPrice = totalPrice,
                couponDiscountPrice = discountAmount,
                deliveryFee = deliveryFee,
            )

            HorizontalDivider(thickness = 8.dp, color = Gray30)

            TotalPriceSection(finalPrice = finalPrice)
        }
    }
}

@Composable
private fun CouponSection(
    coupons: List<CouponUiModel>,
    selectedCoupon: CouponUiModel?,
    onCouponSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 20.dp),
    ) {
        Text(
            text = "적용 가능한 쿠폰",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "* 쿠폰은 1개만 적용 가능합니다.",
            fontSize = 14.sp,
            color = Gray40,
        )
        Spacer(modifier = Modifier.height(16.dp))
        coupons.forEach { coupon ->
            CouponItem(
                description = coupon.description,
                expirationDate = coupon.expirationDate,
                minimumOrderAmount = coupon.minimumOrderAmount?.let { formattedPrice(it) },
                isSelected = coupon == selectedCoupon,
                onSelectClick = { onCouponSelect(coupon.code) },
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun PriceSummarySection(
    totalPrice: Long,
    couponDiscountPrice: Long,
    deliveryFee: Long,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 20.dp),
    ) {
        PriceRow(label = "주문 금액", value = formattedPrice(totalPrice))
        Spacer(modifier = Modifier.height(16.dp))
        PriceRow(
            label = "쿠폰 할인 금액",
            value =
                if (couponDiscountPrice > 0) {
                    "-${formattedPrice(couponDiscountPrice)}"
                } else {
                    formattedPrice(
                        0L,
                    )
                },
        )
        Spacer(modifier = Modifier.height(16.dp))
        PriceRow(label = "배송비", value = formattedPrice(deliveryFee))
    }
}

@Composable
private fun TotalPriceSection(
    finalPrice: Long,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "총 결제 금액",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = formattedPrice(finalPrice),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
    }
}

@Composable
private fun PriceRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Black,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderContentPreview() {
    OrderContent(
        coupons = sampleCoupons,
        selectedCoupon = sampleCoupons[0],
        totalPrice = 204_200L,
        discountAmount = 5_000L,
        deliveryFee = 3_000L,
        finalPrice = 202_200L,
        onOrderClick = { },
        onBackClick = {},
        onCouponSelect = {},
    )
}

private val sampleCoupons =
    listOf(
        CouponUiModel(
            code = "FIXED5000",
            description = "5,000원 할인 쿠폰",
            expirationDate = "2024년 11월 30일",
            minimumOrderAmount = 100_000L,
        ),
        CouponUiModel(
            code = "BOGO",
            description = "2개 구매 시 1개 무료 쿠폰",
            expirationDate = "2024년 11월 30일",
        ),
    )
