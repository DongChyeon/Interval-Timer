package com.dongchyeon.exerciseintervaltimer

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dongchyeon.exerciseintervaltimer.ui.timer.TimerDataState
import com.dongchyeon.exerciseintervaltimer.ui.timer.TimerRepository.Companion.DEFAULT_STATE
import com.dongchyeon.exerciseintervaltimer.ui.timer.TimerViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: BottomSheetScaffoldState,
    timerDataState: TimerDataState,
    viewModel: TimerViewModel,
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
                text = "타이머 설정",
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

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "세트 수",
                modifier = modifier
            )

            Spacer(
                modifier = modifier.width(8.dp)
            )

            NumberPicker(
                modifier, allSets, IntRange(1, 20)
            )
        }

        Spacer(
            modifier = modifier.height(8.dp)
        )

        TimePicker(title = "준비 시간", min = prepareMin, sec = prepareSec)

        Spacer(
            modifier = modifier.height(8.dp)
        )

        TimePicker(title = "운동 시간", min = exerciseMin, sec = exerciseSec)

        Spacer(
            modifier = modifier.height(8.dp)
        )

        TimePicker(title = "휴식 시간", min = restMin, sec = restSec)

        Spacer(
            modifier = modifier.height(8.dp)
        )

        Button(
            onClick = {
                viewModel.setTimer(
                    allSets = allSets.value,
                    prepareTime = prepareMin.value * 60 + prepareSec.value,
                    exerciseTime = exerciseMin.value * 60 + exerciseSec.value,
                    restTime = restMin.value * 60 + restSec.value
                )
            },
        ) {
            Text("타이머 재설정")
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

        NumberPicker(
            modifier, min, IntRange(0, 59)
        )

        Spacer(
            modifier = modifier.width(8.dp)
        )

        Text(
            text = "분",
            modifier = modifier
        )

        Spacer(
            modifier = modifier.width(8.dp)
        )

        NumberPicker(
            modifier, sec, IntRange(0, 59)
        )

        Spacer(
            modifier = modifier.width(8.dp)
        )

        Text(
            text = "초",
            modifier = modifier
        )
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
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    BottomSheet(
        bottomSheetState = bottomSheetScaffoldState,
        timerDataState = DEFAULT_STATE,
        viewModel = viewModel()
    )
}