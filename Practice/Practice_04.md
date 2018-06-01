# ITDC-DROID
Самостоятельная работа №1 (практическая работа №4)
==================================================

## Организация работ

Рекомендуется выделить отдельную директорию для всех практических работ и задач.

![Example](http://i.imgur.com/PwfJYir.jpg)

## Задание №1: Калькулятор

Создайте полноценное приложение калькулятор. Программа должна содержать цифровой экран,
кнопки набора чисел, кнопки математических операций (`+`, `-`, `*`, `/`, `MOD`, `+/-`),
кнопки управления. Приложение должно имитировать работу физического карманного калькулятора.
Приложение должно работать с числами большой размерности. Для этого используйте специализированные
классы из библиотеки Java (начните с простого класса `BigInteger`, после перейдите на класс `BigDecimal`).

![Casio Calculator](https://i.imgur.com/nx2zDbZ.png)

Виджеты пользовательского интерфейса, которые Вам необходимо использовать

* `EditText` или `TextView`
* `Button`

Используйте `TableLayout` для размещения виджетов пользовательского интерфейса.

Проверяйте попытки поделить на ноль. В случае ошибки, покажите всплывающее сообщение при
помощи класса `Toast`.

Приложение должно продолжать работать при изменении конфигурации (например, повороте экрана).

Приложение должно сохранять и восстанавливать предыдущее значение на экране и состояние
вычислительного процесса при перезапуске всего приложения. Используйте класс `SharedPreferences` для
реализации этого.

Воспользуйтесь файлами тем, для задания разных стилевых вариаций кнопок.

## Чтение

### Документация Android

* [App Fundamentals](http://developer.android.com/guide/components/fundamentals.html)
* [UI Overview](http://developer.android.com/guide/topics/ui/overview.html)
* [Input Controls](http://developer.android.com/guide/topics/ui/controls.html)
* [Input Events](http://developer.android.com/guide/topics/ui/ui-events.html)
* [Toasts](http://developer.android.com/guide/topics/ui/notifiers/toasts.html)
* [TableLayout](http://developer.android.com/reference/android/widget/TableLayout.html)
* [Styles and Themes](http://developer.android.com/guide/topics/ui/themes.html)

### Документация Java

* [Stack](https://docs.oracle.com/javase/7/docs/api/java/util/Stack.html)
* [BigInteger](https://docs.oracle.com/javase/7/docs/api/java/math/BigInteger.html)
* [BigDecimal](https://docs.oracle.com/javase/7/docs/api/java/math/BigDecimal.html)
