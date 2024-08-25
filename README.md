First, use the login() functions to get a bearer token

Once you get the bearer token, use the Modeus class constructor to get the PersonID. Then use the getMyTimetable() function to get the schedule for a specific time period.

#Classes:
##Credentails:
login(String password, String email)

##Modeus:
getMyTimetable(Time start, Time end, Boolean thisweek)
