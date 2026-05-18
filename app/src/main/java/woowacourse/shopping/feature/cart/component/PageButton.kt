package woowacourse.shopping.feature.cart.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PageButton(
    text: String,
    onClick: () -> Unit,
    isEnable: Boolean,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = isEnable,
        shape = RoundedCornerShape(0.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Color(0xff04c09e),
                contentColor = Color.White,
                disabledContainerColor = Color(0xffaaaaaa),
                disabledContentColor = Color.White,
            ),
        modifier = modifier.size(height = 42.dp, width = 42.dp),
        contentPadding = PaddingValues(10.dp),
    ) {
        Text(
            text = text,
            fontSize = 22.sp,
        )
    }
}

@Preview
@Composable
private fun PageButtonPreview() {
    PageButton(
        text = ">",
        onClick = {},
        isEnable = true,
    )
}
