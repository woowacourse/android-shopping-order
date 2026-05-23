package woowacourse.shopping.ui.shopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.ui.common.theme.ShoppingTheme
import woowacourse.shopping.ui.nav.AppNavHost

class ShoppingActivity : ComponentActivity() {
    private val container by lazy {
        (application as ShoppingApplication).appContainer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoppingTheme {
                AppNavHost(container = container)
            }
        }
    }
}
