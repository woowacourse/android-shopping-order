package woowacourse.shopping.feature.cart.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.R
import woowacourse.shopping.feature.common.ProductQuantitySelector
import woowacourse.shopping.feature.format.DecimalPriceFormatter
import woowacourse.shopping.feature.productlist.PreviewableAsyncImage

@Composable
fun CartItem(
    isLoading: Boolean,
    imageUrl: String,
    name: String,
    price: Int,
    quantity: Int,
    onDelete: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
            .then(
                if (isLoading) {
                    Modifier.background(Color(0xfff3f3f3))
                } else Modifier,
            )
            .clip(RoundedCornerShape(4.dp))
            .border(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xffaaaaaa),
                width = 1.dp,
            )
            .padding(horizontal = 12.dp, vertical = 18.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.W700,
                fontSize = 18.sp,
                color = if (isLoading) Color.Transparent else Color.Unspecified,
                modifier = Modifier.background(if (isLoading) Color.LightGray else Color.Unspecified),
            )
            if (isLoading.not())
                IconButton(
                    onClick = onDelete,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = stringResource(R.string.close_description),
                        tint = Color(0xffaaaaaa),
                        modifier = Modifier.padding(end = 10.dp),
                    )
                }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth(),
        ) {
            PreviewableAsyncImage(
                isLoading = isLoading,
                imageUrl = imageUrl,
                description = name,
                modifier = Modifier
                    .size(width = 136.dp, height = 72.dp)
                    .then(
                        if (isLoading)
                            Modifier.background(Color.LightGray)
                        else Modifier,
                    ),
            )
            if (isLoading.not())
                Column(horizontalAlignment = Alignment.End) {
                    ProductQuantitySelector(
                        quantity = quantity,
                        onIncrease = onIncrease,
                        onDecrease = onDecrease,
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .size(width = 126.dp, height = 42.dp),
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = DecimalPriceFormatter().format(price * quantity),
                        fontWeight = FontWeight.W400,
                        fontSize = 16.sp,
                        color = Color(0xff555555),
                    )
                }
        }
    }
}

@Preview
@Composable
private fun CartItemPreview() {
    CartItem(
        isLoading = true,
        imageUrl = "",
        name = "프리뷰",
        price = 1000,
        onDelete = {},
        onIncrease = {},
        onDecrease = {},
        quantity = 33,
    )
}
