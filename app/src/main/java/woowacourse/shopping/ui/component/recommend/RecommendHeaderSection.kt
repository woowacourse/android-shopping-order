package woowacourse.shopping.ui.component.recommend

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

@Composable
fun RecommendHeaderSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
    ) {
        Text(
            text = "이런 상품은 어떠세요?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "* 최근 본 상품 기반으로 좋아하실 것 같은 상품들을 추천해드려요.",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun RecommendHeaderSectionPreview() {
    AndroidShoppingTheme {
        RecommendHeaderSection()
    }
}
