# Presenters
Objects taking on the role of Presenter in the MVP (model-view-presenter) pattern. A Presenter is an object concerned with presentation logic (the logic of how to show things in the UI). It communicates with the Model to retrieve and update information (here, SharedPreferences and Services take on this role), and the View ("dumb", with methods only for displaying/retrieving information and relaying events - user interaction, activity lifecycle, etc.) to the Presenter.

The Presenter simply reacts to the information. Validation logic should be in the Model, however, some of our Presenters still contain some validation logic.

This is part of maintaining a clean layered architecture - objects on a higher layer only depend on objects on a layer below it. With this, a View only depends on the Presenter, Android and UI-related classes, not the underlying models, while the Presenter does not need to rely on any Android-specific/UI classes. This notion simplifies testing (as dependencies can be injected in the constructor or via methods in the Presenter), cleans up the Activities (which can can get bloated) (by letting the Activity only contain view/display/retrieval-related logic only and separating the presentation logic).

Each Activity or Fragment has one counterpart Presenter - they are in a 1-to-1 relationship, with the View keeping an instance of the Presenter and vice-versa.
