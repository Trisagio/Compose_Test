package com.example.composetest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Подкомпонент для отображения панели кнопок с возможностью
 * отмены и очистки канваса.
 *
 * @param onUndoCanvas Функция обратного вызова, вызываемая
 * при нажатии кнопки "Назад".
 * @param onClearCanvas Функция обратного вызова, вызываемая
 * при нажатии кнопки "Очистка".
 */
@Composable
fun ButtonsSubPanel(
    onUndoCanvas: () -> Unit, // Функция для отмены последнего действия
    onClearCanvas: () -> Unit // Функция для очистки канваса
) {
    // Основной контейнер, который размещает кнопки в вертикальном
    // столбце с возможностью прокрутки
    Column(
        modifier = Modifier
            .fillMaxWidth() // Заполняет всю доступную ширину экрана
            .fillMaxHeight() // Заполняет всю доступную высоту экрана
            .background(Color.LightGray) // Устанавливает светло-серый фон для панели
            .verticalScroll(rememberScrollState()), // Позволяет прокручивать содержимое по вертикали
        verticalArrangement = Arrangement.Top, // Размещает содержимое в верхней части колонки
        horizontalAlignment = Alignment.CenterHorizontally // Выравнивает содержимое по центру по горизонтали
    ) {
        // Вызов подкомпонента, который содержит кнопки "Назад" и "Очистка"
        SubButtons(onUndoCanvas = onUndoCanvas, onClearCanvas = onClearCanvas)
    }
}

/**
 * Подкомпонент для отображения кнопок "Очистка" и "Назад".
 *
 * @param onClearCanvas Функция обратного вызова, вызываемая при нажатии кнопки "Очистка".
 * @param onUndoCanvas Функция обратного вызова, вызываемая при нажатии кнопки "Назад".
 */
@Composable
fun SubButtons(onClearCanvas: () -> Unit, onUndoCanvas: () -> Unit) {
    // Строка для размещения кнопок с равномерным расстоянием между ними
    Row(
        horizontalArrangement = Arrangement.SpaceBetween, // Располагаем кнопки с равномерным пространством
        modifier = Modifier.fillMaxWidth().padding(8.dp) // Занимаем всю ширину и добавляем отступы
    ) {
        // Контейнер для кнопки "Очистка"
        Box(
            modifier = Modifier
                .fillMaxWidth() // Занимает всю ширину
                .height(100.dp) // Устанавливает высоту контейнера
                .weight(1f) // Задает вес для равномерного распределения ширины
                .padding(horizontal = 5.dp) // Добавляет горизонтальные отступы
                .background(Color.Blue) // Задает синий фон для кнопки
        ) {
            // Кнопка "Очистка"
            Button(
                onClick = { onClearCanvas() }, // Обработчик нажатия на кнопку
                modifier = Modifier
                    .padding(4.dp) // Добавляет отступы вокруг кнопки
                    .fillMaxWidth() // Заполняет всю ширину контейнера
                    .fillMaxHeight() // Заполняет всю высоту контейнера
            ) {
                Text(text = "Очистка") // Текст на кнопке
            }
        }

        // Контейнер для кнопки "Назад"
        Box(
            modifier = Modifier
                .fillMaxWidth() // Занимает всю ширину
                .height(100.dp) // Устанавливает высоту контейнера
                .weight(1f) // Задает вес для равномерного распределения ширины
                .padding(horizontal = 5.dp) // Добавляет горизонтальные отступы
                .background(Color.Blue) // Задает синий фон для кнопки
        ) {
            // Кнопка "Назад"
            Button(
                onClick = { onUndoCanvas() }, // Обработчик нажатия на кнопку
                modifier = Modifier
                    .padding(4.dp) // Добавляет отступы вокруг кнопки
                    .fillMaxWidth() // Заполняет всю ширину контейнера
                    .fillMaxHeight() // Заполняет всю высоту контейнера
            ) {
                Text(text = "Назад") // Текст на кнопке
            }
        }
    }
}