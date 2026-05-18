package woowacourse.shopping.ui.component.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.R
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.Products
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.ui.component.frame.CommonFrame
import woowacourse.shopping.ui.component.item.CartBottomBar
import woowacourse.shopping.ui.component.item.ShoppingItem

@Composable
fun CartRecommendationScreen(
    recommendedProducts: Products,
    totalPrice: Int,
    totalCount: Int,
    onBackClick: () -> Unit,
    onOrderClick: () -> Unit,
    onAddInCart: (PurchaseProduct) -> Unit,
    onAdd: (Long, Int) -> Unit,
    onMinus: (Long, Int) -> Unit,
    onDelete: (Long) -> Unit,
    onItemClick: (Product) -> Unit,
    isContainedInCart: (Long) -> Boolean,
    itemCount: (Long) -> Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        CommonFrame(
            headerContent = {
                RecommendationHeader(onBackClick)
            },
            bodyContent = {
                RecommendationBody(
                    recommendedProducts = recommendedProducts,
                    onAddInCart = onAddInCart,
                    onAdd = onAdd,
                    onMinus = onMinus,
                    onDelete = onDelete,
                    onItemClick = onItemClick,
                    isContainedInCart = isContainedInCart,
                    itemCount = itemCount,
                )
            },
            modifier = Modifier.weight(1f),
        )
        CartBottomBar(
            totalPrice = totalPrice,
            totalCount = totalCount,
            onOrderClick = onOrderClick,
            isChecked = true
        )
    }
}

@Composable
private fun RecommendationHeader(onBackClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxSize(),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_left),
            contentDescription = "뒤로가기",
            tint = Color.White,
            modifier =
                Modifier
                    .size(24.dp)
                    .clickable { onBackClick() },
        )
        Spacer(Modifier.padding(horizontal = 8.dp))
        Text(
            text = "Cart",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun RecommendationBody(
    recommendedProducts: Products,
    onAddInCart: (PurchaseProduct) -> Unit,
    onAdd: (Long, Int) -> Unit,
    onMinus: (Long, Int) -> Unit,
    onDelete: (Long) -> Unit,
    onItemClick: (Product) -> Unit,
    isContainedInCart: (Long) -> Boolean,
    itemCount: (Long) -> Int,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(Modifier.height(48.dp))
        Text(
            text = "이런 상품은 어떠세요?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
        Text(
            text = "* 최근 본 상품 기반으로 좋아하실 것 같은 상품들을 추천해드려요.",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
        )

        LazyRow(
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(recommendedProducts.products) { product ->
                ShoppingItem(
                    count = { itemCount(product.id) },
                    product = product,
                    isContainedInCart = { isContainedInCart(product.id) },
                    onAddInCart = { onAddInCart(it) },
                    onAdd = { onAdd(product.id, 1) },
                    onMinus = { onMinus(product.id, -1) },
                    onDelete = { onDelete(product.id) },
                    onClick = { onItemClick(product) },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun CartRecommendationScreenPreview() {
    val mockProducts =
        Products(
            listOf(
                Product(
                    category = "default",
                    id = 1,
                    imageUri = "https://media.sodagift.com/img/image/1734582680547.jpg",
                    name = "PET보틀-정사각형(400ml)",
                    price = 10000,
                ),
                Product(
                    category = "default",
                    id = 2,
                    imageUri = "https://media.sodagift.com/img/image/1734582680547.jpg",
                    name = "PET보틀-정사각형(400ml)",
                    price = 10000,
                ),
                Product(
                    category = "default",
                    id = 3,
                    imageUri = "https://media.sodagift.com/img/image/1734582680547.jpg",
                    name = "PET보틀-정사각형(400ml)",
                    price = 10000,
                ),
            ),
        )

    CartRecommendationScreen(
        recommendedProducts = mockProducts,
        totalPrice = 30000,
        totalCount = 3,
        onBackClick = {},
        onOrderClick = {},
        onAddInCart = { },
        onAdd = { id, amount -> },
        onMinus = { id, amount -> },
        onDelete = { },
        onItemClick = { },
        isContainedInCart = { false },
        itemCount = { 0 },
    )
}
