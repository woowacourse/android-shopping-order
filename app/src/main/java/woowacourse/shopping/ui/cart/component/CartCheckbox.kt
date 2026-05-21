package woowacourse.shopping.ui.cart.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.common.theme.PrimaryColor

@Composable
fun CartCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier.size(24.dp),
        colors =
            CheckboxDefaults.colors(
                checkedColor = PrimaryColor,
                uncheckedColor = Color.Gray,
                checkmarkColor = Color.White,
            ),
    )
}

@Preview(showBackground = true, name = "체크됨")
@Composable
private fun CartCheckboxPreview1() {
    CartCheckbox(
        checked = true,
        onCheckedChange = {},
    )
}

@Preview(showBackground = true, name = "체크안됨")
@Composable
private fun CartCheckboxPreview2() {
    CartCheckbox(
        checked = false,
        onCheckedChange = {},
    )
}
