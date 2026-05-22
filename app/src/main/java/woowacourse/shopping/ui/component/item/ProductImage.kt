package woowacourse.shopping.ui.component.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import woowacourse.shopping.R

@Composable
fun ProductImage(
    imageUri: String,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = imageUri,
        contentDescription = stringResource(R.string.cd_product_image),
        modifier = modifier,
        error = painterResource(R.drawable.not_found_error),
        fallback = painterResource(R.drawable.not_found_error),
    )
}
