# Project GlobalWaves

## Skel Structure

* src/
  * checker/ - checker files
  * fileio/ - contains classes used to read data from the json files
  * main/
      * Main - the Main class runs the checker on your implementation. Add the entry point to your implementation in it. Run Main to test your implementation from the IDE or from command line.
      * Test - run the main method from Test class with the name of the input file from the command line and the result will be written
        to the out.txt file. Thus, you can compare this result with ref.
* input/ - contains the tests and library in JSON format
* ref/ - contains all reference output for the tests in JSON format

# First Stage of the Project

## Components of the Solution

### Library Module:
Serves as the core component where general-purpose functions are housed. These functions are constant and are related to input processing. The module is called only once, reinforcing the singleton pattern.

### Types Package: 
Contains general functions used throughout the application. This package plays a crucial role in maintaining reusable and modular code.

### User Handling: 
In the Library module, we process users and convert each into a Hashmap. This approach, utilizing the hashmap function, enables efficient identification and handling of users based on their names. We implement a form of isolated singleton for each user.

### Database Class: 
For each user, we have a database class responsible for storing their commands and interactions with the application.

### Input Conversion Function: 
A specialized function is designed to convert JSON test inputs into corresponding classes, facilitating easier handling of data.

### SearchFunction: 
A specialized function is designed to search for songs, podcasts, and playlists based on the user input.
Command functions such as play, pause, next, previous, forward, backward, repeat, shuffle, like, create, add, remove, and follow are implemented in the their own classes.

## Flow Between Components

### Initialization: 

The Library is initiated as a singleton, loading all necessary functions, such as song lists, podcasts, and playlists, users, 
and a hashmap for each user. The hashmap is used to store the database for each user. In Library, we have a function that are
accessible for all users on the outside, so each user updates it constantly, and the changes are reflected in the Library.

In Database, we have a function that is accessible only for the user, so the user can update it, and the changes are reflected in the Database.


### User Processing: 

Users are converted into Hashmaps within the Library for efficient access and management. It helped
with synchronization and data handling by each user. The user is identified by their name, which is unique.
And we can sync the information which each data.

### Command Handling: 
Based on the user input or command, the application directs the flow to the relevant classes and functions.
All these command function were initially handled in the Main class.


### JSON Integration: 
For output, JSON files are integrated, representing the test results. 
That was done using the function JsonConvertToCategories, which later helped to run the tests.
And handle the information from the JSON files.

## Encountered Problems

### Code Refactoring: 
The code was refactored multiple times to ensure modularity and re-usability. This was done to ensure that the code is correct and
the tests are passed. Initially, the database was implemented in the library module as a singleton. 
However, this approach was not feasible as we
had different users with different databases. Thus, we implemented a hashmap for each user, which stores their database.
This approach was correct.

Another problem, was with shuffle function, because I implemented the shuffle function with indexes, and I
had to update the current index, after un-shuffling and shuffling the playlist.

# Second Stage of the Project

## Components of the Solution

### Library Module:
Serves as the core component where general-purpose functions are housed. These functions are constant and are related to input processing. The module is called only once, reinforcing the singleton pattern.
It is crucial now because we have three different types of users, and we need to keep track of all of them.
And the page accessed by them.

### Types Package: 
Contains general functions used throughout the application. This package plays a crucial role in maintaining reusable and modular code.
Added artist and host, and the functions for them, and Page, which is critical for this stage.

### User Handling: 
In the Library module, we process users and convert each into a Hashmap. This approach, utilizing the hashmap function, enables efficient identification and handling of users based on their names. We implement a form of isolated singleton for each user.
This time this was implemented for normal users, artists and hosts. And I used the same approach as in the first stage.

### Database Class:
For each user, artist and host we have a database class responsible for storing their commands and interactions with the application.

### Input Conversion Function:
A specialized function is designed to convert JSON test inputs into corresponding classes, facilitating easier handling of data.

## Flow Between Components

### Initialization:
The Library is initiated as a singleton, loading all necessary functions, such as song lists, podcasts, albums and playlists, users.
And a hashmap for each user, artist and host. The hashmap is used to store the database for each user, artist and host.

We use the flyweight **design pattern**, because we have a lot of users, artists and hosts, and we need to keep track of all of them.

In Database, functions that are accessible only for the user, artist and host, so the user, artist and host can update it, and the changes are reflected in the Database.

### User Processing:

Users, artists and hosts are converted into Hashmaps within the Library for efficient access and management. It helped
with synchronization and data handling by each type of user. The types of user are identified by their name, which is unique.
And we can sync the information which each data.

### Command Handling:
Based on the user input or command, the application directs the flow to the relevant classes and functions.
All these command function were initially handled in the Main class.
I made a class called CommandRunner, where each of these commands are implemented, and I call them from the Main class.

### JSON Integration:
For output, JSON files are integrated, representing the test results.
That was done using the function JsonConvertToCategories, which later helped to run the tests.
And handle the information from the JSON files.

### Vistor Pattern:
The Visitor design pattern is utilized for the status command to 
efficiently handle the diverse presentation requirements for user, artist, 
and host statuses. This approach allows to implement distinct display logic 
for each entity type without altering their underlying classes, aligning with the
principles of extensibility and separation of concerns in object-oriented design.

## Encountered Problems

### Code Refactoring:

The refactoring of the code was undertaken multiple times to enhance
its modularity and re-usability, ensuring both correctness and successful test
passage. The initial implementation resulted in user statuses being updated upon 
access through the JSON array. However, modifications were necessary, particularly 
when deleting a user. It was crucial to confirm that the user's current (if it is deleteAlbum) 
or associated album(if it is deleteUser) was not actively loaded during deletion. To achieve this, all users were synchronized 
before executing the delete function.

Further complexity arose with the 'status' attribute inheriting from 'playerLoad'. 
Precautions were taken to prevent the 'playerLoad' operation from inadvertently modifying the 'username' and 'elements'. This was particularly important when initializing the user's 'elements name' with a test value.

To avoid such overlaps and maintain integrity, a barrier mechanism was implemented. This barrier restricted access to the 'playerLoad' function for users, only releasing (with a false value) once the necessary conditions were met. This approach ensured a robust and error-free operation.

Another implementation that had to be taken into account was the search of the songs, initially the 
search results returned by SongFilters were strings, and take into consideration only the name
of the song. But now, it has to be taken into consideration the album also, because there are 
songs with the same name, but from different albums. 

The return type of the method was altered from `List<String>` to `List<Song>`, necessitating 
a change in the approach for searching songs. Additionally, the consideration of the 
album associated with the songs was integrated into the search process. This modification
was essential to align the method's functionality with its intended purpose, ensuring more
accurate and relevant results.

### Observations:
At Event Class, the function isValidDate() and isFormatDateValid() 
were implemented with ChatGPT

I had to implement the commits in English, after having them in Romanian,
because my laboratory teacher told me that I have to do that.


# Third Stage of the Project

## Components of the Solution

### Library Module:
Serves as the core component where general-purpose functions are housed. These functions are constant and are related to input processing. The module is called only once, reinforcing the singleton **design pattern**.

It is crucial now because we have three different types of users, and we need to keep track of all of them.
And the page accessed by them.
In addition, there is a counting mechanism implemented for songs and episodes, which proves helpful when keeping track of their quantity.

### Types Package:
Contains general functions used throughout the application. This package plays a crucial role in maintaining reusable and modular code.
Added an EpisodeListenCount and SongListenCount, which are used to count the number of times a song or episode is listened to.
And helps in order to give the user or artists the songs which are listened the most.

### User Handling:
In the Library module, we process users and convert each into a Hashmap. This approach, utilizing the hashmap function, enables efficient identification and handling of users based on their names. We implement a form of isolated singleton for each user.
This time this was implemented for normal users, artists and hosts. And I used the same approach as in the first stage.

For Users and Artists, a system is developed to monitor their listening activity. This system records the duration they've spent listening to songs or episodes. However, instead of a traditional hashmap, the implementation utilizes two specific classes: SongListenCount and EpisodeListenCount. These classes are designed to tally the frequency of song or episode plays.
### Database Class:
For each user, artist and host we have a database class responsible for storing their commands and interactions with the application.

### Input Conversion Function:
A specialized function is designed to convert JSON test inputs into corresponding classes, facilitating easier handling of data.

### Design Patterns
The project uses several design patterns such as flyweight, singleton, visitor and command. The flyweight pattern is used for efficient memory usage when dealing with a large number of users, artists, and hosts. The singleton pattern is used for the `Library` class to ensure only one instance exists. The visitor pattern is used to add new operations to existing classes without modifying them.
Command pattern is used to encapsulate a request as an object, thereby letting you parameterize command with different requests.

## Flow Between Components

### Initialization:
The Library is initiated as a singleton, loading all necessary functions, such as song lists, podcasts, albums and playlists, users.
And a hashmap for each user, artist and host. The hashmap is used to store the database for each user, artist and host.
We use the flyweight design pattern, because we have a lot of users, artists and hosts, and we need to keep track of all of them.

In Database, functions that are accessible only for the user, artist and host, so the user, artist and host can update it, and the changes are reflected in the Database.

### User Processing:

Users, artists and hosts are converted into Hashmaps within the Library for efficient access and management. It helped
with synchronization and data handling by each type of user. The types of user are identified by their name, which is unique.
And we can sync the information which each data.

### Command Handling:
Based on the user input or command, the application directs the flow to the relevant classes and functions.
All these command function were initially handled in the Main class.
I made a class called CommandRunner, where each of these commands are implemented, and I call them from the Main class.

### JSON Integration:
For output, JSON files are integrated, representing the test results.
That was done using the function JsonConvertToCategories, which later helped to run the tests.
And handle the information from the JSON files.

### Vistor Pattern:
The Visitor design pattern is utilized for the status command to
efficiently handle the diverse presentation requirements for user, artist,
and host statuses. This approach allows to implement distinct display logic
for each entity type without altering their underlying classes, aligning with the
principles of extensibility and separation of concerns in object-oriented design.

### Command Pattern:
In our Main class, we have adopted the Command Pattern to manage user commands effectively. Each user command is encapsulated within an object that implements the Command interface. This interface defines a single method, execute, which takes a User object as an argument and can throw an IOException.

When a user command is received, it is transformed into a command object, and the execute method is called to carry out the specified action on the User object. This approach allows us to abstract away the details of command execution and easily extend our application by adding new command implementations that adhere to the Command interface.

By applying the Command Pattern, we achieve a more modular and maintainable design, promoting loose coupling between the sender and receiver of commands while enabling us to accommodate various user actions seamlessly.

### Lambda Expressions
Lambda expressions are used with stream operations to filter and sort data efficiently. For example, the `getTopAlbumsListened()` method in `User` uses a lambda expression to sort the Albums by their listen count.

## Encountered Problems

### Code Refactoring:
Needed to refactor the code in order to make it more modular and reusable. 

However, modifications were necessary, particularly when deleting an album. It was crucial to confirm that songs from an albums where deleted only if there were 
in the respective albums. In order to achieve this, I put a condition in order to make sure, that song album name matches the albums that needs to be deleted.

Further complexity arose with the 'album' which needed to be loaded from playerLoad, there were albums with the same name and different owners,
so it ought to be taken into consideration the owner of the album.

Modified the search, select, playerLoad and the search functions in order to ensure that, the album was searched by a function
called searchAlbumsByNameAndOwner, which takes into consideration the owner of the album.

Modified the Main Class in order to implement a Command Pattern and each command is taken by the getCommand function, and then
it is executed by the execute function.

### Observations:
I added the same commit name because I thought that the commit has been made, because I didn't had the git. file at
the beginning, so I had to add it manually.
![Screenshot 2024-01-16 201340.png](..%2F..%2F..%2FDownloads%2FScreenshot%202024-01-16%20201340.png)