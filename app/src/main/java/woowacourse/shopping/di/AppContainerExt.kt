package woowacourse.shopping.di

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import woowacourse.shopping.ShoppingApplication

@Composable
fun appContainer(): AppContainer {
    val context = LocalContext.current
    return (context.applicationContext as ShoppingApplication).appContainer
}
