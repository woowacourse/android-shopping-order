package woowacourse.shopping.ui.detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.ui.component.ProductAsyncImage
import woowacourse.shopping.ui.component.QuantitySelector
import woowacourse.shopping.ui.component.ShoppingAppBar
import woowacourse.shopping.ui.model.ProductUiModel
import woowacourse.shopping.ui.theme.Gray40
import woowacourse.shopping.ui.theme.Green40
import woowacourse.shopping.ui.util.formattedPrice

@Composable
fun DetailScreenRoute(
    onNavigateToCart: () -> Unit,
    onNavigateBack: () -> Unit,
    onRecentItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    detailViewModel: DetailViewModel = viewModel(factory = DetailViewModel.Factory),
) {
    val context = LocalContext.current
    val uiState by detailViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(detailViewModel) {
        detailViewModel.event.collect { event ->
            when (event) {
                DetailEvent.NavigateToCart -> onNavigateToCart()
                DetailEvent.NavigateBack -> onNavigateBack()
                DetailEvent.ShowProductNotFoundMessage -> {
                    Toast.makeText(context, "상품을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }

                DetailEvent.ShowProductLoadFailureMessage -> {
                    Toast.makeText(context, "상품 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }

                DetailEvent.ShowAddCartFailureMessage -> {
                    Toast.makeText(context, "장바구니에 상품을 담지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    DetailScreen(
        product = uiState.product,
        quantity = uiState.quantity,
        totalPrice = uiState.totalPrice,
        recentItem = uiState.recentItem,
        onCloseClick = onNavigateBack,
        onQuantityChange = detailViewModel::updateQuantity,
        onAddToCart = detailViewModel::addToCart,
        onRecentItemClick = onRecentItemClick,
        modifier = modifier,
    )
}

@Composable
fun DetailScreen(
    product: ProductUiModel,
    quantity: Int,
    totalPrice: Long,
    recentItem: ProductUiModel?,
    onCloseClick: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onAddToCart: () -> Unit,
    onRecentItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            ShoppingAppBar(
                contents = {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기",
                        tint = Color.White,
                        modifier =
                            Modifier
                                .size(16.dp)
                                .clickable { onCloseClick() },
                    )
                },
            )
        },
        bottomBar = {
            Box(
                modifier =
                    Modifier
                        .navigationBarsPadding()
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Green40)
                        .clickable { onAddToCart() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "장바구니 담기",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        },
        modifier = modifier.statusBarsPadding(),
    ) { innerPadding ->
        DetailContent(
            imageUrl = product.imageUrl,
            productName = product.name,
            quantity = quantity,
            totalPrice = totalPrice,
            onQuantityChange = onQuantityChange,
            recentItem = {
                if (recentItem != null) {
                    RecentItemCard(
                        name = recentItem.name,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp)
                                .clickable {
                                    onRecentItemClick(recentItem.id)
                                },
                    )
                    Spacer(modifier = Modifier.height(34.dp))
                }
            },
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun DetailContent(
    imageUrl: String,
    productName: String,
    quantity: Int,
    totalPrice: Long,
    onQuantityChange: (Int) -> Unit,
    recentItem: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ProductAsyncImage(
            imageUrl = imageUrl,
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.8f),
        )
        Text(
            text = productName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 18.dp),
        )
        HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Gray40)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
        ) {
            Text(
                text = formattedPrice(totalPrice),
                fontSize = 20.sp,
                fontWeight = FontWeight.W400,
                color = Color.Black,
            )
            QuantitySelector(
                quantity = quantity,
                onQuantityChange = onQuantityChange,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        recentItem()
    }
}

@Preview
@Composable
private fun DetailScreenPreview() {
    DetailScreen(
        product =
            ProductUiModel(
                id = 1L,
                name = "상품",
                price = 1000,
                imageUrl = "",
                quantity = 1,
            ),
        quantity = 1,
        totalPrice = 1000,
        recentItem = null,
        onCloseClick = {},
        onQuantityChange = {},
        onRecentItemClick = {},
        onAddToCart = {},
    )
}

@Preview
@Composable
private fun DetailContentPreview() {
    DetailContent(
        imageUrl = "",
        productName = "Test",
        quantity = 1,
        totalPrice = 1000,
        onQuantityChange = {},
        recentItem = {},
    )
}
