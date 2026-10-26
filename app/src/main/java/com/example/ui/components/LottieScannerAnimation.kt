package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.R
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ElectricBlue

/**
 * Subtle Lottie Animation component for the document camera scanning experience.
 * Plays the continuous AI laser scan, focus reticle, and corner highlights.
 */
@Composable
fun LottieScannerBeamOverlay(
    modifier: Modifier = Modifier,
    alpha: Float = 0.85f
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.lottie_camera_scan)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 1.0f
    )

    if (composition != null) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = modifier.alpha(alpha)
        )
    }
}

/**
 * Subtle Lottie focus pulse at the center of the camera viewfinder
 */
@Composable
fun LottieCameraFocusRadar(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.lottie_radar_pulse)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 0.9f
    )

    if (composition != null) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = modifier.size(size)
        )
    }
}

/**
 * Animated "Camera Active" AI Status Badge with pulsing beacon and shimmer
 */
@Composable
fun CameraActiveStatusBadge(
    modifier: Modifier = Modifier,
    isDetecting: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_beacon")
    val beaconAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beacon_alpha"
    )
    val beaconScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beacon_scale"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xCC0B1528))
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        ChampagneGold.copy(alpha = 0.6f),
                        ElectricBlue.copy(alpha = 0.4f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Pulsing live beacon indicator
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .scale(beaconScale)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981).copy(alpha = beaconAlpha))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "AI VISION ACTIVE",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = ChampagneGold,
                modifier = Modifier.size(12.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "• Auto-Aligning",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFCBD5E1)
            )
        }
    }
}

/**
 * Camera Viewfinder Transition Wrapper providing smooth camera entry & aperture flash
 */
@Composable
fun CameraActiveTransitionContainer(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var hasAppeared by remember { mutableStateOf(false) }

    LaunchedEffect(isActive) {
        if (isActive) {
            hasAppeared = true
        }
    }

    AnimatedVisibility(
        visible = hasAppeared,
        enter = fadeIn(animationSpec = tween(durationMillis = 400, easing = LinearEasing)) +
                scaleIn(initialScale = 0.96f, animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)),
        exit = fadeOut(animationSpec = tween(durationMillis = 200)),
        modifier = modifier
    ) {
        content()
    }
}
