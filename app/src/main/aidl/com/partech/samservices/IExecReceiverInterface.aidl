package com.partech.samservices;

interface IExecReceiverInterface {


     /**
      * Returns a version code that describes this interface.
      * @return the version code 
      */
     int getVersionCode();


      /**
       * Set the time with the time from millisec_epoch
       * @param millis the time in millis from epoch
       * @param the return value based on the command or -1 if there is an error
       */
       int setTime(long millis);

      /**
       * Execute the command with the provided arguments.
       * @param cmd the command to execute
       * @param args the arguments to the command
       * @param the return value based on the command or -1 if there is an error
       */
       int execute(String cmd, String args);

      /**
       * Get the interface mac address for the provided interface name.
       * @param ifacename the interface name
       * @return the mac address as a byte[] or an empty array if no
       * mac address is located.
       **/
       String getMacAddress(String ifacename);

}
