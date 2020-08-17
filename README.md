# Profiler

This is an app that allows the user to create profiles and create records for every profile.<br/>
The app uses SQLite databases to store the profiles and their records (2 tables One-To-Many).<br/>

### Remarks
  - Notifications (mainly birthday notifications) require Build.VERSION_CODES.N
  - Personal Profile/Records are stored in seperate tables (total number of tables: 4)
  
## Undone Work
  - Records containing ViewPager (multiple images) need to be changed into horizontal RecyclerViews.
  - If New-Posts-Notification is checked in the SettingsActivity, make onCreate() of the main activity (AllDataActivity) loop on profiles, <br/>
    take every social media link (Facebook mainly) and check if the profile has new posts from last check (threading). If true, notify the user.
 
## Probability of Further Coding
Low.
