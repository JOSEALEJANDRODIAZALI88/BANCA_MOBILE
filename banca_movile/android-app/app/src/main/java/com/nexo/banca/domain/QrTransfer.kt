package com.nexo.banca.domain

import android.net.Uri
import androidx.core.net.toUri

data class QrTransfer(val name: String, val account: String, val amount: Long?)

object QrTransferCodec {
    fun encode(name: String, account: String, amount: Long? = null): String = Uri.Builder()
        .scheme("nexo")
        .authority("transfer")
        .appendQueryParameter("name", name)
        .appendQueryParameter("account", account)
        .apply { amount?.let { appendQueryParameter("amount", it.toString()) } }
        .build()
        .toString()

    fun decode(raw: String): QrTransfer? = runCatching {
        val uri = raw.toUri()
        if (uri.scheme != "nexo" || uri.host !in setOf("transfer", "receive")) return null
        val name = uri.getQueryParameter("name")?.trim().orEmpty()
        val account = uri.getQueryParameter("account")?.trim().orEmpty()
        val amount = uri.getQueryParameter("amount")?.toLongOrNull()
        if (name.isBlank() || !BankingRules.validAccount(account)) return null
        if (amount != null && amount !in 1..BankingRules.MAX_TRANSFER) return null
        QrTransfer(name = name.take(60), account = account, amount = amount)
    }.getOrNull()
}
