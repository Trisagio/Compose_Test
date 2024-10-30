package com.example.composetest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Панель для выбора цвета и толщины линии.
 *
 * @param onClick Функция обратного вызова, которая вызывается при выборе цвета.
 * @param onLineWidthChange Функция обратного вызова, которая вызывается при изменении толщины линии.
 */
@Composable
fun BottomPanel(
    onClick: (Color) -> Unit, // Обработчик для выбора цвета
    onLineWidthChange: (Float) -> Unit // Обработчик для изменения толщины линии
) {
    // Колонка для вертикального размещения элементов
    Column(
        modifier = Modifier
            .fillMaxWidth() // Занимает всю ширину экрана
            .fillMaxHeight() // Занимает всю высоту экрана
            .background(Color.LightGray) // Задает светло-серый фон
            .verticalScroll(rememberScrollState()), // Позволяет вертикальную прокрутку
        horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание элементов по центру по горизонтали
        verticalArrangement = Arrangement.Bottom // Размещение элементов внизу колонки
    ) {
        // Отображение списка цветов с обработкой кликов
        ColorList { color ->
            onClick(color) // Вызываем функцию обратного вызова с выбранным цветом
        }

        // Отображение пользовательского слайдера для выбора толщины линии
        CustomSlider { lineWidth ->
            onLineWidthChange(lineWidth) // Вызываем функцию обратного вызова с выбранной толщиной линии
        }
    }
}

/**
 * Компонент, отображающий список цветов в горизонтальном ряду.
 *
 * @param onClick Функция обратного вызова, которая вызывается при клике на цвет,
 *                передавая выбранный цвет.
 */
@Composable
fun ColorList(onClick: (Color) -> Unit) {
    // Список доступных цветов для выбора
    val colors = listOf(
        Color.Blue,     // Синий
        Color.White,    // Белый
        Color.Black,    // Черный
        Color.Red,      // Красный
        Color.Magenta,  // Пурпурный
        Color.Yellow,   // Желтый
        Color.Green,    // Зеленый
        Color.Cyan,     // Голубой
        Color.Gray,     // Серый
        Color.DarkGray   // Темно-серый
    )

    // LazyRow для отображения списка цветов в горизонтальном ряду
    LazyRow(
        modifier = Modifier.padding(10.dp) // Добавляем отступы вокруг LazyRow
    ) {
        // Перебираем каждый цвет в списке и создаем элемент интерфейса для него
        items(colors) { color ->
            // Box - контейнер для отображения цветовой точки
            Box(
                modifier = Modifier
                    .padding(end = 10.dp) // Добавляем отступ справа от цветовой точки
                    .clickable { // Обрабатываем клики на цветовой точке
                        onClick(color) // Вызываем функцию обратного вызова с выбранным цветом
                    }
                    .size(40.dp) // Устанавливаем размер цветовой точки
                    .background(color, CircleShape) // Задаем цвет фона и форму (круг)
            )
        }
    }
}

/**
 * Компонент пользовательского слайдера для настройки толщины кисти.
 *
 * @param onChange Функция обратного вызова, которая вызывается при изменении
 * значения слайдера, передавая новое значение толщины кисти в процентах.
 */
@Composable
fun CustomSlider(onChange: (Float) -> Unit) {
    // Переменная состояния для хранения текущей позиции слайдера (толщины кисти)
    var position by remember {
        mutableStateOf(0.05f) // Инициализация слайдера на 5%
    }

    // Структура Column для вертикального расположения элементов
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Отображение текущей толщины кисти в процентах
        Text("Толщина кисти: ${(position * 100).toInt()}", color = Color.White)

        // Компонент Slider для выбора значения толщины кисти
        Slider(
            value = position, // Устанавливаем текущее значение слайдера
            onValueChange = {
                // Обновление позиции слайдера, если новое значение больше 0
                val tempPos = if (it > 0) it else 0.01f // Устанавливаем минимальное значение 0.01
                position = tempPos // Обновляем состояние позиции слайдера
                onChange(tempPos * 100) // Вызываем функцию обратного вызова с новым значением в процентах
            }
        )
    }
}