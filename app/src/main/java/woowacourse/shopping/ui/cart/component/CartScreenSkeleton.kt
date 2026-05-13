package woowacourse.shopping.ui.cart.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun CartScreenSkeleton(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        CartHeader { }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 18.dp, end = 18.dp, top = 24.dp)
                .shimmer(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            repeat(5) {
                CartItem()
            }
        }
    }
}

@Composable
private fun CartItem(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 324.dp, height = 152.dp)
            .background(Color(0xFFF3F3F3))
    ) {
        Column(
            modifier = Modifier.padding(top = 17.88.dp, bottom = 17.88.dp, start = 15.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 220.dp, height = 25.38.dp)
                    .background(Color(0xFFE2E2E2))
            )
            Spacer(Modifier.size(18.88.dp))
            Box(
                modifier = Modifier
                    .size(width = 134.dp, height = 71.53.dp)
                    .background(Color(0xFFE2E2E2))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CartScreenSkeletonPreview() {
    CartScreenSkeleton()
}
