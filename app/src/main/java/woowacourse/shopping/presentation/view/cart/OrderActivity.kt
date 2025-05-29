package woowacourse.shopping.presentation.view.cart

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

class OrderActivity :
    AppCompatActivity(),
    OrderNavigator {
    private lateinit var binding: ActivityOrderBinding
    private var currentFragmentTag: String? = null
    private val viewModel: OrderViewModel by viewModels { OrderViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setWindowInsets()
        setupListeners()

        if (savedInstanceState != null) {
            currentFragmentTag = savedInstanceState.getString(KEY_CURRENT_FRAGMENT_TAG)
            return
        }

        showFragment(CartFragment::class.java)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentFragmentTag?.let { outState.putString(KEY_CURRENT_FRAGMENT_TAG, it) }
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
        val fragmentManager = supportFragmentManager

        fragmentManager.commit {
            setReorderingAllowed(true)
            currentFragmentTag?.let { fragmentManager.findFragmentByTag(it)?.let(::hide) }

            val targetFragment = fragmentManager.findFragmentByTag(tag)
            if (targetFragment != null) {
                show(targetFragment)
            } else {
                add(R.id.cart_fragment_container, fragmentClass, null, tag)
            }
            currentFragmentTag = tag
        }
    }

    private fun setupListeners() {
        binding.viewOrder.buttonOrder.setOnClickListener {
            orderButtonHandler()
        }
        binding.viewOrder.checkboxSelectAll.setOnCheckedChangeListener { _, checked ->
            viewModel.selectCurrentPageCartProduct(checked)
        }
    }

    private fun orderButtonHandler() {
        when (currentFragmentTag) {
            CartFragment::class.simpleName -> showFragment(SuggestionFragment::class.java)
            SuggestionFragment::class.simpleName -> {
                // TODO: 주문 기능 구현
            }
        }
    }

    companion object {
        private const val KEY_CURRENT_FRAGMENT_TAG = "current_fragment_tag"

        fun newIntent(context: Context): Intent = Intent(context, OrderActivity::class.java)
    }
}
