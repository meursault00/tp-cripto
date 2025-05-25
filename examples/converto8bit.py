from PIL import Image

# Abrir la imagen (aunque esté en RGB, pero en grises)(f"./portadoras/c{i}.bmp")
for i in range(0,3):
    img_rgb = Image.open(f"./portadoras/c{i}.bmp")

# Convertir a modo "L" (luminancia, 8 bits por píxel)
    img_gray_8bit = img_rgb.convert("L")

# Guardar la imagen resultante
    img_gray_8bit.save(f"./portadoras/c{i}.bmp")
