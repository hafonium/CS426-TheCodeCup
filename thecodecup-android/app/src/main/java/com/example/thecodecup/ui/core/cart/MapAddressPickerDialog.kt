package com.example.thecodecup.ui.core.cart

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy
import java.util.Locale

@Composable
internal fun MapAddressPickerDialog(
    initialAddress: String,
    onDismiss: () -> Unit,
    onAddressSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var selectedAddress by remember(initialAddress) { mutableStateOf(initialAddress) }
    var locating by remember { mutableStateOf(false) }

    fun selectCurrentLocation() {
        locating = true
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
            .mapNotNull { provider ->
                runCatching {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED
                    ) manager.getLastKnownLocation(provider) else null
                }.getOrNull()
            }
            .maxByOrNull { it.time }
        if (location != null) selectedPoint = GeoPoint(location.latitude, location.longitude)
        locating = false
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.any { it }) selectCurrentLocation() else locating = false
    }

    LaunchedEffect(selectedPoint) {
        selectedPoint?.let { point ->
            selectedAddress = withContext(Dispatchers.IO) {
                runCatching {
                    @Suppress("DEPRECATION")
                    Geocoder(context, Locale.getDefault())
                        .getFromLocation(point.latitude, point.longitude, 1)
                        ?.firstOrNull()
                        ?.getAddressLine(0)
                }.getOrNull()
                    ?: "${"%.6f".format(point.latitude)}, ${"%.6f".format(point.longitude)}"
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select delivery location") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
            ) {
                Text("Tap anywhere on the map to choose a location.", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(10.dp))
                AddressMap(
                    selectedPoint = selectedPoint,
                    onPointSelected = { selectedPoint = it }
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = selectedAddress,
                    onValueChange = { selectedAddress = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Selected address") },
                    minLines = 2
                )
                TextButton(
                    onClick = {
                        val granted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        if (granted) selectCurrentLocation()
                        else {
                            locating = true
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    enabled = !locating
                ) {
                    Icon(Icons.Outlined.MyLocation, null)
                    Spacer(Modifier.width(6.dp))
                    Text(if (locating) "Finding location..." else "Use my location")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAddressSelected(selectedAddress.trim()) },
                enabled = selectedAddress.isNotBlank()
            ) { Text("Use this address") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun AddressMap(
    selectedPoint: GeoPoint?,
    onPointSelected: (GeoPoint) -> Unit
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                MapView(context).apply {
                    setTileSource(OpenStreetMapTileSource)
                    setMultiTouchControls(true)
                    clipChildren = true
                    clipToPadding = true
                    controller.setZoom(13.0)
                    controller.setCenter(GeoPoint(13.7563, 100.5018))
                    overlays.add(MapEventsOverlay(object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(point: GeoPoint): Boolean {
                            onPointSelected(point)
                            return true
                        }

                        override fun longPressHelper(point: GeoPoint) = false
                    }))
                    mapView = this
                }
            },
            update = { map ->
                map.overlays.removeAll { it is Marker }
                selectedPoint?.let { point ->
                    map.overlays.add(Marker(map).apply {
                        position = point
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Delivery location"
                    })
                    map.controller.animateTo(point)
                }
                map.invalidate()
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            mapView?.onPause()
            mapView?.onDetach()
        }
    }
}

private val OpenStreetMapTileSource = object : OnlineTileSourceBase(
    "OpenStreetMap",
    0,
    19,
    256,
    ".png",
    arrayOf("https://tile.openstreetmap.org/"),
    "© OpenStreetMap contributors",
    TileSourcePolicy(
        2,
        TileSourcePolicy.FLAG_NO_BULK or TileSourcePolicy.FLAG_NO_PREVENTIVE
    )
) {
    override fun getTileURLString(tileIndex: Long): String =
        baseUrl +
            org.osmdroid.util.MapTileIndex.getZoom(tileIndex) + "/" +
            org.osmdroid.util.MapTileIndex.getX(tileIndex) + "/" +
            org.osmdroid.util.MapTileIndex.getY(tileIndex) +
            imageFilenameEnding()
}
