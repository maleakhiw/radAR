# Views
The V in MVP (model-view-presenter). A set of interfaces for Presenters to communicate with Activites, such that the Presenters do not get coupled with the actual Android Activity and only interact with the actual View (layout) through this abstraction. This allows for easy mocking of views in testing and easily swapping between Activities with the same Presenter, as all communication is done through the common interfaces done here - decoupling the presentation logic from the actual UI/Android-specific/view logic.

These Views are all implemented by Activities in the root directory.
