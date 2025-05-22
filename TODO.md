# 📌 Roadmap & Checklist — Secret Image Share

> Proyecto de Criptografía & Seguridad (ITBA)  
> Java 17 · Gradle · BMP 8 bpp · LSB Steganography · Wu-Lo (k,n)

---

## 0. Estado General

| Módulo | Avance | Última actualización |
|--------|:------:|----------------------|
| Lectura / escritura BMP | ☐ | – |
| Permutación (seed → R)  | ☑ | 2025-05-22 |
| LSB Encoder / Decoder    | ☐ | – |
| Secret Sharing Scheme    | ☐ | – |
| CLI (`Main`)             | ☐ | – |
| Tests (JUnit)            | ☐ | – |
| Informe / README         | ☐ | – |

---

## 1. To-Do detallado

### 1.1 Núcleo BMP
- [ ] `BMPHeader.parse()` — validar firma “BM”, 8 bpp, BI_RGB.
- [ ] `BMPHeader.write()` — serializar 54 bytes, bytes 6-9 reservados.
- [ ] `BMPImage.read()` / `write()` — copy-on-write de paleta y píxeles.
- [ ] Tests de lectura con imágenes de ejemplo.

### 1.2 Permutación
- [x] Fisher–Yates reproducible (`PermutationUtil.generate`).
- [x] Inversa de permutación (`PermutationUtil.invert`).
- [ ] Test reproducibilidad (seed = 42).

### 1.3 Esquema Wu–Lo
- [ ] `SecretSharingScheme.createShadows()` (param. k=8).
- [ ] `SecretSharingScheme.recoverSecret()` (interpolación).
- [ ] `Lagrange` completo para 8 puntos mod 251.
- [ ] Tests encode+decode con arreglo sintético.

### 1.4 Esteganografía LSB
- [ ] `LSBEncoder.embed(pixels, share)`.
- [ ] `LSBDecoder.extract(pixels, len)`.
- [ ] Test ida-vuelta, distorsión ≤ 1 LSB.

### 1.5 CLI
- [ ] Parseo de flags `-d / -r / -secret / -k / -n / -dir`.
- [ ] Modo **Distribuir** → genera sombras, embed y guarda en `<dir>`.
- [ ] Modo **Recuperar** → extrae k sombras y reconstruye `<out.bmp>`.
- [ ] Mensajes de ayuda (`-h`).

### 1.6 Documentación & Entrega
- [ ] Actualizar `README.md` con requisitos y comandos.
- [ ] Redactar `DESIGN.md` / informe PDF (4-6 páginas).
- [ ] Crear scripts `run_encode.sh`, `run_decode.sh`.
- [ ] Tag `v1.0-final` y push.

---

## 2. Ideas Futuras (no obligatorias)

- [ ] Soporte **k ≠ 8** (ajustar bits LSB por píxel).
- [ ] Cifrado extra (AES-CTR) antes de embed.
- [ ] Interface gráfica (JavaFX) para demo interactiva.
- [ ] Métrica PSNR para evaluar imperceptibilidad.

---

## 3. Convenciones de Git

* **Branch `main`**: solo versiones que compilan.  
* **Branch `develop`**: merge de features semanales.  
* Commits cortos y descriptivos:  
