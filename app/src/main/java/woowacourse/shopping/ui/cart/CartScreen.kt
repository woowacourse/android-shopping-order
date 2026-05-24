package woowacourse.shopping.ui.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import woowacourse.shopping.constant.Format.formatPrice
import woowacourse.shopping.constant.ShoppingColor.CART_PAGE_BUTTON_ACTIVE_COLOR
import woowacourse.shopping.constant.ShoppingColor.CART_PAGE_BUTTON_INACTIVE_COLOR
import woowacourse.shopping.domain.product.Category
import woowacourse.shopping.domain.product.ImageUrl
import woowacourse.shopping.domain.product.Price
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductName
import woowacourse.shopping.ui.util.LoadState

@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onClickClose: () -> Unit,
    onNavigateToRecommend: () -> Unit,
) {
    val uiState by viewModel.cartUiState.collectAsStateWithLifecycle()
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .systemBarsPadding(),
    ) {
        CartTopAppBar(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            onClick = onClickClose,
        )
        CartScreenContent(
            modifier = Modifier.weight(1f),
            uiState = uiState,
            onClickRemoveItem = viewModel::removeCartItem,
            onClickIncrease = viewModel::increaseCartItemQuantity,
            onClickDecrease = viewModel::decreaseCartItemQuantity,
            onClickGoToPreviousPage = viewModel::goToPreviousPage,
            onClickGoToNextPage = viewModel::goToNextPage,
            onClickToggleSelection = viewModel::toggleSelection,
        )

        CartScreenBottomBar(
            totalMoney = uiState.totalPrice,
            totalCount = uiState.totalCount,
            selectAllButtonVisible = true,
            isAllSelected = uiState.isAllSelected,
            onClickSelectAll = viewModel::toggleAllSelection,
            onClickOrder = onNavigateToRecommend,
        )
    }
}

@Composable
private fun CartScreenContent(
    uiState: CartUiState,
    modifier: Modifier = Modifier,
    onClickRemoveItem: (Int) -> Unit,
    onClickIncrease: (Int) -> Unit,
    onClickDecrease: (Int) -> Unit,
    onClickGoToPreviousPage: () -> Unit,
    onClickGoToNextPage: () -> Unit,
    onClickToggleSelection: (Int) -> Unit,
) {
    when (uiState.loadState) {
        is LoadState.Initial -> {}

        is LoadState.Loading -> {
            LoadingContent(
                modifier = modifier,
            )
        }

        is LoadState.Success -> {
            CartItemList(
                cartItems = uiState.cartItems,
                modifier = modifier,
                onRemoveClick = { cartId -> onClickRemoveItem(cartId) },
                onIncrease = { cartId -> onClickIncrease(cartId) },
                onDecrease = { cartId -> onClickDecrease(cartId) },
                onCheckedChange = { cartId -> onClickToggleSelection(cartId) },
            )

            if (uiState.showPageNavigator) {
                PageNavigator(
                    currentPage = uiState.currentPage,
                    hasPrevious = uiState.hasPreviousPage,
                    hasNext = uiState.hasNextPage,
                    onPreviousClick = { onClickGoToPreviousPage() },
                    onNextClick = { onClickGoToNextPage() },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                )
            }
        }

        is LoadState.Error -> {
            ErrorContent(
                modifier = modifier,
                message = uiState.loadState.throwable?.message ?: "장바구니를 불러오지 못했어요.",
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            fontSize = 16.sp,
            color = Color.Gray,
        )
    }
}

@Composable
private fun CartItemList(
    cartItems: List<CartItemUiModel>,
    onRemoveClick: (Int) -> Unit,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onCheckedChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 4.dp),
    ) {
        items(
            items = cartItems,
            key = { it.id },
        ) { cartItem ->
            CartItemCard(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp),
                cartItem = cartItem,
                onRemoveClick = {
                    onRemoveClick(cartItem.id)
                },
                onIncrease = {
                    onIncrease(cartItem.id)
                },
                onDecrease = {
                    onDecrease(cartItem.id)
                },
                onCheckedChange = {
                    onCheckedChange(cartItem.id)
                },
            )
        }
    }
}

@Composable
private fun CartItemCard(
    modifier: Modifier = Modifier,
    cartItem: CartItemUiModel,
    onRemoveClick: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onCheckedChange: (Int) -> Unit = {},
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(2.dp),
        border = BorderStroke(1.dp, Color(0xFFD0D0D0)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = cartItem.isSelected,
                    onCheckedChange = {
                        onCheckedChange(cartItem.id)
                    },
                )
                Text(
                    text = cartItem.product.name.value,
                    modifier = Modifier.weight(1f),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A4A4A),
                )

                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier.size(28.dp),
                ) {
                    Icon(
                        imageVector = Default.Close,
                        contentDescription = "삭제",
                        tint = Color(0xFFB0B0B0),
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                ProductImage(
                    imageUrl = cartItem.product.imageUrl.value,
                    modifier = Modifier.size(width = 72.dp, height = 64.dp),
                )
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    QuantityStepper(
                        quantity = cartItem.quantity,
                        onIncrease = onIncrease,
                        onDecrease = onDecrease,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatPrice(cartItem.totalPrice),
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepperSign(symbol = "−", onClick = onDecrease)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = quantity.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.width(12.dp))
        StepperSign(symbol = "+", onClick = onIncrease)
    }
}

@Composable
private fun StepperSign(
    symbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = symbol,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4A4A4A),
        )
    }
}

@Composable
private fun PageNavigator(
    currentPage: Int,
    hasPrevious: Boolean,
    hasNext: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Button(
            onClick = onPreviousClick,
            enabled = hasPrevious,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color(CART_PAGE_BUTTON_ACTIVE_COLOR),
                    disabledContainerColor = Color(CART_PAGE_BUTTON_INACTIVE_COLOR),
                ),
        ) {
            Text(
                text = "<",
                color = Color.White,
            )
        }
        Text(
            text = "${currentPage + 1}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
        Button(
            onClick = onNextClick,
            enabled = hasNext,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color(CART_PAGE_BUTTON_ACTIVE_COLOR),
                    disabledContainerColor = Color(CART_PAGE_BUTTON_INACTIVE_COLOR),
                ),
        ) {
            Text(
                text = ">",
                color = Color.White,
            )
        }
    }
}

@Composable
private fun ProductImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "상품 이미지",
        modifier =
            modifier
                .background(Color(0xFFF1F1F1))
                .border(1.dp, Color(0xFFE4E4E4)),
    )
}

@Preview(showBackground = true)
@Composable
private fun CartScreenPreview() {
    CartScreenContent(
        modifier = Modifier.fillMaxSize(),
        uiState =
            CartUiState(
                cartItems =
                    listOf(
                        CartItemUiModel(
                            id = 1,
                            product =
                                Product(
                                    id = 1,
                                    imageUrl = ImageUrl("https://example.com/image.png"),
                                    name = ProductName("PET보틀-정사각형"),
                                    price = Price(10_000),
                                    Category("도서"),
                                ),
                            quantity = 2,
                            totalPrice = 20_000,
                            isSelected = false,
                        ),
                    ),
                currentPage = 1,
                hasPreviousPage = false,
                hasNextPage = false,
                loadState = LoadState.Success,
            ),
        onClickRemoveItem = {},
        onClickIncrease = {},
        onClickDecrease = {},
        onClickGoToPreviousPage = {},
        onClickGoToNextPage = {},
        onClickToggleSelection = {},
    )
}
