package woowacourse.shopping.ui.component.cart

import android.icu.text.DecimalFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import woowacourse.shopping.R
import woowacourse.shopping.model.Price
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductTitle
import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.ui.component.productlist.ProductQuantityBox

@Composable
fun ShoppingCartItemCard(
    shoppingCartItem: ShoppingCartItem,
    selected: Boolean,
    quantityPrice: Int,
    onRemoveShoppingItemClick: (ShoppingCartItem) -> Unit,
    onToggleShoppingItemSelectionClick: (Long, Boolean) -> Unit,
    onIncreaseShoppingItemQuantityClick: (ShoppingCartItem) -> Unit,
    onDecreaseShoppingItemQuantityClick: (ShoppingCartItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .border(
                    color = MaterialTheme.colorScheme.outline,
                    width = 1.dp,
                    shape = RoundedCornerShape(4.dp),
                ).padding(12.dp),
    ) {
        val product = shoppingCartItem.product
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                ShoppingCartCheckBox(
                    checked = selected,
                    onCheckedChange = { isChecked ->
                        onToggleShoppingItemSelectionClick(shoppingCartItem.getId(), isChecked)
                    },
                )
                Text(
                    text = product.getTitle(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            Image(
                painter = painterResource(R.drawable.remove_icon),
                contentDescription = stringResource(R.string.remove_item_description),
                modifier =
                    Modifier
                        .size(16.dp)
                        .clickable { onRemoveShoppingItemClick(shoppingCartItem) },
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = stringResource(R.string.product_image_description),
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .width(136.dp)
                        .height(72.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer),
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End,
            ) {
                ProductQuantityBox(
                    onQuantityPlusClick = { onIncreaseShoppingItemQuantityClick(shoppingCartItem) },
                    onQuantityMinusClick = { onDecreaseShoppingItemQuantityClick(shoppingCartItem) },
                    quantity = shoppingCartItem.getQuantity(),
                    modifier = Modifier.padding(top = 8.dp),
                )
                Text(
                    text =
                        DecimalFormat(stringResource(R.string.price_format_pattern)).format(
                            quantityPrice,
                        ),
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ShoppingCartItemCardPreview() {
    ShoppingCartItemCard(
        shoppingCartItem =
            ShoppingCartItem(
                id = 1,
                shoppingItem =
                    ShoppingItem(
                        Product(1, ProductTitle("동원 스위트콘"), Price(99_800), ""),
                        4,
                    ),
            ),
        quantityPrice = 399_200,
        onRemoveShoppingItemClick = {},
        onToggleShoppingItemSelectionClick = { _, _ -> },
        onIncreaseShoppingItemQuantityClick = {},
        onDecreaseShoppingItemQuantityClick = {},
        selected = true,
    )
}
