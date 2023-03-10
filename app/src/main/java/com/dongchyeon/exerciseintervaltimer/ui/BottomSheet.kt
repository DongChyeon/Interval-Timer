package com.dongchyeon.exerciseintervaltimer.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dongchyeon.exerciseintervaltimer.ui.timer.TimerDataState
import com.dongchyeon.exerciseintervaltimer.ui.timer.TimerRepository.Companion.DEFAULT_STATE
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: BottomSheetScaffoldState,
    timerDataState: TimerDataState,
    setTimer: (allSets: Int, prepareTime: Int, exerciseTime: Int, restTime: Int) -> Unit
) {
    val allSets = remember { mutableStateOf(timerDataState.allSets) }

    val prepareMin = remember { mutableStateOf(timerDataState.prepareSec / 60) }
    val prepareSec = remember { mutableStateOf(timerDataState.prepareSec % 60) }

    val exerciseMin = remember { mutableStateOf(timerDataState.exerciseSec / 60) }
    val exerciseSec = remember { mutableStateOf(timerDataState.exerciseSec % 60) }

    val restMin = remember { mutableStateOf(timerDataState.restSec / 60) }
    val restSec = remember { mutableStateOf(timerDataState.restSec % 60) }

    Column(
        modifier = modifier
            .heightIn(min = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = modifier
                .background(Color.Black)
                .height(50.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (bottomSheetState.bottomSheetState.isCollapsed) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                tint = Color.White,
                contentDescription = null,
                modifier = modifier
            )
            Text(
                text = "????????? ??????",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                fontSize = 18.sp,
                modifier = modifier
                    .padding(4.dp)
            )
        }

        Spacer(
            modifier = modifier.height(8.dp)
        )

        Text(
            text = "?????? ???",
            modifier = modifier
        )

        Spacer(
            modifier = modifier.height(4.dp)
        )

        NumberPicker(
            modifier, allSets, IntRange(1, 20), textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        )

        Spacer(
            modifier = modifier.height(8.dp)
        )

        TimePicker(
            title = "?????? ??????", min = prepareMin, sec = prepareSec, textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        )

        Spacer(
            modifier = modifier.height(8.dp)
        )

        TimePicker(
            title = "?????? ??????", min = exerciseMin, sec = exerciseSec, textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        )

        Spacer(
            modifier = modifier.height(8.dp)
        )

        TimePicker(
            title = "?????? ??????", min = restMin, sec = restSec, textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        )

        Spacer(
            modifier = modifier.height(8.dp)
        )

        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black,
                contentColor = Color.White
            ),
            onClick = {
                setTimer(
                    allSets.value,
                    prepareMin.value * 60 + prepareSec.value,
                    exerciseMin.value * 60 + exerciseSec.value,
                    restMin.value * 60 + restSec.value
                )
            },
        ) {
            Text("????????? ?????????")
        }

        Spacer(
            modifier = modifier.height(8.dp)
        )
    }
}

@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    title: String,
    min: MutableState<Int>,
    sec: MutableState<Int>,
    textStyle: TextStyle = LocalTextStyle.current
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = modifier
        )

        Spacer(
            modifier = modifier.width(8.dp)
        )

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NumberPicker(
                modifier = modifier,
                state = min,
                range = IntRange(0, 59),
                textStyle = textStyle
            )

            Spacer(
                modifier = modifier.width(8.dp)
            )

            Text(
                text = "???",
                style = textStyle,
                modifier = modifier
            )

            Spacer(
                modifier = modifier.width(8.dp)
            )

            NumberPicker(
                modifier = modifier,
                state = sec,
                range = IntRange(0, 59),
                textStyle = textStyle
            )

            Spacer(
                modifier = modifier.width(8.dp)
            )

            Text(
                text = "???",
                style = textStyle,
                modifier = modifier
            )
        }
    }
}

@Composable
fun NumberPicker(
    modifier: Modifier = Modifier,
    state: MutableState<Int>,
    range: IntRange,
    textStyle: TextStyle = LocalTextStyle.current,
    onStateChanged: (Int) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val numbersColumnHeight = 36.dp
    val halvedNumbersColumnHeight = numbersColumnHeight / 2
    val halvedNumbersColumnHeightPx =
        with(LocalDensity.current) { halvedNumbersColumnHeight.toPx() }

    fun animatedStateValue(offset: Float): Int =
        state.value - (offset / halvedNumbersColumnHeightPx).toInt()

    val animatedOffset = remember { Animatable(0f) }.apply {
        val offsetRange = remember(state.value, range) {
            val value = state.value
            val first = -(range.last - value) * halvedNumbersColumnHeightPx
            val last = -(range.first - value) * halvedNumbersColumnHeightPx
            first..last
        }
        updateBounds(offsetRange.start, offsetRange.endInclusive)
    }
    val coercedAnimatedOffset = animatedOffset.value % halvedNumbersColumnHeightPx
    val animatedStateValue = animatedStateValue(animatedOffset.value)

    Column(
        modifier = modifier
            .wrapContentSize()
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { deltaY ->
                    coroutineScope.launch {
                        animatedOffset.snapTo(animatedOffset.value + deltaY)
                    }
                },
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        val endValue = animatedOffset.fling(
                            initialVelocity = velocity,
                            animationSpec = exponentialDecay(frictionMultiplier = 20f),
                            adjustTarget = { target ->
                                val coercedTarget = target % halvedNumbersColumnHeightPx
                                val coercedAnchors = listOf(
                                    -halvedNumbersColumnHeightPx,
                                    0f,
                                    halvedNumbersColumnHeightPx
                                )
                                val coercedPoint =
                                    coercedAnchors.minByOrNull { abs(it - coercedTarget) }!!
                                val base =
                                    halvedNumbersColumnHeightPx * (target / halvedNumbersColumnHeightPx).toInt()
                                coercedPoint + base
                            }
                        ).endState.value

                        state.value = animatedStateValue(endValue)
                        onStateChanged(state.value)
                        animatedOffset.snapTo(0f)
                    }
                }
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .offset { IntOffset(x = 0, y = coercedAnimatedOffset.roundToInt()) }
        ) {
            ProvideTextStyle(textStyle) {
                Text(
                    text = (animatedStateValue - 1).toString(),
                    modifier = modifier
                        .align(Alignment.Center)
                        .offset(y = -halvedNumbersColumnHeight)
                        .alpha(coercedAnimatedOffset / halvedNumbersColumnHeightPx)
                )
                Text(
                    text = animatedStateValue.toString(),
                    modifier = modifier
                        .align(Alignment.Center)
                        .alpha(1 - abs(coercedAnimatedOffset) / halvedNumbersColumnHeightPx)
                )
                Text(
                    text = (animatedStateValue + 1).toString(),
                    modifier = modifier
                        .align(Alignment.Center)
                        .offset(y = halvedNumbersColumnHeight)
                        .alpha(-coercedAnimatedOffset / halvedNumbersColumnHeightPx)
                )
            }
        }
    }
}

private suspend fun Animatable<Float, AnimationVector1D>.fling(
    initialVelocity: Float,
    animationSpec: DecayAnimationSpec<Float>,
    adjustTarget: ((Float) -> Float)?,
    block: (Animatable<Float, AnimationVector1D>.() -> Unit)? = null,
): AnimationResult<Float, AnimationVector1D> {
    val targetValue = animationSpec.calculateTargetValue(value, initialVelocity)
    val adjustedTarget = adjustTarget?.invoke(targetValue)

    return if (adjustedTarget != null) {
        animateTo(
            targetValue = adjustedTarget,
            initialVelocity = initialVelocity,
            block = block
        )
    } else {
        animateDecay(
            initialVelocity = initialVelocity,
            animationSpec = animationSpec,
            block = block,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun PreviewTimePicker() {
    BottomSheet(
        bottomSheetState = BottomSheetScaffoldState(
            drawerState = DrawerState(DrawerValue.Closed),
            bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed),
            snackbarHostState = SnackbarHostState()
        ),
        timerDataState = DEFAULT_STATE,
        setTimer = { _, _, _, _ -> })
}