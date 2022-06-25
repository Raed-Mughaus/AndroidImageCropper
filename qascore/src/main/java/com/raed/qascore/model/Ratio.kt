package com.raed.qascore.model

sealed class Ratio {
    object Custom: Ratio()
    class Fixed(
        val antecedent: Int,
        val consequent: Int,
    ): Ratio()
}