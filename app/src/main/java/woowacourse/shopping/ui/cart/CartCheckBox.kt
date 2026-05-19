package woowacourse.shopping.ui.cart

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import woowacourse.shopping.ui.theme.Green40

@Composable
fun CartCheckBox(
    onCheckedChange: () -> Unit,
    isChecked: Boolean,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onCheckedChange,
        modifier = modifier,
    ) {
        Icon(
            imageVector =
                if (isChecked) {
                    Icons.Default.CheckBox
                } else {
                    Icons.Default.CheckBoxOutlineBlank
                },
            contentDescription = "상품 선택 버튼",
            tint = if (isChecked) Green40 else Color.Unspecified,
        )
    }
}
