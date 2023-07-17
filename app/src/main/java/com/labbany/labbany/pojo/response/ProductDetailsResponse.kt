package com.labbany.labbany.pojo.response

data class ProductDetailsResponse(
    val code: Int,
    val `data`: Data,
    val msg: String,
    val success: Boolean
) {

    data class Data(
        val product_detail: List<ProductDetail>
    )

    data class ProductDetail(
        val cut_piece: List<CutPiece>,
        val extra: List<Extra>,
        val packing: List<Packing>,
        val productDescription: String,
        val productId: Int,
        val productLogo: String,
        val productName: String,
        val productNameUrdu: String,
        val productType: Int,
        val size: List<Size>
    )

    data class CutPiece(
        val cut_piece_id: Int,
        val cut_piece_price: String?,
        val cut_piece_type: String,
        val display_order: String
    )

    data class Extra(
        val display_order: String,
        val extra_id: Int,
        val extra_price: String?,
        val extra_type: String
    )

    data class Packing(
        val display_order: String,
        val packing_id: Int,
        val packing_price: String?,
        val packing_type: String
    )

    data class Size(
        val display_order: String,
        val size_id: Int,
        val size_price: String?,
        val size_type: String
    )
}