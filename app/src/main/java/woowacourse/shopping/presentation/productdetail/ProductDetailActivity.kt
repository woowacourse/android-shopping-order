package woowacourse.shopping.presentation.productdetail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityDetailProductBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.presentation.Extra
import woowacourse.shopping.presentation.ResultState
import woowacourse.shopping.presentation.cart.CartCounterClickListener
import woowacourse.shopping.presentation.product.ItemClickListener

class ProductDetailActivity :
    AppCompatActivity(),
    ItemClickListener,
    CartCounterClickListener {
    private lateinit var binding: ActivityDetailProductBinding
    private val viewModel: ProductDetailViewModel by viewModels { ProductDetailViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_product)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        binding.cartCounterListener = this
        binding.itemClickListener = this

        initInsets()
        setupToolbar()

        val productId = intent.getLongExtra(Extra.KEY_PRODUCT_ID, 0L)

        initListeners()
        observeViewModel()
        viewModel.fetchData(productId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_product_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.action_product_detail_close -> {
                setResult(Activity.RESULT_OK)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.tbDetailProduct)
        supportActionBar?.title = null
    }

    private fun initListeners() {
        binding.btnProductDetailAddCart.setOnClickListener {
            viewModel.addToCart()
        }
    }

    private fun observeViewModel() {
        viewModel.insertProductResult.observe(this) { result ->
            when (result) {
                is ResultState.Success -> showToast(R.string.product_detail_add_cart_toast_insert_success)
                is ResultState.Failure -> showToast(R.string.product_detail_add_cart_toast_insert_fail)
                else -> Unit
            }
        }

        viewModel.toastMessage.observe(this) { resId ->
            showToast(resId)
        }
    }

    private fun showToast(
        @StringRes messageResId: Int,
    ) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newIntent(
            context: Context,
            productId: Long,
        ): Intent =
            Intent(context, ProductDetailActivity::class.java).apply {
                putExtra(Extra.KEY_PRODUCT_ID, productId)
            }
    }

    override fun onClickProductItem(productId: Long) {
        val intent =
            newIntent(this, productId).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        startActivity(intent)
    }

    override fun onClickAddToCart(cartItem: CartItem) {
    }

    override fun onClickPlus(id: Long) {
        Log.d("meeple_log", "onclick")
        viewModel.increaseQuantity()
    }

    override fun onClickMinus(id: Long) {
        viewModel.decreaseQuantity()
    }
}
