package woowacourse.shopping.ui.cart.list.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import woowacourse.shopping.R
import woowacourse.shopping.ui.ShoppingTypography
import woowacourse.shopping.ui.cart.common.CartCheckbox
import woowacourse.shopping.ui.cart.list.uistate.CartItemUiModel
import woowacourse.shopping.ui.common.component.card.shoppingOutlinedCard
import woowacourse.shopping.ui.common.component.cartcontrol.QuantityStepper
import woowacourse.shopping.ui.common.formatter.formatPrice
import woowacourse.shopping.ui.fixture.MockProducts
import woowacourse.shopping.ui.theme.ShoppingColors

@Composable
fun CartItemUnit(
    item: CartItemUiModel,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .shoppingOutlinedCard(backgroundColor = Color.White)
                .padding(18.dp),
    ) {
        NameAndCloseIcon(
            item = item,
            onCheckedChange = onCheckedChange,
            onClick = onDeleteClick,
        )

        Spacer(Modifier.size(20.dp))

        ImageAndPrice(
            item = item,
            onIncreaseQuantity = onIncreaseQuantity,
            onDecreaseQuantity = onDecreaseQuantity,
        )
    }
}

@Composable
private fun NameAndCloseIcon(
    item: CartItemUiModel,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CartCheckbox(
            checked = item.isSelected,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(end = 12.dp),
        )

        Text(
            text = item.name,
            color = ShoppingColors.Gray4,
            style = ShoppingTypography.productName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) {
            IconButton(
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd),
                onClick = onClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.content_description_close),
                    tint = ShoppingColors.Gray2,
                )
            }
        }
    }
}

@Composable
private fun ImageAndPrice(
    item: CartItemUiModel,
    modifier: Modifier = Modifier,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = stringResource(R.string.content_description_image),
            modifier =
                Modifier
                    .width(136.dp)
                    .height(72.dp),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier =
                Modifier
                    .height(72.dp)
                    .padding(end = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End,
        ) {
            QuantityStepper(
                quantity = item.quantity,
                onIncreaseQuantity = onIncreaseQuantity,
                onDecreaseQuantity = onDecreaseQuantity,
            )
            Text(
                text = formatPrice(item.price * item.quantity, withSpaceBeforeWon = true),
                color = ShoppingColors.Gray4,
                style = ShoppingTypography.productPrice,
            )
        }
    }
}

@Preview(showBackground = true, name = "카트 아이템 유닛")
@Composable
private fun CartItemUnitPreview() {
    CartItemUnit(
        item =
            CartItemUiModel(
                cartItemId = MockProducts.APPLE.id,
                productId = MockProducts.APPLE.id,
                name = MockProducts.APPLE.name,
                imageUrl = MockProducts.APPLE.imageUrl,
                price = MockProducts.APPLE.price.value,
                quantity = 2,
            ),
        onCheckedChange = {},
        onDeleteClick = {},
        onIncreaseQuantity = {},
        onDecreaseQuantity = {},
    )
}

@Preview(showBackground = true, name = "이름과 닫기아이콘")
@Composable
private fun NameAndCloseIconPreview() {
    NameAndCloseIcon(
        item =
            CartItemUiModel(
                cartItemId = MockProducts.APPLE.id,
                productId = MockProducts.APPLE.id,
                name = MockProducts.APPLE.name,
                imageUrl = MockProducts.APPLE.imageUrl,
                price = MockProducts.APPLE.price.value,
                quantity = 2,
            ),
        onCheckedChange = {},
        onClick = {},
    )
}

@Preview(showBackground = true, name = "사진과 금액")
@Composable
private fun ImageAndPricePreview() {
    ImageAndPrice(
        item =
            CartItemUiModel(
                cartItemId = MockProducts.APPLE.id,
                productId = MockProducts.APPLE.id,
                name = MockProducts.APPLE.name,
                imageUrl = MockProducts.APPLE.imageUrl,
                price = MockProducts.APPLE.price.value,
                quantity = 2,
            ),
        onIncreaseQuantity = {},
        onDecreaseQuantity = {},
    )
}
