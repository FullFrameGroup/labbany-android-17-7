package com.labbany.labbany.ui.basket.payment

import com.labbany.labbany.pojo.model.VisaTypeModel

interface OnCardBrandSelected {

    fun onCardBrandSelected(cardBrand: VisaTypeModel):Boolean
}