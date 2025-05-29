from PIL import Image

for i in range(8, 9):
    img_rgb = Image.open(f"./secret/boca.bmp")

    # Convertir a modo "L" (escala de grises, 8 bits por píxel)
    img_gray_8bit = img_rgb.convert("L")

    # Redimensionar la imagen a 450x300
    img_resized = img_gray_8bit.resize((450, 300), Image.LANCZOS)

    # Guardar la imagen redimensionada
    img_resized.save(f"./secret/boca450.bmp")
