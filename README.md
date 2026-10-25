# TinyPrice (IATinyFish)

Práctica: app Android que busca productos en la web en tiempo real, sin exponer la API key del proveedor.

**Flujo:** Android (Kotlin + Compose) → Supabase Edge Function → TinyFish Search → Android

## Qué se hizo

- **App Android** con Jetpack Compose, ViewModel y `StateFlow`: pantalla de inicio, carga, resultados, error y detalle de cada oferta.
- **Capa de datos** con un repositorio intercambiable: datos de demostración o búsqueda real (Retrofit + Gson).
- **Backend** en Supabase Cloud: la Edge Function `search-products` (TypeScript/Deno) valida la consulta, llama a TinyFish y devuelve ofertas con su enlace de origen.
- **Seguridad:** la API key de TinyFish vive solo como secreto en Supabase (`TINYFISH_API_KEY`). No está en el código ni en el APK.
- La búsqueda está orientada a Ecuador (`location=EC`, `language=es`).

> TinyFish Search devuelve título, enlace y fragmento, no precios normalizados. Por eso el precio muestra "Ver en fuente".

## Estructura

```
app/src/main/java/com/apps/iatinyfish/
├── MainActivity.kt
├── data/{model, remote, repository}
├── presentation/deals/   (DealsApp.kt, DealsViewModel.kt)
└── ui/theme/
supabase/functions/search-products/index.ts
```

## Cómo clonarlo y ejecutarlo

### 1. Clonar
```
git clone https://github.com/GracielaCagua/IATinyFish.git
cd IATinyFish
```

### 2. Abrir en Android Studio
- Usa Android Studio **Quail 3 (2026.1.3)** o superior, con **JDK 17+** como Gradle JDK.
- Versiones del proyecto: AGP 9.3.0, Gradle 9.5.0, Kotlin 2.3.21, compileSdk 37.
- Abre la carpeta, espera el Sync y conecta un teléfono o emulador (API 23+).

### 3. Modo demostración (sin backend)
En `gradle.properties` deja `useRemoteDeals=false` y pulsa Run. Verás datos de ejemplo.

### 4. Modo real (con tu propio backend)
1. Crea una cuenta en [TinyFish](https://agent.tinyfish.ai/) y genera una API key.
2. Crea un proyecto en [Supabase](https://supabase.com/dashboard) y copia su Reference ID.
3. En `gradle.properties` pon tu URL y activa el modo real:
   ```
   edgeRuntimeBaseUrl=https://TU_REF.supabase.co/functions/v1
   useRemoteDeals=true
   ```
4. En una terminal, dentro del proyecto:
   ```
   npm install
   npx supabase login
   npx supabase link --project-ref TU_REF
   npx supabase secrets set TINYFISH_API_KEY=tu_clave
   npx supabase functions deploy search-products
   ```
5. Sincroniza Gradle y ejecuta la app.

## Notas

- `verify_jwt = false`: el endpoint es público. Es válido para esta práctica, pero no para producción (faltaría autenticación y límites de uso).
- Nunca subas la API key al repositorio.