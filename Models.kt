package com.zyranto.app.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class Product(
    val id: Int,
    val name: String,
    val sku: String,
    val description: String,
    val imageUrl: String?,
    val regularPrice: Double,
    val salePrice: Double?,
    val stock: Int,
    val category: String,
    val status: String = "publish"
) {
    val price: Double get() = salePrice ?: regularPrice
    val onSale: Boolean get() = salePrice != null && salePrice < regularPrice
    val inStock: Boolean get() = stock > 0
}

data class DeliveryInfo(val name: String, val phone: String, val address: String, val city: String)

data class Cat(val name: String, val icon: ImageVector)

object Categories {
    val all = listOf(
        Cat("Fashion", Icons.Filled.Checkroom),
        Cat("Women Fashion", Icons.Filled.Woman),
        Cat("Men Fashion", Icons.Filled.Man),
        Cat("Gadgets & Accessories", Icons.Filled.Laptop),
        Cat("Home & Lifestyle", Icons.Filled.Home),
        Cat("Gifts & More", Icons.Filled.CardGiftcard)
    )
}

object Config {
    // TODO: replace with the real business contact details
    const val WHATSAPP_NUMBER = "8801XXXXXXXXX"
    const val SUPPORT_EMAIL = "support@example.com"
}
