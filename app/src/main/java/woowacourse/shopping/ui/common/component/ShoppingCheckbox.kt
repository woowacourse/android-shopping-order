package woowacourse.shopping.ui.common.component

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
fun ShoppingCheckbox(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit = {},
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
private fun ShoppingCheckboxPreview1() {
    ShoppingCheckbox(
        checked = true,
        onCheckedChange = {},
    )
}

@Preview(showBackground = true, name = "체크안됨")
@Composable
private fun ShoppingCheckboxPreview2() {
    ShoppingCheckbox(
        checked = false,
        onCheckedChange = {},
    )
}
