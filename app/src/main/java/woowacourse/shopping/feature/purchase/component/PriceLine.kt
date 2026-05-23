package woowacourse.shopping.feature.purchase.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import woowacourse.shopping.feature.format.DecimalPriceFormatter

@Composable
fun PriceLine(
    header: String,
    price: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(header, fontWeight = FontWeight.W700, fontSize = 18.sp)
        Text(
            DecimalPriceFormatter().format(price),
            fontWeight = FontWeight.W500,
            fontSize = 18.sp,
            color = Color(0xff333333),
        )
    }
}
