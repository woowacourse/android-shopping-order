package woowacourse.shopping.ui.shopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import woowacourse.shopping.ui.navigation.AppNavHost
import woowacourse.shopping.ui.theme.ShoppingTheme

class ShoppingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            ShoppingTheme {
                AppNavHost(navController = navController)
            }
        }
    }
}
