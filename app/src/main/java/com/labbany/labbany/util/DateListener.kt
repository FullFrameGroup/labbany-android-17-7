package com.labbany.labbany.util

import java.util.*

interface DateListener {

    fun onFullTimeListener(calendar: Calendar)

    fun onCancel()
}