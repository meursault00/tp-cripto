# Secret Image Share (TP C&S 2025)

Implementación en Java/Gradle del esquema (k,n) de Wu-Lo + esteganografía LSB
para BMP 8 bpp.

```bash
# compilar y correr
./gradlew build
./gradlew run --args="-h"


-d -secret examples\secret\boca.bmp -k 8 -n 8 -dir examples\portadoras

-r -secret examples\secret\recuperada.bmp -k 8  -dir examples\portadoras