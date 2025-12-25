# ============================================================================
# DOCKERFILE - ML SERVICE
# Imagen Docker para el servicio de Machine Learning
# ============================================================================

FROM python:3.11-slim

# Establecer directorio de trabajo
WORKDIR /app

# Copiar requirements primero para aprovechar cache de Docker
COPY requirements.txt .

# Instalar dependencias
RUN pip install --no-cache-dir -r requirements.txt

# Copiar código fuente
COPY . .

# Exponer puerto del servicio
EXPOSE 8001

# Comando de inicio
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8001"]
