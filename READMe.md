<p align="center"><img src="https://raw.githubusercontent.com/jbeguna04/Baking-App/master/logodesign/logotype1.1brown.png"></p>

# Baking Recipes
This app has been created during a Google Scholarhip of Android Developer Nanodegree Program on Udacity. It is the third app we created, and the main purpose is to get familiar with fragments, testing and creating widgets. As data, there is a json file in assets' folder that is being used.

The app has adaptive ui, depending on if you run it on mobile or tablet. The Master / Detail Flow is being used for this reason.

Also, the widget created for the app dispays the ingredients of a selected recipe. The recipe is selected by the user when creating the widget through a configuration screen. When the user clickes the header of the widget the recipe details activity of the corresponding recipe opens up.

There are two classes of testing, consist of 8 testings, checking core functionality of the app, for instance, if the recycler view works as expected and when an item is clicked opens the corresponding activity. Also if an activity gets and loads the corresponding info from an intent extras correctly.

## Screenshots (on phone)

<img src="https://github.com/NasiaKouts/Baking-App/blob/master/assets/recording.gif" width="280"> |
<img src="https://github.com/NasiaKouts/Baking-App/blob/master/assets/mainMenu.png" width="280"> |
<img src="https://github.com/NasiaKouts/Baking-App/blob/master/assets/recipeDetails.png" width="280"> | 
<img src="https://github.com/NasiaKouts/Baking-App/blob/master/assets/ingredients.png" width="280"> |
<img src="https://github.com/NasiaKouts/Baking-App/blob/master/assets/videoLoading.png" width="280"> |
<img src="https://github.com/NasiaKouts/Baking-App/blob/master/assets/videoPlaying.png" width="280"> |
<img src="https://github.com/NasiaKouts/Baking-App/blob/master/assets/costumizeWidget.png" width="280"> |
<img src="https://github.com/NasiaKouts/Baking-App/blob/master/assets/widget.png" width="280"> 

## Screenshots (on tablet)
to be added

### Libraries used:
- Butterknife
- Exoplayer
- Picasso
- Gson
- Espresso
- RoundedImageView
- TriangleLabelView

### Attribution
Icons made by https://www.flaticon.com/authors/freepik 
and https://www.flaticon.com/authors/those-icons from www.flaticon.com

### Notes
- I decided to use default icon as image of the recipe in case there is no image available. On the other side, i decided that in case there is no thumnail available, not to show anything and just wait the video to be loaded.
- In case a recipe step doesn't containt any video, then the media player visibility is set to gone.
- The app request audio focus and at the same time "release" focus when other app is requesting it.
- When the video start playing and suddenly the internet connection get losts, the player doesn't stop immediately, but waits a little bit in order to continue playing if the connection restores whithin this reasonable time - waiting. If it is not, then a toast, message and retry button will appear informing the user about the issue.
- Lastly, in recipe details i do not use recycler view cause I don't think it would provide any benefit at all. There are only three "views" displaying:
    - The video player
    - The step's description (max 3 sentences)
    - The bottom navigation buttons (previous and next step)
    
    Of these 3 views, I want the video player and the bottom navigation buttons to be always, no matter what visible. Also there is no way that the context wont fit the screen. However to make sure that the user can read the description in any case, I sourounded the description's TextView with NestedScrollView (except the phone landscape mode that the video is either fullscreen either the textview is all alone so there is no fear of not fitting the screen.) 
    Relevant post on forum: https://discussions.udacity.com/t/rubric-clarification-on-recyclerview/249025/18 (conclution given by mentor forum @Alex_234)
