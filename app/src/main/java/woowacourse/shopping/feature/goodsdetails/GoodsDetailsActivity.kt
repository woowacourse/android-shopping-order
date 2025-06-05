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

        val goodsUiModel = intent.getParcelableExtra<GoodsUiModel>(GOODS_KEY)
        if (goodsUiModel == null) {
            Toast.makeText(this, R.string.toast_fail_load_goods, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val cartUiModel = intent.getParcelableExtra<CartUiModel>(CART_KEY)

        viewModel = GoodsDetailsViewModel(
            goodsUiModel,
            cartUiModel,
            CartRepositoryImpl(CartRemoteDataSourceImpl()),
            GoodsRepositoryImpl(
                GoodsRemoteDataSourceImpl(),
                GoodsLocalDataSourceImpl(ShoppingDatabase.getDatabase(this))
            )
        )

        binding.viewModel = viewModel
        binding.quantityChangeListener = object : QuantityChangeListener {
            override fun onIncrease(cartItem: CartItem) = viewModel.increaseSelectorQuantity()
            override fun onDecrease(cartItem: CartItem) = viewModel.decreaseSelectorQuantity()
        }

        observeEvent()
        viewModel.mostRecentlyViewedGoods.observe(this) {
            mostRecentGoods = it.toUi()
        }
    }

    private fun observeEvent() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    val msgResId = when (event.messageKey) {
                        ToastMessageKey.FAIL_CART_ADD -> R.string.toast_fail_cart_add
                        ToastMessageKey.FAIL_CART_UPDATE -> R.string.toast_fail_cart_update
                    }
                    Toast.makeText(this, getString(msgResId), Toast.LENGTH_SHORT).show()
                }

                is UiEvent.CartAddSuccess -> {
                    val message = getString(R.string.goods_detail_cart_insert_complete_toast_message, event.quantity)
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }

                is UiEvent.ShowMostRecentlyViewed -> {
                    mostRecentGoods = event.goods.toUi()
                }

                UiEvent.ClickMostRecentlyViewed -> {
                    mostRecentGoods?.let {
                        startActivity(
                            newIntent(this, it).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                putExtra(EXTRA_SOURCE, SOURCE_RECENTLY_VIEWED)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_close, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_close) finish()
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val GOODS_KEY = "GOODS"
        const val CART_KEY = "CART"
        const val EXTRA_SOURCE = "extra_source"
        const val SOURCE_RECENTLY_VIEWED = "recently_viewed"
        const val SOURCE_GOODS_LIST = "goods_list"

        fun newIntent(context: Context, goods: GoodsUiModel): Intent =
            Intent(context, GoodsDetailsActivity::class.java).apply {
                putExtra(GOODS_KEY, goods)
            }
    }
}