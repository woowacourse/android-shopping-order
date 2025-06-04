package woowacourse.shopping.feature.goodsdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        viewModel.alertEvent.observe(this) { goodsDetailsAlertMessage ->
            Toast.makeText(
                this,
                getString(goodsDetailsAlertMessage.resourceId, goodsDetailsAlertMessage.quantity),
                Toast.LENGTH_SHORT
            ).show()
        }

        viewModel.clickMostRecentlyGoodsEvent.observe(this) { mostRecentGoods ->
            val intent = newIntent(this, mostRecentGoods.toUi()).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_SOURCE, SOURCE_RECENTLY_VIEWED)
            }
            startActivity(intent)
        }

        observeToast(viewModel.toastMessage)
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