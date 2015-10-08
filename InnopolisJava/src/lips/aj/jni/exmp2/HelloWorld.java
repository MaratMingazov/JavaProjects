package lips.aj.jni.exmp2;
public class HelloWorld 
{
      public static void main(String args[]) 
      {
         System.out.println("This is the main function in HelloWorld class");
      }
      public static void TestCall(String szArg)
      {
      	System.out.println(szArg);      
      }
      public static int DisplayStruct(ControlDetail ctrlDetail)
      {
      	System.out.println("Structure is:\n-------------------------");
      	System.out.println("Name:" + ctrlDetail.Name);
      	return 1;     	
      }
      public static void DisplayStructArray(WorkOrder ArrWO[])
      {
    	  for(int i = 0; i< ArrWO.length;i++)
    	  {
    		  System.out.println("<---Work Order Number:" + String.valueOf(i+1) + "<---");
    		  System.out.println("Sum_Serial_ID: " + ArrWO[i].sumSerialId);
    		  System.out.println("Access_Number: " + ArrWO[i].accessNumber);
    	  }	
      }
      public static Object ReturnObjFunc()
      {
      	System.out.println("Going to return an object from java");
      	ReturnData RetData = new ReturnData(1,"Successfull function call");
      	return RetData;
      }
}