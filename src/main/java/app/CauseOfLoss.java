package app;

/**
 * @author Luca Grosso, 2024
 * @author Joe Czerniecki, 2024
 * Activity class for the table that connects to the TakesPartIn table in the foodloss database
 */

public class CauseOfLoss {
   // CauseOfLoss
   private String causeOfLoss;


   /**
    * Create an activity and set the field
    */
   public CauseOfLoss(String causeOfLoss) {
      this.causeOfLoss = causeOfLoss;
   }

   public String getCauseOfLoss() {
      return this.causeOfLoss;
   }

}
