package com.labbany.labbany.di

import com.labbany.labbany.util.ShardHelper
import org.koin.dsl.module

object GeneralModules {


    val modules = module {

        single { ShardHelper(get()) }

    }



}