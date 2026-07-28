package com.example.thecodecup.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.withContext
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy
import java.util.Locale
import kotlin.coroutines.resume

@Composable
fun MapAddressPickerDialog(
    initialAddress: String,
    onDismiss: () -> Unit,
    onAddressSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var selectedAddress by remember(initialAddress) { mutableStateOf(initialAddress) }
    var locating by remember { mutableStateOf(false) }
    var locationMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var locationJob by remember { mutableStateOf<Job?>(null) }

    fun selectCurrentLocation() {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isDeviceLocationEnabled()) {
            locating = false
            locationMessage = "Phone location is turned off. Turn on Location Services, then try again."
            return
        }

        locationJob?.cancel()
        locating = true
        locationMessage = null
        locationJob = scope.launch {
            val freshLocation = withTimeoutOrNull(12_000L) {
                manager.awaitLocation()
            }
            val location = freshLocation ?: manager.newestLastKnownLocation()
            if (location != null) {
                selectedPoint = GeoPoint(location.latitude, location.longitude)
                if (freshLocation == null) {
                    locationMessage = "A live fix timed out, so the most recent saved location is shown."
                }
            } else {
                locationMessage =
                    "Could not get your location. Move to an open area and try again."
            }
            locating = false
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.any { it }) {
            selectCurrentLocation()
        } else {
            locating = false
            locationMessage = "Location permission is required to use your current location."
        }
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
                locationMessage?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    if (!managerLocationEnabled(context)) {
                        TextButton(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            }
                        ) {
                            Text("Turn on phone location")
                        }
                    }
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

    DisposableEffect(Unit) {
        onDispose { locationJob?.cancel() }
    }
}

private fun managerLocationEnabled(context: Context): Boolean =
    (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
        .isDeviceLocationEnabled()

private fun LocationManager.isDeviceLocationEnabled(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        isLocationEnabled
    } else {
        runCatching {
            isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }.getOrDefault(false)
    }

@Suppress("MissingPermission")
private suspend fun LocationManager.awaitLocation(): Location? =
    suspendCancellableCoroutine { continuation ->
        val enabledProviders = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER
        ).filter { provider -> runCatching { isProviderEnabled(provider) }.getOrDefault(false) }

        if (enabledProviders.isEmpty()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        lateinit var listener: LocationListener
        listener = LocationListener { location ->
            if (continuation.isActive) {
                removeUpdates(listener)
                continuation.resume(location)
            }
        }
        continuation.invokeOnCancellation { removeUpdates(listener) }
        runCatching {
            enabledProviders.forEach { provider ->
                requestLocationUpdates(provider, 0L, 0f, listener)
            }
        }.onFailure {
            removeUpdates(listener)
            if (continuation.isActive) continuation.resume(null)
        }
    }

@Suppress("MissingPermission")
private fun LocationManager.newestLastKnownLocation(): Location? =
    listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        .mapNotNull { provider -> runCatching { getLastKnownLocation(provider) }.getOrNull() }
        .maxByOrNull { it.time }

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
