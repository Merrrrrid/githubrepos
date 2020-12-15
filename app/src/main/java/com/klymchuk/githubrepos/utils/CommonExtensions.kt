package com.klymchuk.githubrepos.utils

fun Any.logTag(): String {
    return this::class.java.simpleName
}

fun Boolean.inverted(): Boolean {
    return !this
}

