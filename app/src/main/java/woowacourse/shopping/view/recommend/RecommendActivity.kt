package woowacourse.shopping.view.recommend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityRecommnedBinding
import woowacourse.shopping.view.productDetail.ProductDetailActivity
import woowacourse.shopping.view.showToast

class RecommendActivity :
    AppCompatActivity(),
    RecommendProductItemActions {
    private val binding by lazy { ActivityRecommnedBinding.inflate(layoutInflater) }
    private val viewModel: RecommendViewModel by viewModels()
    private val recommendProductsAdapter by lazy {
        RecommendProductsAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initBinding()

        initObserve()
    }

    private fun initObserve() {
        viewModel.recommendProducts.observe(this) { recommendProducts ->
            recommendProductsAdapter.asdf(recommendProducts)
        }
        viewModel.event.observe(this) { event: RecommendEvent ->
            when (event) {
                RecommendEvent.LOAD_RECENT_PRODUCTS_FAILURE ->
                    showToast(R.string.product_recommend_load_recent_products_error_message)

                RecommendEvent.LOAD_SHOPPING_CART_FAILURE ->
                    showToast(R.string.product_recommend_add_shopping_cart_error_message)

                RecommendEvent.LOAD_PRODUCT_FAILURE ->
                    showToast(R.string.product_recommend_load_products_by_category)

                RecommendEvent.PLUS_CART_ITEM_FAILURE ->
                    showToast(R.string.product_recommend_add_shopping_cart_error_message)

                RecommendEvent.REMOVE_CART_ITEM_FAILURE ->
                    showToast(R.string.product_recommend_remove_shopping_cart_product_error_message)

                RecommendEvent.MINUS_CART_ITEM_FAILURE ->
                    showToast(R.string.shopping_cart_update_shopping_cart_quantity_error_message)
            }
        }
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.recommendCartItems.adapter = recommendProductsAdapter
        binding.viewModel = viewModel
        binding.onClickBackButton = {
            setResult(RESULT_OK)
            finish()
        }
        onBackPressedDispatcher.addCallback {
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onSelectProduct(item: RecommendProduct) {
        val intent = ProductDetailActivity.newIntent(this, item.productId)
        startActivity(intent)
    }

    override fun onPlusProductQuantity(item: RecommendProduct) {
        viewModel.plusCartItemQuantity(
            productId = item.productId,
            quantity = item.quantity + 1,
        )
    }

    override fun onMinusProductQuantity(item: RecommendProduct) {
        viewModel.minusCartItemQuantity(
            productId = item.productId,
            quantity = item.quantity - 1,
        )
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, RecommendActivity::class.java)
            return intent
        }
    }
}
