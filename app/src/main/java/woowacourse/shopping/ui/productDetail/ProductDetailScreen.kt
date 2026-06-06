package woowacourse.shopping.ui.productDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import woowacourse.shopping.constant.Format.formatPrice
import woowacourse.shopping.constant.ShoppingColor.APP_BAR_COLOR
import woowacourse.shopping.constant.ShoppingColor.CART_ADD_BUTTON_COLOR
import woowacourse.shopping.constant.ShoppingColor.PRODUCT_DETAIL_BACKGROUND_COLOR
import woowacourse.shopping.domain.product.Category
import woowacourse.shopping.domain.product.ImageUrl
import woowacourse.shopping.domain.product.Price
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductName
import woowacourse.shopping.ui.util.LoadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    onCloseClick: () -> Unit,
    viewModel: ProductDetailViewModel,
    onLastViewedProductClick: (Product) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProductDetailScreenContent(
        modifier =
            Modifier
                .fillMaxSize()
                .systemBarsPadding(),
        uiState = uiState,
        onCloseClick = onCloseClick,
        onIncreaseClick = viewModel::increaseQuantity,
        onDecreaseClick = viewModel::decreaseQuantity,
        onAddToCartClick = viewModel::addToCart,
        onLastViewedProductClick = onLastViewedProductClick,
    )
}

@Composable
private fun ProductDetailScreenContent(
    uiState: ProductDetailUiState,
    onCloseClick: () -> Unit,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit,
    modifier: Modifier = Modifier,
    onAddToCartClick: () -> Unit = {},
    onLastViewedProductClick: (Product) -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        ProductDetailTopAppBar(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            onCloseClick = {
                onCloseClick()
            },
        )

        when (uiState.loadState) {
            is LoadState.Initial -> {}

            is LoadState.Loading -> {
                LoadingContent(modifier = Modifier.weight(1f))
            }

            is LoadState.Success -> {
                uiState.product?.let {
                    ProductDetailContent(
                        modifier =
                            Modifier
                                .background(Color.White)
                                .weight(1f),
                        imageUrl = it.imageUrl.value,
                        productName = it.name.value,
                        price = it.price.value,
                        selectedQuantity = uiState.currentQuantity,
                        lastViewedProduct = uiState.lastViewedProduct,
                        onIncreaseClick = onIncreaseClick,
                        onDecreaseClick = onDecreaseClick,
                        onLastViewedProductClick = onLastViewedProductClick,
                    )
                    CardAddButton(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                        onAddToCartClick = {
                            onAddToCartClick()
                        },
                    )
                }
            }

            is LoadState.Error -> {
                ErrorContent(
                    modifier = Modifier.weight(1f),
                    message = uiState.loadState.message ?: "알 수 없는 에러가 발생하였습니다.",
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailTopAppBar(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(text = "")
        },
        actions = {
            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "닫기",
                    tint = Color.White,
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color(APP_BAR_COLOR),
                scrolledContainerColor = Color.Unspecified,
                navigationIconContentColor = Color.White,
                titleContentColor = Color.White,
                actionIconContentColor = Color.White,
            ),
        windowInsets = WindowInsets(0, 0, 0, 0),
    )
}

@Composable
private fun ProductDetailContent(
    modifier: Modifier = Modifier,
    imageUrl: String,
    productName: String,
    price: Int,
    selectedQuantity: Int,
    lastViewedProduct: Product?,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit,
    onLastViewedProductClick: (Product) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        ProductImageSection(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(
                        Color(PRODUCT_DETAIL_BACKGROUND_COLOR),
                    ),
            imageUrl = imageUrl,
        )
        ProductNameSection(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            productName = productName,
        )
        HorizontalDivider(
            color = Color.Black,
            thickness = 1.dp,
        )
        ProductPriceSection(price = price)
        ProductQuantitySection(
            quantity = selectedQuantity,
            onIncreaseClick = onIncreaseClick,
            onDecreaseClick = onDecreaseClick,
        )
        if (lastViewedProduct != null) {
            LastViewedProductSection(
                product = lastViewedProduct,
                onClick = { onLastViewedProductClick(lastViewedProduct) },
            )
        }
    }
}

@Composable
private fun ProductImageSection(
    modifier: Modifier = Modifier,
    imageUrl: String,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        ProductImage(
            imageUrl = imageUrl,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(40.dp),
        )
    }
}

@Composable
private fun ProductNameSection(
    productName: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = productName,
        modifier = modifier,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
    )
}

@Composable
private fun ProductPriceSection(
    price: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "가격",
            fontSize = 18.sp,
            color = Color.Black,
        )
        Text(
            text = formatPrice(price),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
        )
    }
}

@Composable
private fun ProductQuantitySection(
    quantity: Int,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "수량",
            fontSize = 18.sp,
            color = Color.Black,
        )
        QuantityStepper(
            quantity = quantity,
            onIncreaseClick = onIncreaseClick,
            onDecreaseClick = onDecreaseClick,
        )
    }
}

@Composable
private fun LastViewedProductSection(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = "마지막으로 본 상품",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF12B89A),
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .clickable(onClick = onClick)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Text(
                text = product.name.value,
                fontSize = 18.sp,
                color = Color(0xFF4A4A4A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepperSign(symbol = "−", onClick = onDecreaseClick)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = quantity.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.width(16.dp))
        StepperSign(symbol = "+", onClick = onIncreaseClick)
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
                .size(28.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF0F0F0))
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = symbol,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
        )
    }
}

@Composable
private fun ProductImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
) {
    AsyncImage(
        modifier = modifier,
        model = imageUrl,
        contentDescription = "상품 이미지",
    )
}

@Composable
private fun CardAddButton(
    modifier: Modifier = Modifier,
    onAddToCartClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = onAddToCartClick,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Color(CART_ADD_BUTTON_COLOR),
                contentColor = Color.White,
            ),
        shape = RoundedCornerShape(0.dp),
    ) {
        Text(
            text = "장바구니 담기",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailScreenPreview() {
    ProductDetailScreenContent(
        uiState =
            ProductDetailUiState(
                product =
                    Product(
                        id = 1,
                        name = ProductName("상품명"),
                        imageUrl = ImageUrl("https://example.com/image.jpg"),
                        price = Price(10000),
                        category = Category("카테고리"),
                    ),
                currentQuantity = 1,
                lastViewedProduct = null,
                loadState = LoadState.Success,
            ),
        onCloseClick = {},
        onIncreaseClick = {},
        onDecreaseClick = {},
        onAddToCartClick = {},
        onLastViewedProductClick = { },
    )
}
