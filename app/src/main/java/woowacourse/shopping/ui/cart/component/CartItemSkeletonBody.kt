package woowacourse.shopping.ui.cart.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import woowacourse.shopping.ui.theme.ShoppingColors

@Composable
fun CartItemSkeletonBody(
    modifier: Modifier = Modifier,
    itemCount: Int = 2,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(itemCount) {
            CartItemSkeletonUnit()
        }
    }
}

@Composable
fun CartItemSkeletonUnit(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White)
                .border(1.dp, ShoppingColors.Gray2, RoundedCornerShape(4.dp))
                .padding(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .width(120.dp)
                        .height(18.dp)
                        .background(ShoppingColors.Gray1),
            )

            Box(
                modifier =
                    Modifier
                        .size(18.dp)
                        .background(ShoppingColors.Gray1),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier =
                    Modifier
                        .width(136.dp)
                        .height(72.dp)
                        .background(ShoppingColors.Gray1),
            )

            Column(
                modifier = Modifier.height(72.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End,
            ) {
                Box(
                    modifier =
                        Modifier
                            .width(92.dp)
                            .height(32.dp)
                            .background(ShoppingColors.Gray1),
                )

                Box(
                    modifier =
                        Modifier
                            .width(72.dp)
                            .height(16.dp)
                            .background(ShoppingColors.Gray1),
                )
            }
        }
    }
}
