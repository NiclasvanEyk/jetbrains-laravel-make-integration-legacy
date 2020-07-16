package com.niclas_van_eyk.laravel_make_integration.actions.make

import com.niclas_van_eyk.laravel_make_integration.actions.NamespacedArtisanMakeAction

class MakeCommandAction: NamespacedArtisanMakeAction() {
    override fun defaultFolder(): String {
        return "/app/Console/Commands/"
    }

    override fun subCommand(): String {
        return "command"
    }
}