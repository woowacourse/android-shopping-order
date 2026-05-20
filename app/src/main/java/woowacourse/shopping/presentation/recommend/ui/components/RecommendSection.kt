package woowacourse.shopping.presentation.recommend.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import woowacourse.shopping.R
import woowacourse.shopping.presentation.productlist.components.ProductCard
import woowacourse.shopping.presentation.productlist.model.ShoppingItemUiModel
import woowacourse.shopping.ui.theme.AndroidshoppingTheme
import woowacourse.shopping.ui.theme.Gray50

@Composable
fun RecommendSection(
    onIncrease: (Long) -> Unit,
    onDecrease: (Long) -> Unit,
    items: ImmutableList<ShoppingItemUiModel>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = stringResource(R.string.recommend_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
        Spacer(
            modifier = Modifier.height(4.dp),
        )
        Text(
            text = stringResource(R.string.recommend_subtitle),
            fontSize = 12.sp,
            color = Gray50,
        )
        Spacer(modifier = Modifier.height(30.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(
                items = items,
                key = { it.product.id },
            ) { item ->
                ProductCard(
                    product = item.product,
                    quantity = item.quantity,
                    onClick = {},
                    onIncrease = { onIncrease(item.product.id) },
                    onDecrease = { onDecrease(item.product.id) },
                    modifier =
                        Modifier
                            .width(154.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecommendSectionPreview() {
    AndroidshoppingTheme {
        RecommendSection(
            onIncrease = {},
            onDecrease = {},
            items = emptyList<ShoppingItemUiModel>().toImmutableList(),
        )
    }
}
