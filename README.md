# ZYRANTO Android (Stage 3: UI + catalogue layer)

## Build debug APK
1. Install Android Studio (Koala or newer; JDK 17 is bundled).
2. File > Open > select this ZYRANTO folder. Wait for Gradle sync (needs internet).
3. Build > Build Bundle(s)/APK(s) > Build APK(s).
   Output: app/build/outputs/apk/debug/app-debug.apk

## Modes (app/build.gradle.kts)
- DEMO_MODE = true  : built-in sample products (design preview). Orders are NOT created.
- DEMO_MODE = false : loads products live from
  https://zyranto.wordpress.com/wp-json/wc/store/v1/products
  (needs WooCommerce active on a WordPress plan that allows it).

## Before release
- Set WHATSAPP_NUMBER / SUPPORT_EMAIL in data/Models.kt.
- Build > Generate Signed Bundle/APK > APK > create a keystore (keep it private).

## Not done yet
Login/Register, real order creation, order history, push notifications (FCM),
Room cache, admin dashboard, bKash/Nagad: these need the secure backend/plugin (Stage 4+).
