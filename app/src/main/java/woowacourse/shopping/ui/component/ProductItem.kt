@file:Suppress("FunctionName")

package woowacourse.shopping.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.Product
import java.text.DecimalFormat

@Composable
fun ProductItem(
    product: Product,
    quantity: Int,
    onAddToCartClick: () -> Unit,
    onQuantityPlusClick: () -> Unit,
    onQuantityMinusClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd,
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription =
                    stringResource(
                        R.string.product_image_content_description,
                        product.getTitle(),
                    ),
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(154.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer),
            )
            AddQuantityButton(
                quantity = quantity,
                onAddToCartClick = onAddToCartClick,
                onQuantityPlusClick = onQuantityPlusClick,
                onQuantityMinusClick = onQuantityMinusClick,
                modifier = Modifier.padding(8.dp),
            )
        }
        Text(
            text = product.getTitle(),
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground,
            modifier =
                Modifier.padding(
                    horizontal = 7.5.dp,
                ),
        )
        Text(
            text = DecimalFormat(stringResource(R.string.price_format_pattern)).format(product.getPrice()),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier =
                Modifier.padding(
                    horizontal = 7.5.dp,
                ),
        )
    }
}

@Composable
private fun AddQuantityButton(
    quantity: Int,
    onAddToCartClick: () -> Unit,
    onQuantityPlusClick: () -> Unit,
    onQuantityMinusClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (quantity == 0) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd,
        ) {
            ShoppingCartAddBox(
                onShoppingCartAddClick = onAddToCartClick,
                modifier = Modifier.size(36.dp),
            )
        }
    } else {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            ProductQuantityBox(
                onQuantityPlusClick = onQuantityPlusClick,
                onQuantityMinusClick = onQuantityMinusClick,
                quantity = quantity,
                modifier = Modifier,
            )
        }
    }
}
