package woowacourse.shopping.ui.component.recommend

import android.icu.text.DecimalFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.R
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

@Composable
fun RecommendOrderButton(
    onOrderButtonClick: () -> Unit,
    orderItemCount: Int,
    totalPrice: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(88.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text =
                DecimalFormat(stringResource(R.string.price_format_pattern)).format(
                    totalPrice,
                ),
            modifier =
                Modifier
                    .weight(2f)
                    .padding(end = 16.dp),
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Button(
            onClick = onOrderButtonClick,
            modifier =
                Modifier
                    .weight(1.5f)
                    .fillMaxHeight(),
            shape = RectangleShape,
            enabled = orderItemCount > 0,
        ) {
            Text(text = "주문하기($orderItemCount)")
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun RecommendOrderButtonPreview() {
    AndroidShoppingTheme {
        RecommendOrderButton(
            onOrderButtonClick = {},
            orderItemCount = 2,
            totalPrice = 24_000,
        )
    }
}
