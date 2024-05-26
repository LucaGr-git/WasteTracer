package app;

/**
 * @author Luca Grosso, 2024
 * @author Joe Czerniecki, 2024
 * Activity class for the table that connects to the TakesPartIn table in the foodloss database
 */

public class Activity {
   // Activity
   private String activity;


   /**
    * Create an activity and set the field
    */
   public Activity(String activity) {
      this.activity = activity;
   }

   public String getActivity() {
      return this.activity;
   }

}
