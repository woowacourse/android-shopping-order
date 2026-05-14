package woowacourse.shopping.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CartSkeletonItem() {
    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .height(152.dp)
            .padding(vertical = 18.dp, horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(17.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(end = 90.dp)
                .fillMaxWidth()
                .height(23.dp)
                .background(Color.Gray)
        )
        Box(
            modifier = Modifier
                .padding(end = 175.dp)
                .fillMaxWidth()
                .height(72.dp)
                .background(Color.Gray)
        )
    }
}


@Preview
@Composable
private fun CartSkeletonItemPreview() {
    CartSkeletonItem()
}

