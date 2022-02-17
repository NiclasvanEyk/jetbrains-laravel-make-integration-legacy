package com.niclas_van_eyk.laravel_make_integration.plugin.laravel.routes.toolWindow.list

import com.intellij.openapi.project.Project
import com.intellij.ui.ListSpeedSearch
import com.niclas_van_eyk.laravel_make_integration.common.jetbrains.ui.IntrospectionList
import com.niclas_van_eyk.laravel_make_integration.common.jetbrains.ui.TriggersRender
import com.niclas_van_eyk.laravel_make_integration.common.laravel.introspection.IntrospectionSubject
import com.niclas_van_eyk.laravel_make_integration.plugin.laravel.routes.introspection.IntrospectedRoute
import com.niclas_van_eyk.laravel_make_integration.plugin.laravel.routes.introspection.RouteOrigin
import javax.swing.ListSelectionModel

class RouteList(
    routeUpdates: IntrospectionSubject<List<IntrospectedRoute>>,
    private val project: Project,
    private val onRouteSelected: (IntrospectedRoute?) -> Unit,
) : IntrospectionList<IntrospectedRoute>(routeUpdates) {
    var showMiddlewareParameters by TriggersRender(true, this)
    var showApplicationRoutes by TriggersRender(true, this)
    var showVendorRoutes by TriggersRender(true, this)
    var showClosureRoutes by TriggersRender(true, this)
    var fullyQualifyMiddlewareNames by TriggersRender(false, this)

    val shownRouteOrigins: Set<RouteOrigin>
        get() = mutableSetOf<RouteOrigin>().apply {
            if (showVendorRoutes) add(RouteOrigin.VENDOR)
            if (showApplicationRoutes) add(RouteOrigin.PROJECT)
            if (showClosureRoutes) add(RouteOrigin.UNKNOWN)
        }

    init {
        addMouseListener(RouteListMouseListener(this))
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        selectionModel.addListSelectionListener {
            onRouteSelected(selectedValue)
        }

        cellRenderer = RouteListCellRenderer(showMiddlewareParameters, fullyQualifyMiddlewareNames, project)

        ListSpeedSearch(this) { it.path }

        subscribeToModelUpdates()
    }

    override fun listKey(element: IntrospectedRoute) = listOf(element.path, element.httpMethod).joinToString("...")

    override fun deriveVisibleModel(newModel: List<IntrospectedRoute>): List<IntrospectedRoute> {
        return newModel .filter { shownRouteOrigins.contains(it.origin) }
    }

    override fun triggerRender() {
        super.triggerRender()

        // Needs to be updated, since showMiddleWareParameters could have
        // changed
        cellRenderer = RouteListCellRenderer(showMiddlewareParameters, fullyQualifyMiddlewareNames, project)
    }
}