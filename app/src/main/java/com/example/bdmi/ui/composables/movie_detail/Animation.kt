package com.example.bdmi.ui.composables.movie_detail

/*
expand on click animation:
var isTransformed by rememberSaveable { mutableStateOf(false) }
val width by animateDpAsState(
    targetValue = if (isTransformed) 360.dp else 130.dp,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = 1000f
    )
)

val height by animateDpAsState(
    targetValue = if (isTransformed) 620.dp else 220.dp,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = 1000f
    )
)

Icon(
    imageVector = Icons.Default.Favorite,
    contentDescription = null,
    modifier = Modifier
        .size(width = width, height = height)
        .clickable { isTransformed = !isTransformed }
)
*/