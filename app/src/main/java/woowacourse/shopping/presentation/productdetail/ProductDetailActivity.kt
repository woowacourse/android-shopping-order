package woowacourse.shopping.presentation.productdetail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import woowacourse.shopping.presentation.CartItemUiModel
import woowacourse.shopping.presentation.Extra
import woowacourse.shopping.presentation.ResultState
import woowacourse.shopping.presentation.product.ItemClickListener

class ProductDetailActivity :
    AppCompatActivity(),
    ItemClickListener {
    private lateinit var binding: ActivityDetailProductBinding
    private val viewModel: ProductDetailViewModel by viewModels { ProductDetailViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_product)
        binding.lifecycleOwner = this
        binding.vm = viewModel
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
            intentWithInfos()
        }
    }

    private fun intentWithInfos() {
        val resultIntent =
            Intent().apply {
                putExtra(Extra.KEY_PRODUCT_ID, viewModel.product.value?.productId ?: -1)
                putExtra(Extra.KEY_PRODUCT_ADD_QUANTITY, viewModel.productCount.value ?: 0)
            }
        setResult(RESULT_OK, resultIntent)
        finish()
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

    override fun onClickProductItem(productId: Long) {
        val intent =
            newIntent(this, productId).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        startActivity(intent)
    }

    override fun onClickAddToCart(cartItemUiModel: CartItemUiModel) {
        // TODO: 여기에는 이 인터페이스가 필요 없다. 매개변수가 달라 vm 내에서 메서드 생성 후 처리
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
}
