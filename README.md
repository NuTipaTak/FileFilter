# FileFilter
тестовое задание
## Описание
Эта утилита позволяет фильтровать содержимое текстовых файлов, разделяя целые числа, вещественные числа и строки в разные выходные файлы.

Версия java 23
система сборки Apache Maven 3.9.8 (36645f6c9b5079805ea5009217e36f2cffd34256)
сторинние библиотеки не используются 

Сборка
Для сборки проекта используйте Maven:
```bash
mvn clean package

Инструкция по запуску:

Используйте следующую команду для запуска программы: java -jar {путь к билду}\FileFilter-1.0-SNAPSHOT.jar [опции] <файлы>
Опции:
-o <путь>: Указывает директорию для выходных файлов (по умолчанию текущая).
-p <префикс>: Указывает префикс для имен выходных файлов.
-a: Добавляет данные в существующие файлы вместо перезаписи.
-s: Выводит краткую статистику.
-f: Выводит полную статистику.