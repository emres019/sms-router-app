package com.github.emresarincioglu.smsrouter.feature.home.model

import android.net.Uri

/**
 * @param address Sms originating address
 */
data class Sender(
    val id: Int = 0,
    val name: String = "",
    val image: Uri? = null,
    val address: String = "",
    val isSmsGatewayEnabled: Boolean = false,
    val isInAppGatewayEnabled: Boolean = false,
    val isEmailGatewayEnabled: Boolean = false
)