package woowacourse.shopping.presentation.productdetail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import woowacourse.shopping.presentation.common.model.ProductUiModel
import woowacourse.shopping.presentation.productdetail.components.DetailContent
import woowacourse.shopping.presentation.productdetail.components.LastSeenProductCard
import woowacourse.shopping.presentation.productdetail.model.DetailUiState
import woowacourse.shopping.ui.theme.AndroidshoppingTheme
import woowacourse.shopping.ui.theme.Green40

@Composable
fun ProductDetailScreen(
    onRecentProductClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductDetailViewModel = viewModel(factory = ProductDetailViewModel.Factory),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.event.collect { event ->
                when (event) {
                    is ProductDetailEvent.AddToCart -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                    is ProductDetailEvent.NavigateToBack -> {
                        onBackClick()
                    }
                }
            }
        }
    }

    when (val uiState = uiState.value) {
        is DetailUiState.Loading -> {}

        is DetailUiState.Error -> {}

        is DetailUiState.Success -> {
            ProductDetailContent(
                product = uiState.product,
                recentProduct = uiState.recentProduct,
                productQuantity = uiState.quantity,
                onRecentProductClick = onRecentProductClick,
                onBackClick = onBackClick,
                onAddToCart = { viewModel.addItemToCart(uiState.quantity) },
                onIncrease = viewModel::increaseQuantity,
                onDecrease = viewModel::decreaseQuantity,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun ProductDetailContent(
    product: ProductUiModel,
    recentProduct: ProductUiModel?,
    productQuantity: Int,
    onRecentProductClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    onAddToCart: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            ShoppingAppBar(
                contents = {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.back),
                        tint = Color.White,
                        modifier =
                            Modifier
                                .size(16.dp)
                                .clickable { onBackClick() },
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
                    text = stringResource(R.string.add_product_to_cart),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        },
        modifier =
            modifier
                .statusBarsPadding(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(30.dp),
        ) {
            DetailContent(
                imageUrl = product.imageUrl,
                productName = product.name,
                price = product.price,
                quantity = productQuantity,
                onIncrease = { onIncrease() },
                onDecrease = { onDecrease() },
            )
            recentProduct?.let { lastProduct ->
                LastSeenProductCard(
                    name = lastProduct.name,
                    onClick = { onRecentProductClick(lastProduct.id) },
                    modifier =
                        Modifier
                            .padding(horizontal = 18.dp),
                )
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Preview
@Composable
private fun ProductDetailContentPreview() {
    AndroidshoppingTheme {
        ProductDetailContent(
            product =
                ProductUiModel(
                    id = 1L,
                    name = "맛있는 사과",
                    price = 3000L,
                    imageUrl = "",
                ),
            recentProduct =
                ProductUiModel(
                    id = 2L,
                    name = "신선한 바나나",
                    price = 1500L,
                    imageUrl = "",
                ),
            productQuantity = 1,
            onRecentProductClick = {},
            onBackClick = {},
            onAddToCart = {},
            onIncrease = {},
            onDecrease = {},
        )
    }
}
