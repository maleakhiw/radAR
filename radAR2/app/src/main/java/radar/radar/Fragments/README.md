# Fragments
As its name implies, fragments are "sub-activities": parts of the activities which can be shared or re-used between Activities. In this case we use fragments to enable a multi-tab interface in some of our UIs.

[Fragments on developer.android.com](https://developer.android.com/guide/components/fragments.html)

## List of fragments
### GroupDetailActivity
- GroupDetailsFragment: displays the meeting point and list of members; allows adding new members, setting meeting point and invoking navigation.
- GroupLocationsFragment: displays the locations of the group members when tracking is enabled (here). Also allows invoking the AR view.

### Search
- SearchUserFragment: part of the SearchActivity that lets the user search for other users registered in the system.
- PendingRequestsFragment: the 2nd tab in SearchActivity which displays any pending Friend Requests yet to be responded to (accepted/declined).
