package woowacourse.shopping.view.productdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.CartRemoteRepository
import woowacourse.shopping.data.repository.ProductRemoteRepository
import woowacourse.shopping.data.repository.RecentViewedDbRepository
import woowacourse.shopping.data.repository.ServerPreferencesRepository
import woowacourse.shopping.databinding.ActivityProductDetailBinding
import woowacourse.shopping.databinding.DialogQuantityBinding
import woowacourse.shopping.model.ProductModel
import woowacourse.shopping.view.productlist.ProductListActivity.Companion.ID
import woowacourse.shopping.view.productlist.ProductListActivity.Companion.RESULT_ADDED
import woowacourse.shopping.view.productlist.ProductListActivity.Companion.RESULT_VIEWED

class ProductDetailActivity : AppCompatActivity(), ProductDetailContract.View {
    private val productDetailBinding: ActivityProductDetailBinding by lazy { ActivityProductDetailBinding.inflate(layoutInflater) }
    private val dialogBinding: DialogQuantityBinding by lazy { DialogQuantityBinding.inflate(layoutInflater) }
    private lateinit var dialog: AlertDialog
    private lateinit var presenter: ProductDetailContract.Presenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val productId = intent.getIntExtra(PRODUCT_ID, -1)
        setUpPresenter(productId)
        setContentView(productDetailBinding.root)
        if (productId == -1) {
            forceQuit()
            return
        }
        intent.putExtra(ID, productId)
        presenter.fetchProductDetail()
    }

    private fun setUpPresenter(id: Int) {
        val url = ServerPreferencesRepository(this).getServerUrl()
        presenter =
            ProductDetailPresenter(
                INITIAL_COUNT,
                id,
                this,
                ProductRemoteRepository(url),
                CartRemoteRepository(url),
                RecentViewedDbRepository(this),
            )
    }

    private fun forceQuit() {
        Toast.makeText(this, NOT_EXIST_DATA_ERROR, Toast.LENGTH_LONG).show()
        finish()
    }

    override fun showProductDetail(product: ProductModel, lastViewedProduct: ProductModel?) {
        setUpDialog(product)
        if (lastViewedProduct != null) showLastViewedProduct(lastViewedProduct)
        productDetailBinding.product = product
        Glide.with(productDetailBinding.root.context).load(product.imageUrl)
            .into(productDetailBinding.imgProduct)
        productDetailBinding.btnPutInCart.setOnClickListener {
            dialog.show()
        }
    }

    private fun showLastViewedProduct(lastViewedProduct: ProductModel) {
        productDetailBinding.lastViewedProduct = lastViewedProduct
        productDetailBinding.layoutLastViewed.setOnClickListener {
            startLastViewedDetailActivity(lastViewedProduct)
        }
    }

    private fun setUpDialog(product: ProductModel) {
        dialogBinding.lifecycleOwner = this
        dialogBinding.presenter = presenter
        dialogBinding.product = product
        dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()
    }

    private fun setUpResult(isAdd: Boolean) {
        if (isAdd) {
            setResult(RESULT_ADDED, intent)
        } else {
            setResult(RESULT_VIEWED, intent)
        }
    }

    override fun finishActivity(isAdd: Boolean) {
        setUpResult(isAdd)
        dialog.dismiss()
        finish()
    }

    private fun startLastViewedDetailActivity(lastViewedProduct: ProductModel) {
        val nextIntent = newIntent(this, lastViewedProduct)
        nextIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(nextIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_close, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.close -> {
                finishActivity(false)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val INITIAL_COUNT = 1
        private const val PRODUCT_ID = "PRODUCT_ID"
        private const val NOT_EXIST_DATA_ERROR = "데이터가 넘어오지 않았습니다."

        fun newIntent(
            context: Context,
            product: ProductModel,
        ): Intent {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra(PRODUCT_ID, product.id)
            return intent
        }
    }
}
