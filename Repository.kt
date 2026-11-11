package com.zyranto.app.data

import android.text.Html
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductRepository {
    suspend fun products(): List<Product>
    suspend fun placeOrder(items: Map<Int, Int>, info: DeliveryInfo): Result<String>
}

/** Sample data for DESIGN PREVIEW ONLY. Never creates orders. */
class DemoRepository : ProductRepository {
    override suspend fun products() = listOf(
        p(1, "Wireless Earbuds", 2000.0, 1499.0, 12, "Gadgets & Accessories"),
        p(2, "Smart Watch", 3500.0, 2499.0, 8, "Gadgets & Accessories"),
        p(3, "Backpack", 1199.0, null, 20, "Fashion"),
        p(4, "Sunglasses", 899.0, null, 15, "Fashion"),
        p(5, "Men's Casual Shirt", 1299.0, 999.0, 30, "Men Fashion"),
        p(6, "Women's Kurti", 1899.0, null, 10, "Women Fashion"),
        p(7, "Table Lamp", 1450.0, 1250.0, 0, "Home & Lifestyle"),
        p(8, "Gift Box Set", 1999.0, null, 6, "Gifts & More")
    )

    private fun p(id: Int, n: String, reg: Double, sale: Double?, stock: Int, cat: String) =
        Product(id, n, "DEMO-$id", "Sample product for design preview.", null, reg, sale, stock, cat)

    override suspend fun placeOrder(items: Map<Int, Int>, info: DeliveryInfo): Result<String> =
        Result.failure(IllegalStateException("Demo mode: no real order was created. Connect the store backend to place orders."))
}

// ---- WooCommerce Store API (public, read-only, no secret keys needed) ----
data class StorePrices(
    @SerializedName("regular_price") val regular: String?,
    @SerializedName("sale_price") val sale: String?,
    @SerializedName("currency_minor_unit") val minor: Int?
)
data class StoreImage(val src: String?)
data class StoreCat(val name: String?)
data class StoreProduct(
    val id: Int,
    val name: String?,
    val sku: String?,
    val description: String?,
    val prices: StorePrices?,
    val images: List<StoreImage>?,
    val categories: List<StoreCat>?,
    @SerializedName("on_sale") val onSale: Boolean?,
    @SerializedName("is_in_stock") val inStock: Boolean?,
    @SerializedName("low_stock_remaining") val lowStock: Int?
)

interface StoreApi {
    @GET("wp-json/wc/store/v1/products")
    suspend fun products(@Query("per_page") perPage: Int = 100): List<StoreProduct>
}

class WooRepository(baseUrl: String) : ProductRepository {
    private val api = Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create()).build().create(StoreApi::class.java)

    override suspend fun products(): List<Product> = api.products().map { s ->
        val div = Math.pow(10.0, (s.prices?.minor ?: 2).toDouble())
        val reg = (s.prices?.regular?.toDoubleOrNull() ?: 0.0) / div
        val sale = if (s.onSale == true) (s.prices?.sale?.toDoubleOrNull() ?: 0.0) / div else null
        Product(
            id = s.id, name = s.name.orEmpty(), sku = s.sku.orEmpty(),
            description = Html.fromHtml(s.description.orEmpty(), Html.FROM_HTML_MODE_COMPACT).toString().trim(),
            imageUrl = s.images?.firstOrNull()?.src,
            regularPrice = reg, salePrice = sale,
            // Store API exposes only in-stock flag (+ low-stock count). Exact qty needs the secure backend (Stage 4).
            stock = if (s.inStock == true) (s.lowStock ?: 99) else 0,
            category = s.categories?.firstOrNull()?.name.orEmpty()
        )
    }

    override suspend fun placeOrder(items: Map<Int, Int>, info: DeliveryInfo): Result<String> =
        Result.failure(UnsupportedOperationException("Order creation needs the secure backend/plugin (Stage 4). Not connected yet."))
}
