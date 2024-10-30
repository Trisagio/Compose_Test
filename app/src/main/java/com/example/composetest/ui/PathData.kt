package com.example.composetest.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

/**
 * Данные, связанные с рисуемым путем на холсте.
 *
 * @property path Объект Path, представляющий рисуемую линию или фигуру.
 * @property color Цвет, используемый для рисования пути.
 * @property lineWidth Ширина линии, используемая для рисования пути.
 *
 * @constructor Создает экземпляр PathData с заданными значениями для пути,
 * цветом и шириной линии.
 *
 * По умолчанию, путь инициализируется как пустой, цвет - черный, а ширина линии - 5 пикселей.
 */
data class PathData(
    val path: Path = Path(), // Путь, представляющий рисуемую фигуру
    val color: Color = Color.Black, // Цвет линии по умолчанию - черный
    val lineWidth: Float = 5f // Ширина линии по умолчанию - 5 пикселей
)