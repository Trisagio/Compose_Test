package com.example.composetest

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.example.composetest.ui.BottomPanel
import com.example.composetest.ui.ButtonsSubPanel
import com.example.composetest.ui.PathData
import com.example.composetest.ui.theme.ComposeTestTheme
import android.provider.MediaStore
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.asAndroidBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Основная активность приложения, представляющая холст для рисования.
 *
 * Эта активность инициализирует интерфейс пользователя, обрабатывает действия
 * пользователя и управляет состоянием приложения, включая рисование на канвасе
 * и загрузку/сохранение изображений.
 */
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class) // Оптимизация для использования экспериментальных API Material3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Вызов метода суперкласса для инициализации
        setContent { // Устанавливаем контент для активности
            val pathData = remember { mutableStateOf(PathData()) } // Состояние для текущего пути рисования
            val pathList = remember { mutableStateListOf<PathData>() } // Список всех нарисованных путей
            var showBottomPanel by remember { mutableStateOf(true) } // Состояние видимости нижней панели
            // Переменная состояния для изображения
            var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) } // Загруженное изображение (по умолчанию null)
            // Получаем контекст
            val context = LocalContext.current // Получение контекста текущей активности
            // Обработчик для выбора изображения из галереи
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.GetContent()
            ) { uri: Uri? -> // Обработчик результата выбора изображения
                uri?.let {
                    val inputStream = context.contentResolver.openInputStream(uri) // Открываем поток для чтения изображения
                    val bitmap = BitmapFactory.decodeStream(inputStream) // Декодируем поток в Bitmap
                    imageBitmap = bitmap.asImageBitmap() // Преобразуем Bitmap в ImageBitmap для отображения
                }
            }
            var isSaveButtonEnabled by remember { mutableStateOf(true) } // Состояние для кнопки сохранения
            var isLoadButtonEnabled by remember { mutableStateOf(true) } // Состояние для кнопки загрузки

            ComposeTestTheme { // Применяем тему к Compose
                Column { // Структура для вертикального размещения элементов
                    TopAppBar(
                        title = { Text("Холст") }, // Заголовок панели
                        actions = { // Действия в панели
                            // Кнопка для переключения нижней панели
                            IconButton(onClick = { showBottomPanel = !showBottomPanel }) {
                                Icon(imageVector = if (showBottomPanel)
                                    Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                                    contentDescription = "Toggle Panel" // Описание кнопки
                                )
                            }
                            // Кнопка для удаления фона изображения
                            IconButton(onClick = {
                                imageBitmap = null // Удаляем изображение
                                Log.d("MyLog", "Фоновое изображение удалено.") // Логируем действие
                            }) {
                                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Remove Image") // Иконка удаления
                            }
                            // Кнопка для загрузки изображения из галереи
                            IconButton(onClick = {
                                if (isLoadButtonEnabled) { // Проверяем, доступна ли кнопка
                                    isLoadButtonEnabled = false // Блокируем кнопку
                                    // Здесь вызываем функцию для загрузки изображения
                                    CoroutineScope(Dispatchers.IO).launch {
                                        // Запуск выбора изображения из галереи
                                        launcher.launch("image/*")

                                        // Разблокируем кнопку через секунду
                                        delay(1000)
                                        // Переход на основной поток для изменения состояния кнопки
                                        withContext(Dispatchers.Main) {
                                            isLoadButtonEnabled = true // Разблокируем кнопку
                                        }
                                    }
                                }
                                Log.d("MyLog", "Попытка загрузить изображение.") // Логируем действие
                            }) {
                                Icon(imageVector = Icons.Filled.Search, contentDescription = "Load Image") // Иконка загрузки
                            }
                            // Кнопка для сохранения изображения
                            IconButton(onClick = {
                                if (isSaveButtonEnabled) { // Проверяем, доступна ли кнопка
                                    isSaveButtonEnabled = false // Блокируем кнопку
                                    // Здесь вызываем функцию для сохранения изображения
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val bitmap = createBitmapFromPaths(pathList, imageBitmap) // Создайте bitmap из ваших путей
                                        saveImageToGallery(context, bitmap) // Теперь сохраняем в галерею
                                        Log.d("MyLog", "Попытка сохранить изображение в галерее.") // Логируем действие

                                        // Разблокируем кнопку через секунду
                                        delay(1000)
                                        // Переход на основной поток для изменения состояния кнопки
                                        withContext(Dispatchers.Main) {
                                            isSaveButtonEnabled = true // Разблокируем кнопку
                                        }
                                    }
                                }
                                Log.d("MyLog", "Попытка сохранить изображение в галерее.") // Логируем действие
                            }) {
                                Icon(imageVector = Icons.Filled.Share, contentDescription = "Save Image") // Иконка сохранения
                            }
                        }
                    )

                    // Компонент для рисования на канвасе
                    DrawCanvas(pathData, pathList, imageBitmap)

                    // Условная отрисовка нижней панели управления
                    if (showBottomPanel) {
                        BottomPanel(
                            onClick = { color -> // Обработчик изменения цвета
                                pathData.value = pathData.value.copy(color = color) // Обновляем цвет текущего пути
                            },
                            onLineWidthChange = { lineWidth -> // Обработчик изменения ширины линии
                                pathData.value = pathData.value.copy(lineWidth = lineWidth) // Обновляем ширину линии
                            }
                        )
                    } else {
                        ButtonsSubPanel(
                            onClearCanvas = { // Обработчик очистки канваса
                                pathList.clear() // Очищаем список путей
                                imageBitmap = ImageBitmap(1, 1) // Устанавливаем минимальное изображение
                                Log.d("MyLog", "Размер: ${pathList.size}") // Логируем размер списка
                            },
                            onUndoCanvas = { // Обработчик отмены последнего действия
                                if (pathList.isNotEmpty()) {
                                    pathList.removeAt(pathList.size - 1) // Удаляем последний путь из списка
                                    Log.d("MyLog", "Отменяем последнее действие: ${pathList.size}") // Логируем действие
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Функция, которая отображает канвас для рисования.
 *
 * Эта функция позволяет пользователю рисовать на канвасе, обрабатывая
 * жесты для масштабирования, перемещения и рисования. Она также
 * поддерживает отображение загруженного изображения.
 *
 * @param pathData Состояние, содержащее данные о текущем пути, который рисуется.
 * @param pathList Список всех путей, которые были нарисованы на канвасе.
 * @param imageBitmap Изображение, которое загружается на канвас (может быть null).
 */
@Composable
fun DrawCanvas(
    pathData: MutableState<PathData>, // Состояние для текущих данных пути
    pathList: MutableList<PathData>,  // Список всех нарисованных путей
    imageBitmap: ImageBitmap?         // Загруженное изображение, которое будет отображено на канвасе
) {
    var tempPath = Path() // Временный путь для рисования

    // Переменные для хранения масштаба и смещения
    var scale by remember { mutableStateOf(1f) } // Масштаб канваса
    var offsetX by remember { mutableStateOf(0f) } // Смещение по оси X
    var offsetY by remember { mutableStateOf(0f) } // Смещение по оси Y

    // Компонент Canvas для рисования
    Canvas(
        modifier = Modifier
            .fillMaxWidth() // Заполнение ширины экрана
            .fillMaxHeight(0.8f) // Заполнение 80% высоты экрана
            .pointerInput(Unit) {
                // Обработка жестов для масштабирования и перемещения канваса
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom // Применение масштаба
                    offsetX += pan.x // Обновление смещения по оси X
                    offsetY += pan.y // Обновление смещения по оси Y
                }
            }
            .pointerInput(Unit) {
                // Обработка жестов для рисования на канвасе
                detectDragGestures(
                    // Инициализация временного пути
                    onDragStart = { tempPath = Path() },
                    // Добавление пути в список после завершения рисования
                    onDragEnd = {
                        addPath(pathData, pathList, tempPath)
                    }
                ) { change, dragAmount -> // Обработка движения пальца по экрану
                    // Переводим координаты с учетом текущего масштаба и смещения
                    val x = (change.position.x - offsetX) / scale
                    val y = (change.position.y - offsetY) / scale
                    val x2 = (change.position.x - dragAmount.x - offsetX) / scale
                    val y2 = (change.position.y - dragAmount.y - offsetY) / scale

                    tempPath.moveTo(x2, y2) // Перемещаем временный путь в начальную точку
                    tempPath.lineTo(x, y) // Добавляем линию до текущей точки

                    // Если в списке есть элементы, удаляем последний
                    if (pathList.isNotEmpty()) {
                        pathList.removeLast()
                    }
                    pathList.add(pathData.value.copy(path = tempPath)) // Добавляем новый путь в список
                }
            }
    ) {
        // Применяем масштаб и смещение перед отрисовкой
        with(drawContext.canvas.nativeCanvas) {
            save() // Сохраняем текущее состояние канваса
            scale(scale, scale) // Применяем масштабирование
            translate(offsetX / scale, offsetY / scale) // Применяем смещение

            // Рисуем изображение, если оно загружено
            imageBitmap?.let {
                // Отображение изображения в верхнем левом углу
                drawImage(it, topLeft = Offset(0f, 0f))
            }

            // Рисуем белый фон, если нет нарисованных путей
            if (pathList.isEmpty()) {
                // Минимальный прямоугольник белого цвета
                drawRect(Color.White, size = Size(0.01f, 0.01f))
            } else {
                // Рисуем все пути из списка на канвасе
                pathList.forEach { pathData ->
                    drawPath(
                        pathData.path, // Рисуем путь
                        color = pathData.color, // Цвет пути
                        style = Stroke( // Стиль обводки
                            pathData.lineWidth, // Ширина линии
                            cap = StrokeCap.Round, // Закругленные концы
                            join = StrokeJoin.Round // Закругленные соединения
                        )
                    )
                }
            }
            restore() // Восстанавливаем предыдущее состояние канваса
        }
    }
}

/**
 * Функция для добавления нового пути в список путей.
 *
 * Эта функция принимает текущее состояние пути и добавляет его в
 * переданный список путей. Новый путь создается на основе временного
 * пути, который передается как параметр.
 *
 * @param pathData Состояние, содержащее данные о пути, который нужно добавить.
 * @param pathList Список всех путей, в который будет добавлен новый путь.
 * @param tempPath Временной путь, который нужно скопировать и добавить в список.
 */
private fun addPath(
    // Состояние, содержащее данные о пути, который нужно добавить
    pathData: MutableState<PathData>,

    // Список всех путей, в который будет добавлен новый путь
    pathList: MutableList<PathData>,

    // Временной путь, который нужно скопировать и добавить в список
    tempPath: Path
) {
    // Добавляем в список новый путь, копируя текущее состояние pathData и обновляя его полем path
    pathList.add(pathData.value.copy(path = tempPath))
}

/**
 * Функция для сохранения изображения в галерее устройства.
 *
 * @param context Контекст приложения, необходимый для доступа к ContentResolver.
 * @param bitmap Изображение, которое нужно сохранить в галерее.
 */
private fun saveImageToGallery(context: Context, bitmap: Bitmap) {
    // Запускаем корутину в фоновом потоке для выполнения операций, не блокируя основной поток
    CoroutineScope(Dispatchers.IO).launch {
        // Создаем объект ContentValues для хранения метаданных изображения перед его сохранением
        val values = ContentValues().apply {
            // Указываем имя файла для сохраненного изображения, добавляя текущее время для уникальности
            put(MediaStore.Images.Media.DISPLAY_NAME, "canvas_image_${System.currentTimeMillis()}.png")
            // Указываем MIME-тип изображения, чтобы сообщить системе, что это PNG
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            // Указываем относительный путь, в который будет сохранено изображение
            // Используем Environment.DIRECTORY_DCIM для совместимости с Android 10+
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }

        // Получаем URI для сохранения изображения, используя ContentResolver
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        // Логируем полученный URI для отладки
        Log.d("MyLog", "URI: $uri")

        // Проверяем, что URI был успешно получен, и если да, то продолжаем
        uri?.let {
            // Открываем поток вывода для записи изображения в полученный URI
            context.contentResolver.openOutputStream(it).use { outputStream ->
                // Проверяем, что поток вывода был успешно открыт
                if (outputStream != null) {
                    // Сжимаем изображение в формате JPEG и записываем его в поток с качеством 100 (максимальное)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    // Логируем успешное сохранение изображения
                    Log.d("MyLog", "Изображение успешно сохранено.")
                } else {
                    // Логируем ошибку, если не удалось получить поток вывода
                    Log.e("MyLog", "Ошибка при получении OutputStream.")
                }
            }
        } ?: // Если URI не был получен, логируем ошибку
        Log.e("MyLog", "Не удалось получить URI для сохранения.")
    }
}

/**
 * Функция для создания битмапа (Bitmap) из списка путей и фона.
 *
 * @param pathList Список путей (PathData), которые будут нарисованы на битмапе.
 * @param imageBitmap Фоновое изображение (ImageBitmap), которое будет наложено на битмап (может быть null).
 * @param width Ширина создаваемого битмапа, по умолчанию 1080 пикселей.
 * @param height Высота создаваемого битмапа, по умолчанию 1920 пикселей.
 * @return Созданный битмап (Bitmap) с нарисованными путями и фоном.
 */
private fun createBitmapFromPaths(
    pathList: List<PathData>, // Список данных путей, которые нужно нарисовать
    imageBitmap: ImageBitmap?, // Фоновое изображение, если есть
    width: Int = 1080, // Ширина битмапа
    height: Int = 1920 // Высота битмапа
): Bitmap {
    val size = Size(width.toFloat(), height.toFloat()) // Создаем размер для битмапа

    // Используем drawToBitmap для выполнения команд рисования
    val imageBitmap = drawToBitmap(size) {
        // Заливаем белым цветом фон для битмапа
        drawRect(Color.White, topLeft = Offset(0f, 0f), size = size)

        // Рисуем фоновое изображение, если оно загружено
        imageBitmap?.let {
            drawImage(it, topLeft = Offset(0f, 0f)) // Накладываем фоновое изображение на битмап
        }

        // Проходим по каждому пути из списка и рисуем его на битмапе
        pathList.forEach { pathData ->
            drawPath(
                pathData.path, // Путь для рисования
                color = pathData.color, // Цвет линии
                style = Stroke( // Стиль рисования линии
                    pathData.lineWidth, // Ширина линии
                    cap = StrokeCap.Round, // Закругление концов линии
                    join = StrokeJoin.Round // Закругление соединений линий
                )
            )
        }
    }
    return imageBitmap.asAndroidBitmap() // Преобразуем ImageBitmap в android.graphics.Bitmap и возвращаем
}

/**
 * Функция для рисования в битмап (ImageBitmap).
 *
 * @param size Размер битмапа, по умолчанию 400x400 пикселей.
 * @param drawCommands Лямбда-функция, содержащая команды для рисования на Canvas.
 * @return Созданный битмап (ImageBitmap) с отрисованными командами.
 */
fun drawToBitmap(
    size: Size = Size(400f, 400f), // Позволяет задать произвольный размер для битмапа
    drawCommands: DrawScope.() -> Unit // Лямбда для пользовательских команд рисования на Canvas
): ImageBitmap {
    val drawScope = CanvasDrawScope() // Создаем область рисования (DrawScope)
    val bitmap = ImageBitmap(size.width.toInt(), size.height.toInt()) // Инициализируем новый ImageBitmap с заданными размерами
    val canvas = Canvas(bitmap) // Создаем Canvas для рисования на созданном битмапе

    drawScope.draw(
        density = Density(1f), // Устанавливаем плотность экрана для рисования
        layoutDirection = LayoutDirection.Ltr, // Устанавливаем направление макета слева направо
        canvas = canvas, // Передаем созданный Canvas для рисования
        size = size, // Передаем размер для рисования
    ) {
        drawCommands() // Выполняем переданные пользователем команды рисования
    }
    return bitmap // Возвращаем созданный битмап с нарисованными элементами
}


