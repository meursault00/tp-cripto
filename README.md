# Secreto Compartido en Imágenes con Esteganografía

**Trabajo Práctico de Implementación para la materia Criptografía y Seguridad – ITBA**

**Autores:** Iñaki Bengolea, Ignacio Bruzone, Félix López Menardi

---

Este proyecto consiste en la implementación en Java del esquema de Secreto Compartido propuesto por Wu & Lo, en un modelo (k, n), con esteganografía basada en LSB (Least Significant Bit) sobre imágenes BMP de 8 bits por píxel.

El objetivo es permitir:
- La **distribución** de una imagen secreta en `n` imágenes portadoras (`sombras`) tal que con cualquier `k` de ellas se pueda reconstruir la imagen original.
- La **recuperación** de la imagen original a partir de `k` sombras.

## Requisitos
- Imágenes BMP de 8 bits por píxel, sin compresión.
- Todas las portadoras deben tener el mismo tamaño.
- Java 17+ (se utilizó Java 21 para desarrollo).

## Uso

### Compilar y correr
```bash
./gradlew build
./gradlew run --args="-h"
```

### Ejemplo de Distribución
```bash
./gradlew run --args="-d -secret examples/secret/boca.bmp -k 8 -n 8 -dir examples/portadoras/300x300"
```

### Ejemplo de Recuperación
```bash
./gradlew run --args="-r -secret examples/secret/recuperada.bmp -k 8 -dir examples/portadoras/300x300"
```

### Consideraciones importantes
- Las imágenes portadoras utilizadas en la recuperación deben estar nombradas como:
```
sombra1.bmp, sombra2.bmp, ..., sombraK.bmp
```
dentro del directorio indicado con -dir.

- En caso de usar un valor de k < 8, se generará una mayor cantidad de bloques, lo que implica más sombras a ocultar. Por eso, es responsabilidad del usuario asegurarse de que las imágenes portadoras tengan tamaño suficiente para almacenar las sombras generadas.
- Si el tamaño de las portadoras no es suficiente, el programa emitirá un error y no procederá.

## Créditos
Este trabajo se basa en el paper:
- Luang-Shyr Wu and Tsung-Ming Lo, “An Efficient Secret Image Sharing Scheme” Journal of Systems and Software, 2007.