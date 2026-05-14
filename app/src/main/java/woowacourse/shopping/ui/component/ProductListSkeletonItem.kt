@file:Suppress("FunctionName")

package woowacourse.shopping.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProductListSkeletonItem() {
    Column(
        modifier =
            Modifier
                .size(154.dp, 206.dp),
        verticalArrangement = Arrangement.spacedBy(9.27.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(154.dp, 159.dp)
                    .background(Color.LightGray),
        )
        Box(
            modifier =
                Modifier
                    .size(140.dp, 14.dp)
                    .background(Color.LightGray),
        )
        Box(
            modifier =
                Modifier
                    .size(118.dp, 14.dp)
                    .background(Color.LightGray),
        )
    }
}

@Preview
@Composable
private fun ProductListSkeletonItemPreview() {
    ProductListSkeletonItem()
}
