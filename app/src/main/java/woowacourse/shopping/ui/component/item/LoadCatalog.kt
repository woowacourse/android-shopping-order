package woowacourse.shopping.ui.component.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoadCatalog(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(bottom = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        LoadItemRow()
        LoadItemRow()
        LoadItemRow()
    }
}

@Composable
fun LoadItem(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .width(154.dp)
                .height(206.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .skeleton(),
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .padding(end = 12.dp)
                    .skeleton(),
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .padding(end = 36.dp)
                    .skeleton(),
        )
    }
}

@Composable
fun LoadItemRow(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        LoadItem()
        LoadItem()
    }
}

@Preview
@Composable
fun LoadCatalogPreview() {
    LoadCatalog()
}
