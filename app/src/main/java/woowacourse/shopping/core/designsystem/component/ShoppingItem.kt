package woowacourse.shopping.core.designsystem.component

import android.icu.text.DecimalFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.visible
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.uimodel.ProductUiModel

@Composable
fun ShoppingItem(
    count: () -> Int,
    product: ProductUiModel,
    isContainedInCart: () -> Boolean,
    onAddInCart: (Long) -> Unit,
    onAdd: () -> Unit,
    onMinus: () -> Unit,
    onDelete: () -> Unit,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .width(154.dp)
                .height(206.dp)
                .clickable(
                    onClick = {
                        onClick(product.id)
                    },
                ),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Box {
            ProductImage(product.imageUrl, Modifier.size(154.dp))
            CirclePlusBtn(
                onClick = {
                    onAddInCart(product.id)
                },
                modifier =
                    Modifier
                        .padding(end = 15.dp)
                        .align(Alignment.BottomEnd)
                        .visible(!isContainedInCart()),
            )
            QuantitySelector(
                count = count(),
                onAdd = onAdd,
                onMinus = onMinus,
                onDelete = onDelete,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .visible(isContainedInCart()),
            )
        }

        ProductInfo(product.name, product.formattedPrice)
    }
}

@Composable
private fun ProductInfo(
    name: String,
    formattedPrice: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .padding(horizontal = 9.dp)
                .width(154.dp),
    ) {
        Text(
            text = name,
            fontWeight = FontWeight(700),
            fontSize = 18.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier,
        )

        Text(
            text = formattedPrice,
            fontSize = 16.sp,
            color = Color(0xFF555555),
            modifier = Modifier,
        )
    }
}

fun Int.toPriceString(): String {
    val formatter = DecimalFormat("###,###")
    return "${formatter.format(this)}원"
}

@Preview(showBackground = true)
@Composable
private fun ShoppingItemPreview1() {
    val mockProduct = ProductUiModel(
        imageUrl = "https://media.sodagift.com/img/image/1734582680547.jpg",
        name = "매우매우긴상품명입니다",
        price = 1000000000,
        formattedPrice = 1000000000.toPriceString(),
        category = "카테고리",
        id = 1L,
    )

    ShoppingItem(
        { 0 },
        mockProduct,
        onClick = {},
        onAdd = {},
        onMinus = {},
        onDelete = {},
        onAddInCart = {},
        isContainedInCart = { false },
    )
}

@Preview(showBackground = true)
@Composable
private fun ShoppingItemPreview2() {
    val previewProduct =
        ProductUiModel(
            imageUrl = "https://media.sodagift.com/img/image/1734582680547.jpg",
            name = "매우매우긴상품명입니다",
            price = 1000000000,
            formattedPrice = 1000000000.toPriceString(),
            category = "카테고리",
            id = 1L,
        )

    ShoppingItem(
        { 1 },
        previewProduct,
        onClick = {},
        onAdd = {},
        onMinus = {},
        onDelete = {},
        onAddInCart = {},
        isContainedInCart = { true },
    )
}
