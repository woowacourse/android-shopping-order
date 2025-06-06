package woowacourse.shopping.presentation.cart

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.presentation.Extra
import woowacourse.shopping.presentation.ResultState
import woowacourse.shopping.presentation.recommend.RecommendActivity

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels { CartViewModelFactory() }
    private val cartAdapter by lazy {
        CartAdapter(
            cartCounterClickListener = viewModel,
            cartPageClickListener = viewModel,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        binding.vm = viewModel
        binding.clickListener = viewModel
        binding.lifecycleOwner = this

        setOnBackPressedCallback()
        showSkeleton(true)
        initInsets()
        initAdapter()
        setupToolbar()
        observeViewModel()
    }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.clCart) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initAdapter() {
        binding.rvCartProduct.apply {
            adapter = cartAdapter
            itemAnimator = null
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.tbCart)
        binding.tbCart.apply {
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                val intent =
                    Intent().apply {
                        putExtra(Extra.KEY_CART_IS_UPDATE, viewModel.isUpdated)
                    }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun setOnBackPressedCallback() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent =
                        Intent().apply {
                            putExtra(Extra.KEY_CART_IS_UPDATE, viewModel.isUpdated)
                        }
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            },
        )
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showSkeleton(true)
                }

                is ResultState.Success -> {
                    showSkeleton(false)
                }

                is ResultState.Failure -> {
                    showSkeleton(true)
                }
            }
        }

        viewModel.cartItems.observe(this) { cartItems ->
            binding.root.postDelayed({
                showSkeleton(false)
            }, 1_000L)
            cartAdapter.submitList(cartItems)
        }

        viewModel.toastMessage.observe(this) { resId ->
            showToast(resId)
        }

        viewModel.navigateTo.observe(this) { (totalPrice, totalCount) ->
            val intent =
                RecommendActivity.newIntent(this, totalPrice, totalCount)
            recommendLauncher.launch(intent)
        }
    }

    private val recommendLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val isUpdated =
                    result.data?.getBooleanExtra(Extra.KEY_RECOMMEND_IS_UPDATE, true) ?: true
                if (isUpdated) {
                    viewModel.loadItems()
                }
            }
        }

    private fun showSkeleton(isLoading: Boolean) {
        if (isLoading) {
            binding.rvCartProduct.visibility = View.GONE
        } else {
            binding.rvCartProduct.visibility = View.VISIBLE
            binding.shimmerLayoutCart.visibility = View.GONE
        }
    }

    private fun showToast(
        @StringRes messageResId: Int,
    ) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, CartActivity::class.java)
    }
}
