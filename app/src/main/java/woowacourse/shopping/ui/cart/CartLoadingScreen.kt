package woowacourse.shopping.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.common.skeleton

@Composable
fun CartLoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(324.dp)
            .height(152.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
                    .padding(end = 90.dp)
                    .skeleton()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(end = 175.dp)
                    .skeleton()
            )
        }
    }
}

@Preview
@Composable
fun CartLoadingScreenPreview() {
    CartLoadingScreen()
}
