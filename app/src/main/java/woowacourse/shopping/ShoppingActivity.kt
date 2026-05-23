package woowacourse.shopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import woowacourse.shopping.di.AppViewModelFactory
import woowacourse.shopping.ui.navigation.ShoppingNavHost
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

class ShoppingActivity : ComponentActivity() {
    private val app: ShoppingApplication by lazy { application as ShoppingApplication }

    private val viewModelFactory: AppViewModelFactory by lazy {
        AppViewModelFactory(
            appContainer = app.appContainer,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AndroidShoppingTheme {
                ShoppingNavHost(viewModelFactory = viewModelFactory)
            }
        }
    }
}