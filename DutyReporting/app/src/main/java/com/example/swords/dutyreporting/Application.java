package com.example.swords.dutyreporting;

import com.parse.Parse;

/**
 * Created by Swords on 3/23/15.
 * I'm honestly not entirely sure why this extra class is needed but it's the only solution I could
 * find to make Parse work
 */
public class Application extends android.app.Application {

    public void onCreate() {
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "2DR7xvqsx4YcYsgiZ7HGfy5XBLF1fWudmD21ykku", "75gq1Es8M4imxD1SQHVWG1e1CqvNSlTYtNxRbk0T");
    }

}
