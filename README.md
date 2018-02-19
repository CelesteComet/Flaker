## Flaker

### Background and Overview
Everyone has experienced the frustration of their friends all arriving at different times to a planned event. Flaker attempts to mitigate the problem by providing the routes and ETAs of their friend group. In addition, a filterable tardiness leaderboard is kept, so you can shame your flakiest friend for his perpetual lateness.

### Functionality & MVP
- [ ] User can set a destination and send a location request to other users.
- [ ] When a user receives a location request, they choose their mode of transportation, and their estimated time of travel to the destination will be calculated.
- [ ] Every user that belongs to a request will be able to see every other user’s route and estimated time of travel to the location.
- [ ] Users who did not make it to the destination in time will be a Flaker and their points will be calculated.
- [ ] Leaderboard filterable by friend group


### Technologies & Technical Challenges
Backend: Firebase
Frontend: Android/Java

Tracking Users
With the use of Firebase’s realtime database users who have agreed to be tracked will have their location continuously updated, stored and shared with the rest of the group.

Friend Groups
** Creating friends list **

Meetup spots

UX
*Frontend
    * Using Google Maps API, a map will be displayed in the view. There is also a search bar to search for a destination

*Backend
    * A user’s coordinate will be updated through Google Maps API and synced with a reference to the database, which will be synced with the other users' app



### Accomplished over the Weekend
* Researched Firebase documentation and tutorials
* Connect app to Firebase
* Setup basic app with Google Maps
* Began basic app navigation

### Group Members & Work Breakdown
** Joey Feng, Alex Kite, Rose Lee, Bruce Wong **

#### Day 1
* Assess feasibility of project
* Design database schema - **JOEY, ROSE**
* Finish user authentication tutorials - **JOEY, ROSE**
* Finish basic app tab navigation - **BRUCE**
* Login and User page views - **Alex**

#### Day 2
* Implement user authentication - **JOEY, ROSE, Alex** 
* Integrate profile views with backend **Alex**
* Leaderboard - ** Alex **

#### Day 3
* Test syncing of coordinate response from Google Maps API to database - **JOEY, ROSE**
* Begin implementation of real-time location tracking **All**

#### Day 4
* Continue implementation of real-time location tracking. **All**

#### Day 5
* Implement friends and friend lists.
	[ ] User can search for friends 
	[ ] User can send a friend request 
	[ ] User can add friends who accepted their friend request
	[ ] When a user removes a friend, the link between the user and the friend is broken.

#### Day 6
* Implement Flaker Leadership board
	[ ] Database should fetch friends and sort them by flakiness and then display it in a
	    performant recycler view. 
* Polish Application loose ends.
