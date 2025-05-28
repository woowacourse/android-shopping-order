package woowacourse.shopping.view.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.databinding.ActivityShoppingCartBinding
import woowacourse.shopping.view.cart.selection.CartProductSelectionFragment
import woowacourse.shopping.view.cart.selection.CartProductSelectionFragmentFactory

class ShoppingCartActivity : AppCompatActivity() {
    private val binding by lazy { ActivityShoppingCartBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        val app = application as ShoppingApplication
        ViewModelProvider(
            this,
            ShoppingCartViewModelFactory(app.cartProductRepository),
        )[ShoppingCartViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = CartProductSelectionFragmentFactory(viewModel)
        super.onCreate(savedInstanceState)

        setUpView()
        supportActionBar?.title = ACTION_BAR_TITLE

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.fragment, CartProductSelectionFragment::class.java, null)
            }
        }

        binding.handler = viewModel
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
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
