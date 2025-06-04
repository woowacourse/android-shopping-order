package woowacourse.shopping.view.shoppingCartRecommend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import woowacourse.shopping.databinding.ActivityShoppingCartRecommendBinding
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.view.common.QuantityObservable
import woowacourse.shopping.view.common.ResultFrom
import woowacourse.shopping.view.common.getSerializableExtraData
import woowacourse.shopping.view.product.ProductsItem

class ShoppingCartRecommendActivity :
    AppCompatActivity(),
    RecommendProductListener {
    private val binding: ActivityShoppingCartRecommendBinding by lazy {
        ActivityShoppingCartRecommendBinding.inflate(layoutInflater)
    }
    private lateinit var shoppingCartProductsToOrder: List<ShoppingCartProduct>
    private val viewModel: ShoppingCartRecommendViewModel by viewModels {
        ShoppingCartRecommendViewModel.factory(
            shoppingCartProductsToOrder,
        )
    }

    private val recommendProductAdapter: RecommendProductAdapter by lazy {
        RecommendProductAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.shoppingCartRecommendRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        shoppingCartProductsToOrder =
            intent
                .getSerializableExtraData<Array<ShoppingCartProduct>>(
                    EXTRA_SHOPPING_CART_PRODUCTS_TO_ORDER_KEY,
                )?.toList() ?: emptyList()

        bindViewModel()
        setUpObservers()
    }

    private fun bindViewModel() {
        binding.lifecycleOwner = this
        binding.viewModel = this.viewModel
        binding.adapter = recommendProductAdapter
        binding.recommendListener = this
    }

    private fun setUpObservers() {
        viewModel.recommendProducts.observe(this) { recommendItems ->
            recommendProductAdapter.submitList(recommendItems)
        }
    }

    override fun onPlusShoppingCartClick(quantityObservable: QuantityObservable) {
        val item = quantityObservable as ProductsItem.ProductItem
        viewModel.addProductToShoppingCart(item, item.selectedQuantity)
    }

    override fun onMinusShoppingCartClick(quantityObservable: QuantityObservable) {
        val item = quantityObservable as ProductsItem.ProductItem
        viewModel.minusProductToShoppingCart(item, item.selectedQuantity)
    }

    override fun onBackButtonClick() {
        setResult(ResultFrom.RECOMMEND_PRODUCT_BACK.RESULT_OK)
        finish()
    }

    override fun onOrderButtonClick() {
        // TOOD: 주문하기 api 연동
    }

    companion object {
        private const val EXTRA_SHOPPING_CART_PRODUCTS_TO_ORDER_KEY =
            "woowacourse.shopping.EXTRA_SHOPPING_CART_PRODUCTS_TO_ORDER_KEY"

        fun newIntent(
            context: Context,
            shoppingCartProductsToOrder: Array<ShoppingCartProduct>,
        ): Intent =
            Intent(context, ShoppingCartRecommendActivity::class.java).apply {
                putExtra(EXTRA_SHOPPING_CART_PRODUCTS_TO_ORDER_KEY, shoppingCartProductsToOrder)
            }
    }
}
