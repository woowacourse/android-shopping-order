package woowacourse.shopping.view.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.databinding.ActivityShoppingCartBinding
import woowacourse.shopping.view.cart.select.CartProductSelectFragment

class ShoppingCartActivity : AppCompatActivity() {
    private val binding by lazy { ActivityShoppingCartBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory =
            ShoppingCartFragmentFactory(application as ShoppingApplication)
        super.onCreate(savedInstanceState)
        setUpView()
        supportActionBar?.title = ACTION_BAR_TITLE

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(
                    R.id.fragment,
                    CartProductSelectFragment::class.java,
                    null,
                )
            }
        }
    }

    private fun setUpView() {
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, ShoppingCartActivity::class.java)

        private const val ACTION_BAR_TITLE = "Cart"
    }
}
