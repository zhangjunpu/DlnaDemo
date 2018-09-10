package com.bftv.dlnademo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 *
 * @author Junpu
 * @time 2018/6/25 14:34
 */


fun Context.inflate(resource: Int, root: ViewGroup?, attachToRoot: Boolean): View =
        LayoutInflater.from(this).inflate(resource, root, attachToRoot)

fun CharSequence?.isNotNullOrBlank(): Boolean = !isNullOrBlank()
fun CharSequence?.isNotNullOrEmpty(): Boolean = !isNullOrEmpty()

val CharSequence?.notNull: CharSequence
    get() = this ?: ""
