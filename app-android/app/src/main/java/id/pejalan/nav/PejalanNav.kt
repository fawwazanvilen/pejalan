package id.pejalan.nav

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import id.pejalan.data.LaporanDb
import id.pejalan.ml.GemmaClient
import id.pejalan.ui.detail.DetailScreen
import id.pejalan.ui.feed.FeedScreen
import id.pejalan.ui.map.MapScreen

private data class NavTab(val route: String, val label: String, val icon: ImageVector)

private val Tabs = listOf(
    NavTab("capture", "Capture", Icons.Filled.PhotoCamera),
    NavTab("feed", "Linimasa", Icons.Filled.Timeline),
    NavTab("map", "Peta", Icons.Filled.Map),
)

@Composable
fun PejalanNav(
    gemma: GemmaClient,
    db: LaporanDb,
    captureRoute: @Composable () -> Unit,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in Tabs.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    Tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                if (currentRoute != tab.route) {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.outline,
                                unselectedTextColor = MaterialTheme.colorScheme.outline,
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "capture",
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            composable("capture") { captureRoute() }
            composable("feed") {
                FeedScreen(
                    db = db,
                    onOpenDetail = { id -> navController.navigate("detail/$id") },
                )
            }
            composable("map") {
                MapScreen(
                    db = db,
                    onOpenDetail = { id -> navController.navigate("detail/$id") },
                )
            }
            composable(
                "detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { entry ->
                val id = entry.arguments?.getString("id") ?: return@composable
                DetailScreen(
                    laporanId = id,
                    db = db,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
