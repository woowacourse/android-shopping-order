package woowacourse.shopping.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProductQuantityBox(
    onQuantityPlusClick: () -> Unit,
    onQuantityMinusClick: () -> Unit,
    quantity: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Button(
            onClick = onQuantityPlusClick,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                "-"
            )
        }

        Text(
            text = quantity.toString(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier =
                Modifier.padding(
                    horizontal = 7.5.dp,
                ),
        )

        Button(
            onClick = onQuantityMinusClick,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                "-"
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ProductQuantityBoxPreview() {
    ProductQuantityBox(
        onQuantityPlusClick = {},
        onQuantityMinusClick = {},
        quantity = 1,
    )
}