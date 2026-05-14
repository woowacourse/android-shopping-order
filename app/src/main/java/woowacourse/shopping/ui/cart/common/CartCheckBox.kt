package woowacourse.shopping.ui.cart.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.theme.ShoppingColors

@Composable
fun CartCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Checkbox(
        modifier = modifier.size(18.dp),
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors =
            CheckboxDefaults.colors(
                uncheckedColor = ShoppingColors.Gray4,
                checkedColor = ShoppingColors.BrandGreen,
            ),
    )
}

@Preview(showBackground = true)
@Composable
private fun CartCheckboxPreview() {
    CartCheckbox(
        checked = false,
        onCheckedChange = {},
    )
}
