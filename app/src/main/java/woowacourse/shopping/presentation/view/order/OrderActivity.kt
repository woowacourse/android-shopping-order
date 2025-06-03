package woowacourse.shopping.presentation.view.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.view.order.cart.CartFragment
import woowacourse.shopping.presentation.view.order.suggestion.SuggestionFragment

class OrderActivity :
    AppCompatActivity(),
    OrderNavigator {
    private lateinit var binding: ActivityOrderBinding
    private val viewModel: OrderViewModel by viewModels { OrderViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setWindowInsets()
        setupListeners()

        if (savedInstanceState == null) showFragment(CartFragment::class.java)
    }

    override fun navigateToCart() = showFragment(CartFragment::class.java)

    private fun initBinding() {
        binding = ActivityOrderBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        setContentView(binding.root)
    }

    private fun setWindowInsets() {
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun <T : Fragment> showFragment(fragmentClass: Class<T>) {
        val tag = fragmentClass.simpleName

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            currentFragment()?.let { hide(it) }

            val targetFragment = supportFragmentManager.findFragmentByTag(tag)
            if (targetFragment != null) {
                show(targetFragment)
            } else {
                add(R.id.cart_fragment_container, fragmentClass, null, tag)
            }
        }
    }

    private fun setupListeners() {
        binding.viewOrder.buttonOrder.setOnClickListener { orderButtonHandler() }
    }

    private fun orderButtonHandler() {
        when (currentFragment()) {
            is CartFragment -> showFragment(SuggestionFragment::class.java)
            is SuggestionFragment -> {
                // TODO: 주문 기능 구현
            }
        }
    }

    private fun currentFragment(): Fragment? = supportFragmentManager.fragments.firstOrNull { it.isVisible }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, OrderActivity::class.java)
    }
}
