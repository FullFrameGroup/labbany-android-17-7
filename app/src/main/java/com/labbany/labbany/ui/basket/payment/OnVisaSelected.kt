package com.labbany.labbany.ui.basket.payment

import com.labbany.labbany.pojo.model.VisaModel
import java.io.Serializable

interface OnVisaSelected :Serializable {

    fun onVisaSelected(visa: VisaModel)
}