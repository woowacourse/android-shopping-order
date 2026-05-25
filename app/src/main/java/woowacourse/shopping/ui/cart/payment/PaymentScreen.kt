package woowacourse.shopping.ui.cart.payment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import woowacourse.shopping.model.Coupon
import woowacourse.shopping.model.CouponOrderItem
import woowacourse.shopping.model.DiscountType
import woowacourse.shopping.ui.cart.CartCheckBox
import woowacourse.shopping.ui.cart.CartViewModel
import woowacourse.shopping.ui.component.ShoppingAppBar
import woowacourse.shopping.ui.theme.Gray10
import woowacourse.shopping.ui.theme.Gray40
import woowacourse.shopping.ui.theme.Gray50
import woowacourse.shopping.ui.theme.Green40
import woowacourse.shopping.ui.util.formattedPrice

@Composable
fun PaymentScreenRoute(
    cartViewModel: CartViewModel,
    paymentViewModel: PaymentViewModel = viewModel(factory = PaymentViewModel.Factory),
    onBackClick: () -> Unit,
    onPaymentClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cartUiState by cartViewModel.uiState.collectAsStateWithLifecycle()
    val paymentUiState by paymentViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(cartUiState.totalPrice, cartUiState.selectedCartItems) {
        paymentViewModel.updateOrder(
            orderPrice = cartUiState.totalPrice,
            selectedCartItems = cartUiState.selectedCartItems,
        )
    }

    PaymentScreen(
        orderPrice = paymentUiState.orderPrice,
        coupons = paymentUiState.coupons,
        selectedCouponId = paymentUiState.selectedCouponId,
        couponDiscountPrice = paymentUiState.couponDiscountPrice,
        deliveryFee = paymentUiState.deliveryFee,
        totalPaymentPrice = paymentUiState.totalPaymentPrice,
        orderItems = paymentUiState.orderItems,
        isLoading = paymentUiState.isLoading,
        errorMessage = paymentUiState.errorMessage,
        onBackClick = onBackClick,
        onCouponClick = paymentViewModel::selectCoupon,
        onPaymentClick = onPaymentClick,
        modifier = modifier,
    )
}

@Composable
fun PaymentScreen(
    orderItems: List<CouponOrderItem>,
    coupons: ImmutableList<Coupon>,
    orderPrice: Long,
    selectedCouponId: Long?,
    couponDiscountPrice: Long,
    deliveryFee: Long,
    totalPaymentPrice: Long,
    isLoading: Boolean,
    errorMessage: String?,
    onBackClick: () -> Unit,
    onCouponClick: (Long) -> Unit,
    onPaymentClick: () -> Unit,
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
            PaymentBottomBar(
                onPaymentClick = onPaymentClick,
            )
        },
        modifier = modifier.statusBarsPadding(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color.White),
        ) {
            CouponSection(
                coupons = coupons,
                selectedCouponId = selectedCouponId,
                orderPrice = orderPrice,
                orderItems = orderItems,
                isLoading = isLoading,
                errorMessage = errorMessage,
                onCouponClick = { coupon ->
                    onCouponClick(coupon.id)
                },
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 20.dp),
            )

            HorizontalDivider(
                thickness = 8.dp,
                color = Gray10,
            )

            PaymentSummarySection(
                orderPrice = orderPrice,
                couponDiscountPrice = couponDiscountPrice,
                deliveryFee = deliveryFee,
                totalPaymentPrice = totalPaymentPrice,
            )
        }
    }
}

@Composable
private fun CouponSection(
    coupons: ImmutableList<Coupon>,
    selectedCouponId: Long?,
    orderPrice: Long,
    orderItems: List<CouponOrderItem>,
    isLoading: Boolean,
    errorMessage: String?,
    onCouponClick: (Coupon) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        Text(
            text = "적용 가능한 쿠폰",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
        Text(
            text = "* 쿠폰은 1개만 적용 가능합니다.",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Gray50,
        )
        Spacer(modifier = Modifier.height(4.dp))
        when {
            isLoading -> {
                Text(
                    text = "쿠폰 로딩",
                    fontSize = 14.sp,
                    color = Gray50,
                )
            }

            errorMessage != null -> {
                Text(
                    text = errorMessage,
                    fontSize = 14.sp,
                    color = Gray50,
                )
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    items(
                        items = coupons,
                        key = { coupon -> coupon.id },
                    ) { coupon ->
                        CouponCard(
                            coupon = coupon,
                            isSelected = selectedCouponId == coupon.id,
                            isAvailable = coupon.isAvailable(orderPrice, orderItems),
                            onClick = { onCouponClick(coupon) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CouponCard(
    coupon: Coupon,
    isSelected: Boolean,
    isAvailable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(width = 1.dp, color = Gray40),
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(
                    enabled = isAvailable,
                    onClick = onClick,
                ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                CartCheckBox(
                    isChecked = isSelected,
                    onCheckedChange = {
                        if (isAvailable) onClick()
                    },
                    modifier = Modifier.size(32.dp),
                )

                Text(
                    text = coupon.description,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isAvailable) Gray50 else Gray40,
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 6.dp),
            ) {
                Text(
                    text = "만료일: ${coupon.expirationDate}",
                    fontSize = 12.sp,
                    color = Gray50,
                )
                coupon.minimumAmount?.let {
                    Text(
                        text = "최소 주문 금액: ${formattedPrice(it)}",
                        fontSize = 12.sp,
                        color = Gray50,
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentSummarySection(
    orderPrice: Long,
    couponDiscountPrice: Long,
    deliveryFee: Long,
    totalPaymentPrice: Long,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
    ) {
        PaymentSummaryRow(
            title = "주문 금액",
            price = orderPrice,
        )
        PaymentSummaryRow(
            title = "쿠폰 할인 금액",
            price = -couponDiscountPrice,
        )
        PaymentSummaryRow(
            title = "배송비",
            price = deliveryFee,
        )

        HorizontalDivider(
            thickness = 8.dp,
            color = Gray10,
            modifier = Modifier.padding(top = 12.dp),
        )

        PaymentSummaryRow(
            title = "총 결제 금액",
            price = totalPaymentPrice,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

@Composable
private fun PaymentSummaryRow(
    title: String,
    price: Long,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
        Text(
            text = formattedPrice(price),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
    }
}

@Composable
private fun PaymentBottomBar(
    onPaymentClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .height(48.dp)
                .background(Green40)
                .clickable(onClick = onPaymentClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "결제하기",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
    }
}

private fun previewCoupons(): ImmutableList<Coupon> =
    persistentListOf(
        Coupon(
            id = 1L,
            code = "",
            description = "5,000원 할인 쿠폰",
            expirationDate = "2024년 11월 30일",
            discount = 5000,
            minimumAmount = 100000L,
            availableTime = null,
            buyQuantity = null,
            getQuantity = null,
            discountType = DiscountType.FIXED,
        ),
    )

@Preview
@Composable
private fun PaymentScreenPreview() {
    val coupons = previewCoupons()
    val selectedCouponId = coupons.firstOrNull()?.id
    val deliveryFee = 3000L
    val couponDiscountPrice = 5000L
    val orderItems =
        listOf(
            CouponOrderItem(
                totalPrice = 204200L,
                quantity = 4,
            ),
        )

    PaymentScreen(
        orderPrice = 204200L,
        coupons = coupons,
        selectedCouponId = selectedCouponId,
        couponDiscountPrice = couponDiscountPrice,
        deliveryFee = deliveryFee,
        totalPaymentPrice = 202200L,
        orderItems = orderItems,
        isLoading = false,
        errorMessage = null,
        onBackClick = {},
        onCouponClick = {},
        onPaymentClick = {},
    )
}
