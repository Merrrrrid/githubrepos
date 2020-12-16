package com.klymchuk.githubrepos.utils

import kotlin.math.abs

fun Any.logTag(): String {
    return this::class.java.simpleName
}

fun String.tokenFormatter() : String{
    return "bearer $this"
}

fun Int.thousandFormatter(): String{
   return when {
       abs(this / 1000) > 1 -> {
           "${this / 1000},${(this % 1000).toString().substring(0, 1)}k"
       }
       else -> {
           this.toString()
       }
   }
}