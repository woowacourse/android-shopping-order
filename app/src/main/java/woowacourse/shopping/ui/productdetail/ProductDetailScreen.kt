package woowacourse.shopping.ui.productdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.R
import woowacourse.shopping.core.designsystem.component.ProductImage
import woowacourse.shopping.core.designsystem.component.QuantitySelector
import woowacourse.shopping.core.designsystem.component.layout.CommonFrame
import woowacourse.shopping.core.formatter.toPriceString
import woowacourse.shopping.ui.productdetail.component.LastViewedProduct
import woowacourse.shopping.ui.uimodel.ProductUiModel

@Composable
fun ProductDetailScreen(
    uiState: ProductDetailUiState,
    onLastViewedClick: (Long) -> Unit,
    onAdd: () -> Unit,
    onMinus: () -> Unit,
    onAddRequest: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CommonFrame(
        headerContent = { ProductDetailHeader(onClose) },
        bodyContent = {
            uiState.product?.let { product ->
                ProductDetailBody(
                    uiState = uiState,
                    product = product,
                    onLastViewedClick = onLastViewedClick,
                    onAdd = onAdd,
                    onMinus = onMinus,
                    onAddRequest = onAddRequest,
                )
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun ProductDetailHeader(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = "닫기 버튼",
            modifier =
                Modifier
                    .size(16.dp)
                    .clickable(onClick = onClose),
            tint = Color.White,
        )
    }
}

@Composable
private fun ProductDetailBody(
    uiState: ProductDetailUiState,
    product: ProductUiModel,
    onLastViewedClick: (Long) -> Unit,
    onAdd: () -> Unit,
    onMinus: () -> Unit,
    onAddRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        ProductDetailInfo(
            uiState = uiState,
            product = product,
            onLastViewedClick = onLastViewedClick,
            onAdd = onAdd,
            onMinus = onMinus,
        )

        TextButton(
            onClick = onAddRequest,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(color = Color(0xFF04C09E)),
        ) {
            Text(
                text = "장바구니 담기",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun ProductDetailInfo(
    uiState: ProductDetailUiState,
    product: ProductUiModel,
    onLastViewedClick: (Long) -> Unit,
    onAdd: () -> Unit,
    onMinus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        val configuration = LocalConfiguration.current
        val maxWidth = configuration.screenWidthDp.dp
        ProductImage(
            imageUri = product.imageUrl,
            modifier =
                Modifier
                    .size(maxWidth),
        )

        Text(
            text = product.name,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            modifier = Modifier.padding(18.dp),
        )
        HorizontalDivider()
        Row(
            modifier =
                Modifier
                    .padding(18.dp)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = uiState.formattedTotalPrice,
                fontSize = 20.sp,
            )

            QuantitySelector(
                count = uiState.count,
                onAdd = onAdd,
                onMinus = onMinus,
            )
        }
        if (uiState.lastViewedProduct != null && uiState.lastViewedProduct.id != product.id) {
            LastViewedProduct(
                product = uiState.lastViewedProduct,
                onClick = onLastViewedClick,
                modifier = Modifier.padding(18.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailScreenPreview1() {
    val mockProduct = ProductUiModel(
        imageUrl = "hello",
        name = "너무너무너무긴아이템이름",
        price = 100000,
        formattedPrice = 100000.toPriceString(),
        category = "a",
        id = 1L,
    )

    ProductDetailScreen(
        uiState = ProductDetailUiState(
            product = mockProduct,
            count = 1,
            lastViewedProduct = mockProduct,
        ),
        onLastViewedClick = {},
        onAddRequest = {},
        onClose = {},
        onAdd = { },
        onMinus = { },
    )
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailScreenPreview2() {
    val product =
        ProductUiModel(
            imageUrl = "hello",
            name = "너무너무너무긴아이템이름",
            price = 100000,
            formattedPrice = 100000.toPriceString(),
            category = "a",
            id = 1L,
        )

    ProductDetailScreen(
        uiState = ProductDetailUiState(
            product = product,
            count = 1,
            lastViewedProduct = product,
        ),
        onLastViewedClick = {},
        onAddRequest = {},
        onClose = {},
        onAdd = { },
        onMinus = { },
    )
}
