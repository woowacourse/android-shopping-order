package woowacourse.shopping.ui.cart

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.remote.RemoteCartRepository
import woowacourse.shopping.data.product.remote.retrofit.RemoteProductRepository
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.ui.cart.adapter.CartAdapter

class CartActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCartBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<CartViewModel> {
        CartViewModelFactory(
            RemoteProductRepository(),
            RemoteCartRepository(),
        )
    }
    private val adapter by lazy { CartAdapter(viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initializeView()
    }

    private fun initializeView() {
        initializeToolbar()
        initializeCartAdapter()
        viewModel.changedCartEvent.observe(this) {
            it.getContentIfNotHandled() ?: return@observe
            setResult(Activity.RESULT_OK)
        }
    }

    private fun initializeToolbar() {
        binding.toolbarCart.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initializeCartAdapter() {
        binding.rvCart.itemAnimator = null
        binding.rvCart.adapter = adapter

        viewModel.cartUiState.observe(this) {
            val cartUiState = it.getContentIfNotHandled() ?: return@observe
            when (cartUiState) {
                CartUiState.Failure -> {
                    Toast.makeText(
                        this,
                        R.string.load_page_error,
                        Toast.LENGTH_SHORT,
                    ).show()
                }

                CartUiState.Loading -> {
                    binding.layoutCartSkeleton.visibility = View.VISIBLE
                    binding.rvCart.visibility = View.GONE
                }

                is CartUiState.Success -> {
                    binding.layoutCartSkeleton.visibility = View.GONE
                    binding.rvCart.visibility = View.VISIBLE
                    adapter.submitList(cartUiState.cartUiModels)
                }
            }
        }
    }
}
