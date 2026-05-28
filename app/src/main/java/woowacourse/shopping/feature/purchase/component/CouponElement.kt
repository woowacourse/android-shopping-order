package woowacourse.shopping.feature.purchase.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.format.DateTimeFormatter
import woowacourse.shopping.feature.purchase.CouponUiModel

@Composable
fun CouponElement(
    model: CouponUiModel,
    selectedCouponId: String?,
    onCheckClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(4.dp))
            .border(width = 1.dp, color = Color(0xffaaaaaa))
            .padding(horizontal = 16.dp, vertical = 18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = model.id == selectedCouponId,
                onCheckedChange = { _ ->
                    onCheckClick(model.id)
                },
                colors = CheckboxDefaults.colors().copy(
                    checkedBoxColor = Color(0xFF04C09E),
                ),
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.width(5.dp))
            Text(model.title, fontWeight = FontWeight.W700, fontSize = 18.sp)
        }
        Spacer(Modifier.height(5.dp))
        Text(
            "만료일 : ${model.expirationDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))}",
            fontWeight = FontWeight.W400,
            fontSize = 12.sp,
            color = Color(0xff555555),
        )
        if (model.description != null) {
            Text(
                "최소 주문 금액: ${model.description}",
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                color = Color(0xff555555),
            )
        }
    }
}
