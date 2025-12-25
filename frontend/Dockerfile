# ============================================================================
# DOCKERFILE - FRONTEND
# Servidor Nginx para servir archivos estáticos
# ============================================================================

FROM nginx:alpine

# Copiar archivos del frontend
COPY index.html /usr/share/nginx/html/
COPY styles.css /usr/share/nginx/html/
COPY app.js /usr/share/nginx/html/

# Copiar configuración personalizada de Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponer puerto
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost/ || exit 1

# Nginx se inicia automáticamente
