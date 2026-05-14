package woowacourse.shopping.ui.productList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import woowacourse.shopping.constant.Format.formatPrice

@Composable
fun ProductCard(
    productName: String,
    price: Int,
    imageUrl: String,
    quantity: Int,
    onClick: () -> Unit,
    onAddClick: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.clickable { onClick() }) {
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "상품 이미지",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(154.dp),
                contentScale = ContentScale.Crop,
            )
            ProductCardQuantityControl(
                quantity = quantity,
                onAddClick = onAddClick,
                onIncrease = onIncrease,
                onDecrease = onDecrease,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
            )
        }
        ProductInfoColumn(
            modifier = Modifier.padding(start = 6.dp, end = 9.dp, top = 8.dp, bottom = 12.dp),
            productName = productName,
            price = price,
        )
    }
}

@Composable
private fun ProductInfoColumn(
    productName: String,
    price: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            productName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            formatPrice(price),
            fontSize = 16.sp,
            color = Color.Gray,
        )
    }
}

@Composable
private fun ProductCardQuantityControl(
    quantity: Int,
    onAddClick: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (quantity == 0) {
        AddCircleButton(onClick = onAddClick, modifier = modifier)
    } else {
        InlineStepper(
            quantity = quantity,
            onIncrease = onIncrease,
            onDecrease = onDecrease,
            modifier = modifier,
        )
    }
}

@Composable
private fun AddCircleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFB0B0B0))
                .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "+",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
    }
}

@Composable
private fun InlineStepper(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .height(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepperSign(symbol = "-", onClick = onDecrease)
        Box(
            modifier =
                Modifier
                    .width(28.dp)
                    .height(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "$quantity",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
            )
        }
        StepperSign(symbol = "+", onClick = onIncrease)
    }
}

@Composable
private fun StepperSign(
    symbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(width = 28.dp, height = 32.dp)
                .clickable { onClick() }
                .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = symbol,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
    }
}

@Preview
@Composable
private fun ProductCardPreview() {
    ProductCard(
        productName = "상품명",
        price = 10000,
        imageUrl = "",
        quantity = 0,
        onClick = {},
        onAddClick = {},
        onIncrease = {},
        onDecrease = {},
    )
}
