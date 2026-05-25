package woowacourse.shopping.ui.payment.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.data.model.CartItem

@Composable
fun OrderItemsSection(items: List<CartItem>) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Text(
            text = "주문 상품",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (items.isEmpty()) {
            Text(
                text = "선택된 상품이 없습니다.",
                fontSize = 14.sp,
                color = Color.Gray,
            )
        } else {
            items.forEach { item ->
                OrderItemRow(item = item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun OrderItemRow(item: CartItem) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "${item.product.name} x ${item.quantity}",
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            color = Color.Black,
        )
        Text(
            text = item.totalPrice.value.formatPrice(),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
        )
    }
}
