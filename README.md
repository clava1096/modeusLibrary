# Modeus library
This library is designed to obtain data about the schedule (and not only it) of Tyumen State University students in JSON format. Used in a mobile application as a third-party library.

Written using gson and asynchttpclient.

First, use the login() functions to get a bearer token.

Once you get the bearer token, use the Modeus class constructor to get the PersonID. Then use the getMyTimetable() function to get the schedule for a specific time period.

```
Credentails:
login(password, email) - get bearear token.

Modeus:
getMyTimetable(Time start, Time end, Boolean thisweek) - get timetable.
```
