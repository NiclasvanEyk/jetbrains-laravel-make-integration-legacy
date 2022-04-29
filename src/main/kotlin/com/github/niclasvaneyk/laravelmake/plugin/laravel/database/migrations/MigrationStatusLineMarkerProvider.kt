package com.github.niclasvaneyk.laravelmake.plugin.laravel.database.migrations

import com.github.niclasvaneyk.laravelmake.plugin.jetbrains.LaravelIcons
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.PhpClass
import java.io.File

class MigrationStatusLineMarkerProvider: LineMarkerProviderDescriptor() {
    override fun getName() = "Laravel migration status"
    override fun getIcon() = LaravelIcons.LaravelLogo

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        val file = element.containingFile
        if (file.parent?.name != "migrations") return null
        if (element !is PhpClass) return null

        val migrations = element.project.service<MigrationService>()
        val inferredName = File(file.name).nameWithoutExtension
        val migration = migrations.current.find { it.name == inferredName} ?: return null

        return MigrationStatusLineMarker(element, migration)
    }
}

class MigrationStatusLineMarker(
    element: PsiElement,
    private val migration: Migration,
): LineMarkerInfo<PsiElement>(
    element,
    element.textRange,
    if (migration.ran) AllIcons.Actions.Commit
    else AllIcons.Hierarchy.MethodNotDefined,
    { text(migration) },
    null,
    GutterIconRenderer.Alignment.LEFT,
    { text(migration) },
)  {
    companion object {
        fun text(migration: Migration): String {
            return if (migration.ran) "Migration ran"
            else "Migration still needs to be run"
        }
    }
}