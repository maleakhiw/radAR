# Listeners
Interfaces to follow the Listener design pattern. Keep an instance of objects implementing these interfaces and they will be able to handle messages sent to them. Used for hooking into events (lifecycle or otherwise) and inter-fragment communication.

- `CameraDetailsListener`: an object or Activity implementing this interface accepts `CameraData` objects, containing details about the device's active camera for use in coordinate transformations
- `GroupDetailsLifecycleListener`: a nice abstraction for Fragments to be able to notify when they are done setting up. Needs renaming to something more general.
- `LocationCallbackProvider`: implemented by Activities, required since Google Maps API has some weird side effects when location callbacks are not constructed in the Activity.
- `LocationUpdateListener`: used for inter-fragment communication: lets the Fragment pass the LocationUpdate to the Activity.
