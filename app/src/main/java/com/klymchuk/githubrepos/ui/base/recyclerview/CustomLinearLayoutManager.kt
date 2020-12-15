package com.klymchuk.githubrepos.ui.base.recyclerview

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class CustomLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }

}