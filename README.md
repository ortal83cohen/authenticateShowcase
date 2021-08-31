# Description

A very small Android application which will authenticate a user (Login page) using any federated auth providers
(Firebase, Auth0, Google Apple) and provide access to the Home Page of the application. Enable biometrics to bypass the
Login screen when the user opens the app the second time.

# Time limit

This task was limited to 8 hours. some of the things I wished doing in this showcase take more than the time limit, for that reson I left a //TODO comment in the code.
Key improvments that I think this Project should habve: 
* Implement DataSource and use it as the "one source of truth".
* Implement UI Tests.
* Implement auth provider (I would go for Firebase, it's fast. easy and simple for a showcase)
* Implement Depandency injection.

# My Focus

• Structure of the code - code Architecture, readability, testability, clean and reusable.
• Code formatting, included unit tests.
• Overall user experience in the application.

# Architecture Explanation

I used mvvm with clean architecture 
<img src="images/clean arch layer.jpeg">

# The Android project used pattern

A typical android project is divided into 3 layers:
<img src="images/android representation.jpeg">

 - **Presentation Layer** (MVVM) contains the UI and ViewModel which will control the views. This UI will depend heavily on the Use Case.
 - **Domain Layer** contains Entities, Use Case, Mappers and Repository Interface. This is the most core layer and is associated with business processes.
 - **Data Layer** contains Repository Implementation and DataSource which can be Local DataSource (database) and Remote DataSource (network).

# How to run this project

No special action or knowledge needed here, just run it with your favorite IDEA.