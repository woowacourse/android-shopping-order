package woowacourse.shopping.ui.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.core.designsystem.theme.AndroidshoppingTheme
import woowacourse.shopping.ui.navigation.ShoppingNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val shoppingApplication = application as ShoppingApplication

        setContent {
            AndroidshoppingTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                ) { innerPadding ->
                    ShoppingNavHost(
                        navController = navController,
                        shoppingApplication = shoppingApplication,
                        contentPadding = innerPadding,
                        snackbarHostState = snackbarHostState,
                    )
                }
            }
        }
    }
}
