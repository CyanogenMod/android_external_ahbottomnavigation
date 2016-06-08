## Changelog

### Newest version: 1.3.0

* **BREAKING!** Updated listener, now return a boolean => `boolean onTabSelected(int position, boolean wasSelected);`
* Improved notification management for small items
* Added notification elevation
* Managed complex drawable (selector with states)
* Added constructor `public AHBottomNavigationItem(String title, Drawable drawable)`

### 1.2.3

* Added `setUseElevation(boolean useElevation, float elevation)`
* Fixed a bug with `behaviorTranslationEnabled` & `restoreBottomNavigation`
* Improved translation behavior when the Scroll View is not long enough.

### 1.2.2

* Fixed bug when switching between normal and colored mode

### 1.2.1

* Fixed method typo `setNotificationMarginLef` => `setNotificationMarginLeft`
* Avoid multiple call for showing/hiding AHBottomNavigation

### 1.2.0

* Updated Notification: now accept String (empty String to remove the notification)
* Deprecated integer for Notification
* Removed deprecated methods & interface for `AHBottomNavigationListener`
* Fixed touch ripples when the bottom navigation is colored
* Cleaned colors.xml to avoid conflicts
* Removed constructor AHBottomNavigationItem()
* Added `setTitleTextSize(float activeSize, float inactiveSize)`
* Added `setNotificationMarginLeft(int activeMargin, int inactiveMargin)`

### 1.1.8

* Added `hideBottomNavigation(boolean withAnimation)`
* Added `restoreBottomNavigation(boolean withAnimation)`

### 1.1.7

* Added `public AHBottomNavigationItem getItem(int position)` to get a specific item
* Added `public void refresh()` to force a UI refresh

### 1.1.6

* Improved `hideBottomNavigation()` and `restoreBottomNavigation()`
* Added `setTitleTypeface`
* Changed method name `setNotificationBackgroundColorResource` by `setNotificationTypeface`
* Started working on `onSaveInstanceState` and `onRestoreInstanceState` (currentItem & notifications for now)

### 1.1.5

* Added hideBottomNavigation()
* Added CURRENT_ITEM_NONE to unselect all items
* Improved Notifications (animation, size)

### 1.1.4

* Updated lib dependencies

### 1.1.3

* Fixed Snackbar when setBehaviorTranslationEnabled(false)

### 1.1.2

* Fixed animations on pre Kit Kat
* Added an example with Vector Drawable

### 1.1.1

* Fixed layout rendering with fragments

### 1.1.0

* Compatible with Snackbar
* Compatible with Floating Action Button

### 1.0.5

* Snackbar is now compatible

### 1.0.4

* Added: setCurrentItem(int position, boolean useCallback)
* Added: setUseElevation(boolean useElevation)
* Added: restoreBottomNavigation()

### 1.0.3

* Fixed setForceTint()

### 1.0.2

* Fixed crash when setForceTitlesDisplay(true)
* Improved UI

### 1.0.1

* Bug fixes
* Notifications
* Minimum SDK version: 14

### Before

* AHBottomNavigation was under development.