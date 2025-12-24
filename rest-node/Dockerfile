FROM node:20-alpine
WORKDIR /app

# Nur package Dateien zuerst (bessere Layer-Caches)
COPY package.json package-lock.json ./
RUN npm ci --omit=dev

# App-Code kopieren
COPY . .

# Express default PORT=3000
ENV NODE_ENV=production
EXPOSE 3000

CMD ["node", "./bin/www"]
