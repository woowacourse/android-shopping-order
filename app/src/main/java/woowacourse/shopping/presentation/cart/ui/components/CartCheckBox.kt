package woowacourse.shopping.presentation.cart.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import woowacourse.shopping.ui.theme.Green40

@Composable
fun CartCheckBox(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = if (isSelected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
        contentDescription = "선택",
        tint = if (isSelected) Green40 else Color.Gray,
        modifier =
            modifier.clickable {
                onClick()
            },
    )
}

@Preview
@Composable
private fun UnselectedCartCheckBoxPreview() {
    CartCheckBox(
        isSelected = false,
        onClick = {},
    )
}

@Preview
@Composable
private fun selectedCartCheckBoxPreview() {
    CartCheckBox(
        isSelected = true,
        onClick = {},
    )
}
