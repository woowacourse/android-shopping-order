package woowacourse.shopping.presentation.cart

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import woowacourse.shopping.presentation.ResultState
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.recommend.RecommendActivity

class CartActivity :
    AppCompatActivity(),
    CartPageClickListener,
    CartCounterClickListener {
    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels { CartViewModelFactory() }
    private val cartAdapter by lazy {
        CartAdapter(
            cartCounterClickListener = this,
            cartPageClickListener = this,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        binding.vm = viewModel
        binding.clickListener = this
        binding.lifecycleOwner = this

        initInsets()
        initAdapter()
        setupToolbar()
        observeViewModel()
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.loadItems()
        viewModel.fetchSelectedInfo()
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
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
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
            cartAdapter.submitList(cartItems)
            viewModel.fetchSelectedInfo()
        }

        viewModel.toastMessage.observe(this) { resId ->
            showToast(resId)
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

    override fun onClickPlus(id: Long) {
        viewModel.increaseQuantity(id)
    }

    override fun onClickMinus(id: Long) {
        viewModel.decreaseQuantity(id)
    }

    override fun onClickDelete(cartItem: CartItemUiModel) {
        viewModel.deleteProduct(cartItem)
    }

    override fun onClickSelect(cartId: Long) {
        viewModel.toggleItemChecked(cartId)
    }

    override fun onClickCheckAll() {
        viewModel.toggleItemCheckAll()
    }

    override fun onClickRecommend() {
        val intent =
            RecommendActivity.newIntent(
                this,
                viewModel.selectedTotalPrice.value ?: 0,
                viewModel.selectedTotalCount.value ?: 0,
            )
        activityResultLauncher.launch(intent)
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.loadItems()
                viewModel.fetchSelectedInfo()
            }
        }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, CartActivity::class.java)
    }
}
