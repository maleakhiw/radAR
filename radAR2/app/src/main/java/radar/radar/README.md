# Package radar.radar
Java source code for the Android application.

## Subdirectories
- Adapters: Adapters for RecyclerViews, or adapting interfaces
- Fragments: fragments, or reusable parts of Activities
- Listeners: interfaces for accepting a certain set of objects, for the Listener design pattern
- Models: data models, common within our application's objects
- Presenters: Presenters in MVP (Model-View-Presenter) which handle presentation logic (described in the corresponding README)
- Views: Views in MVP. The notion of Views in MVP differs from Views in MVC - in MVC, the layout is considered the view, with the Activity considered as the Controller; in MVP, we now consider the Activity as part of the View. In this case, this directory/package contains a set of interfaces that Presenters using said View use to communicate with it, abstracting away Android-specific classes, and UI logic and code.
