package woowacourse.shopping.feature.goodsdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.IntentCompat
import woowacourse.shopping.R
import woowacourse.shopping.data.ShoppingDatabase
import woowacourse.shopping.data.carts.repository.CartRemoteDataSourceImpl
import woowacourse.shopping.data.carts.repository.CartRepositoryImpl
import woowacourse.shopping.data.goods.repository.GoodsLocalDataSourceImpl
import woowacourse.shopping.data.goods.repository.GoodsRemoteDataSourceImpl
import woowacourse.shopping.data.goods.repository.GoodsRepositoryImpl
import woowacourse.shopping.databinding.ActivityGoodsDetailsBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.feature.BaseActivity
import woowacourse.shopping.feature.CartUiModel
import woowacourse.shopping.feature.GoodsUiModel
import woowacourse.shopping.feature.QuantityChangeListener
import woowacourse.shopping.util.toUi

class GoodsDetailsActivity : BaseActivity<ActivityGoodsDetailsBinding>() {

    private lateinit var viewModel: GoodsDetailsViewModel
    private var mostRecentGoods: GoodsUiModel? = null

    override fun inflateBinding(): ActivityGoodsDetailsBinding =
        ActivityGoodsDetailsBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this

        val goodsUiModel = IntentCompat.getParcelableExtra(intent, GOODS_KEY, GoodsUiModel::class.java)
        if (goodsUiModel == null) {
            Toast.makeText(this, "상품 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val cartUiModel =
            IntentCompat.getParcelableExtra(intent, CART_KEY, CartUiModel::class.java)

        viewModel = GoodsDetailsViewModel(
            goodsUiModel,
            cartUiModel,
            CartRepositoryImpl(CartRemoteDataSourceImpl()),
            GoodsRepositoryImpl(
                GoodsRemoteDataSourceImpl(),
                GoodsLocalDataSourceImpl(ShoppingDatabase.getDatabase(this)),
            ),
        )

        binding.viewModel = viewModel

        binding.quantityChangeListener = object : QuantityChangeListener {
            override fun onIncrease(cartItem: CartItem) {
                viewModel.increaseSelectorQuantity()
            }

            override fun onDecrease(cartItem: CartItem) {
                viewModel.decreaseSelectorQuantity()
            }
        }

        observeUiEvent()
    }

    private fun observeUiEvent() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    val message = when (event.messageKey) {
                        ToastMessageKey.FAIL_CART_ADD -> getString(R.string.toast_fail_cart_add)
                        ToastMessageKey.FAIL_CART_UPDATE -> getString(R.string.toast_fail_cart_update)
                    }
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }

                is UiEvent.CartAddSuccess -> {
                    val message = getString(
                        R.string.goods_detail_cart_insert_complete_toast_message,
                        event.quantity
                    )
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }

                is UiEvent.ShowMostRecentlyViewed -> {
                    mostRecentGoods = event.goods.toUi()
                }

                is UiEvent.ClickMostRecentlyViewed -> {
                    mostRecentGoods?.let {
                        val intent = newIntent(this, it).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            putExtra(EXTRA_SOURCE, SOURCE_RECENTLY_VIEWED)
                        }
                        startActivity(intent)
                    }
                }
            }
        }
        viewModel.mostRecentlyViewedGoods.observe(this) { goods ->
            mostRecentGoods = goods.toUi()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_close, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_close) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val GOODS_KEY = "GOODS"
        const val CART_KEY = "CART"

        fun newIntent(
            context: Context,
            goods: GoodsUiModel,
        ): Intent = Intent(context, GoodsDetailsActivity::class.java).apply {
            putExtra(GOODS_KEY, goods)
        }

        const val EXTRA_SOURCE = "extra_source"
        const val SOURCE_RECENTLY_VIEWED = "recently_viewed"
        const val SOURCE_GOODS_LIST = "goods_list"
    }
}