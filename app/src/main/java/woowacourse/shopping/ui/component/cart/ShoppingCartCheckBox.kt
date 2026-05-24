package woowacourse.shopping.ui.component.cart

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ShoppingCartCheckBox(
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    enabled: Boolean = true,
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        modifier = modifier,
        colors =
            CheckboxDefaults.colors(
                uncheckedColor = MaterialTheme.colorScheme.outline,
                disabledCheckedColor = MaterialTheme.colorScheme.outline,
                disabledUncheckedColor = MaterialTheme.colorScheme.outline,
            ),
    )
}
