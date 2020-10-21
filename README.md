# Transformer project

This is an assessment of the interview.

## Assumptions

I have made some assumptions in different parts to help myself defined the boundary of this project.

###  Application

1. The app is the only way for the user to access the remote/local databases. That is, there is no other way to delete/create/update either transformers or AllSpark.
2. The token(AllSpark) is never going to expire.
3. The token will be automatically created for the first time and it cannot be manually assigned. As a consequence, the user always gets a fresh token with no transformer in the records when they gained it.
4. The transformer must have a non-blank name.
5. The id attribute given by the API of a transformer is always unique.
6. The network traffic is trusted.

### Battle Rule
1. A ran away opponent is considered as a loser thus will be eliminated.
2. The team is sorted by descending rank. If the rank of transformers is equal, then they will be sorted by its unique id alphabetically. The id is always unique and thus the order is stable.
3. In the special rules, only the transformer called Optimus Prime or Predaking will be applied to special rule regardless of its team, and it is Case-Sensitive.
4. In the special rules, if the Optimus Prime or Predaking face each other (or a duplicate of each other), the game immediately ends with all competitors destroyed. The competitors here include all previous competitors(victor, ran away). And the result of the game is tied under this circumstance.

## Architecture
There are several modules in this app (Repository + MVVM patterns). ![Flow](https://developer.android.com/topic/libraries/architecture/images/final-architecture.png)

1. Fragment/Activity
	All Fragments and Activities are just helping a user to view the transformers or to provide actions such as create, edit, delete and fight.

2. ViewModels
	ViewModels are the places to keep data(LiveData) for views(Fragment/Activity) to be observed. They are also the triggers to invoke a flow like creating "AllSpark" or delete transformers. ViewModel help data to survive configuration changes. And it is also testable.

3. Repository
	The very heart of the project. It is a singleton so that "Single Source of Truth" can be achieved. From the ViewModels perspective, the repository provides all features to manipulate the transformers without known the detail beneath. Inside, the repo will request data from API and persist to it into the local data source, and it is testable.

4. Remote Data Source(API)
	The database of transformers, all the actions of transformers will goes here and it will respond to the result of the actions. It is implemented by Retrofit and Okhttp based on the API definition in the assessment. It is tested by using an in-memory mock server.

5. Model(Local Data Source)
	This is the local database to provide a better user experience and to minimize the network traffic. It will persist data from the remote data source or the user input, and the repository can use it to restore the state after app restart. It is implemented by Room and can be tested by the "Android Test" instead of the unit test.

6. POJO
	There are some classes, such as `Fight`, `Game`, `FightResult`, `GameResult`, that implement **Bussines logic(battle rules)**. It is developed by using **TDD** and it can provide concrete features. Of course, it is testable.

### Library

1. Coroutine
	Handle async task
2. Android Architecture Component
	For MVVM pattern
3. Room
	Persist transformer data locally
4. Retrofit
	Create a RESTful API
5. Glide
	Handle images of the team icon
6. Koin
	Dependency injection
7. Mockk
	Mock object for testing
8. Truth
	The  utility of assertion in testing

## How I did it?

I want to build a testable project, with each module well separately. After reviewing the requirements, I found they are two parts of this assessment, one is CRUD for the transformers and the other is wage war. And the latter part relies on the former.

The following steps are how I build-up this project:

1. Clarify the ambiguous definition and made some assumptions.
2. Defined `Transformer` data class because I am going to use it through the entire project.
3. Adding tests to `Transformer`.
4. Implement `TransformerRepository` with `Transformer`. The design of `TransformerRepository` is based on the assumptions and use case.
5. Because of the `RemoteDataSource` and `LocalDataSource` are not implemented yet so I mock it for the tests.
6. Implement `RemoteDataSource` and adding test with mock server
7. Implement `LocalDataSource` and adding Android Test.
8. Implement `EditTransformerViewModel` and its view for creating and edit transformers. Tests are also included.
9. Implement `ListTransformerViewModel` and its view for display/delete transformers and test.
10. Run the app on the real device(Android 11) and emulator(4.4) to perform a real test.
11. After a couple of debugging and refactoring. The app now can manipulate transformers through UI.
12. Implement `Fight` and `Game` data class for the next feature. They are done by TDD.
13. Integrated to the `ListTransformerViewModel` and also add some dialogue to display the result of the war.
14. Test it on real devices.
15. Writing README.md
16. All done.